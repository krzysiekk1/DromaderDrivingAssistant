package com.skobbler.sdkdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.skobbler.ngx.packages.SKPackageManager;
import com.skobbler.ngx.sdktools.download.SKToolsDownloadItem;
import com.skobbler.ngx.sdktools.download.SKToolsDownloadListener;
import com.skobbler.ngx.sdktools.download.SKToolsDownloadManager;
import com.skobbler.sdkdemo.R;
import com.skobbler.sdkdemo.application.ApplicationPreferences;
import com.skobbler.sdkdemo.application.DemoApplication;
import com.skobbler.sdkdemo.database.DownloadResource;
import com.skobbler.sdkdemo.database.MapDataParser;
import com.skobbler.sdkdemo.database.MapDownloadResource;
import com.skobbler.sdkdemo.database.MapsDAO;
import com.skobbler.sdkdemo.database.ResourcesDAOHandler;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

/**
 * Activity that displays a list of downloadable resources and provides the ability to download them
 */
public class ResourceDownloadsListActivity extends Activity {

    /**
     * Constants
     */
    public static final long KILO = 1024;

    public static final long MEGA = KILO * KILO;

    public static final long GIGA = MEGA * KILO;

    public static final long TERRA = GIGA * KILO;

    /**
     * Download manager used for controlling the download process
     */
    private SKToolsDownloadManager downloadManager;

    /**
     * Adapter for download items
     */
    private DownloadsAdapter adapter;

    /**
     * List element displaying download items
     */
    private ListView listView;

    /**
     * List of items in the current screen
     */
    private List<ListItem> currentListItems;

    /**
     * Map from resource codes to items
     */
    private Map<String, ListItem> codesMap = new HashMap<String, ListItem>();

    /**
     * List of all map resources
     */
    public static Map<String, MapDownloadResource> allMapResources;

    /**
     * List of downloads which are currently in progress
     */
    public static List<DownloadResource> activeDownloads = new ArrayList<DownloadResource>();

    /**
     * DAO object for accessing the maps database
     */
    public static MapsDAO mapsDAO;

    /**
     * Stack containing list indexes for opened screens
     */
    private Stack<Integer> previousListIndexes = new Stack<Integer>();

    /**
     * Context object
     */
    private DemoApplication appContext;

    private Map<Long, Long> downloadChunksMap = new TreeMap<Long, Long>();

    /**
     * Handler object used for scheduling periodic UI updates while downloading is in progress
     */
    private Handler handler;

    /**
     * True if download estimates should be refreshed at next UI update
     */
    private boolean refreshDownloadEstimates;

    /**
     * Timestamp at which last download started
     */
    private long downloadStartTime;

    /**
     * Item in the download list
     */
    private class ListItem implements Comparable<ListItem> {

        private String name;

        private DownloadResource downloadResource;

        private List<ListItem> children;

        private ListItem parent;

        @Override
        public int compareTo(ListItem listItem) {
            if (listItem != null && listItem.name != null && name != null) {
                return name.compareTo(listItem.name);
            }
            return 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads_list);
        appContext = (DemoApplication) getApplication();
        handler = new Handler();

        final ListItem mapResourcesItem = new ListItem();
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return initializeMapResources();
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    appContext.getAppPrefs().saveBooleanPreference(ApplicationPreferences.MAP_RESOURCES_UPDATE_NEEDED, false);
                    populateWithChildMaps(mapResourcesItem);
                    currentListItems = mapResourcesItem.children;

                    listView = (ListView) findViewById(R.id.list_view);
                    adapter = new DownloadsAdapter();
                    listView.setAdapter(adapter);
                    ResourceDownloadsListActivity.this.findViewById(R.id.cancel_all_button).setVisibility(activeDownloads.isEmpty() ? View.GONE : View.VISIBLE);
                    downloadManager = SKToolsDownloadManager.getInstance(adapter);
                    if (!activeDownloads.isEmpty() && activeDownloads.get(0).getDownloadState() == SKToolsDownloadItem.DOWNLOADING) {
                        startPeriodicUpdates();
                    }
                } else {
                    Toast.makeText(ResourceDownloadsListActivity.this, "Could not retrieve map data from the server", Toast.LENGTH_SHORT).show();
                    ResourceDownloadsListActivity.this.finish();
                }
            }
        }.execute();
    }

    /**
     * Runnable used to trigger UI updates that refresh the download estimates (for current speed and remaining time)
     */
    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            refreshDownloadEstimates = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
            handler.postDelayed(this, 1000);
        }
    };

    /**
     * Starte periodic UI updates
     */
    private void startPeriodicUpdates() {
        downloadStartTime = System.currentTimeMillis();
        handler.postDelayed(updater, 3000);
    }

    /**
     * Stops the periodic UI updates
     */
    private void stopPeriodicUpdates() {
        downloadChunksMap.clear();
        handler.removeCallbacks(updater);
    }

    /**
     * Initializes the map resources (reads them from the database if they are available there or parses them otherwise and stores them in the database)
     */
    private boolean initializeMapResources() {
        mapsDAO = ResourcesDAOHandler.getInstance(this).getMapsDAO();

        if (allMapResources == null || appContext.getAppPrefs().getBooleanPreference(ApplicationPreferences.MAP_RESOURCES_UPDATE_NEEDED)) {

            if (appContext.getAppPrefs().getBooleanPreference(ApplicationPreferences.MAP_RESOURCES_UPDATE_NEEDED)) {
                mapsDAO.deleteMaps();
            }

            allMapResources = mapsDAO.getAvailableMapsForACertainType(null);
            if (allMapResources == null || appContext.getAppPrefs().getBooleanPreference(ApplicationPreferences.MAP_RESOURCES_UPDATE_NEEDED)) {
                // maps table in DB not populated yet or needs to be updated
                List<MapDownloadResource> parsedMapResources = new ArrayList<MapDownloadResource>();
                Map<String, String> parsedMapItemsCodes = new HashMap<String, String>();
                Map<String, String> regionItemsCodes = new HashMap<String, String>();
                try {
                    // parse Maps.json
                    String jsonUrl = SKPackageManager.getInstance().getMapsJSONPathForCurrentVersion();
                    HttpURLConnection connection = (HttpURLConnection) new URL(jsonUrl).openConnection();
                    new MapDataParser().parseMapJsonData(parsedMapResources, parsedMapItemsCodes, regionItemsCodes, connection.getInputStream());
                    // populate DB maps table with parsing results
                    mapsDAO.insertMaps(parsedMapResources, parsedMapItemsCodes, regionItemsCodes, this);
                    // get all map resources
                    allMapResources = mapsDAO.getAvailableMapsForACertainType(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (appContext.getAppPrefs().getBooleanPreference(ApplicationPreferences.MAP_RESOURCES_UPDATE_NEEDED)) {
                activeDownloads = new ArrayList<DownloadResource>();
            } else {
                activeDownloads = getActiveMapDownloads();
                if (!activeDownloads.isEmpty() && activeDownloads.get(0).getDownloadState() == SKToolsDownloadItem.DOWNLOADING) {
                    // pausing first download in queue, if it's in downloading state
                    activeDownloads.get(0).setDownloadState(SKToolsDownloadItem.PAUSED);
                    mapsDAO.updateMapResource((MapDownloadResource) activeDownloads.get(0));
                }
            }
        }

        return allMapResources != null && allMapResources.size() > 0;
    }

    /**
     * Filters the active downloads (having their state QUEUED, DOWNLOADING or PAUSED) from all the available downloads
     *
     * @return
     */
    private List<DownloadResource> getActiveMapDownloads() {
        List<DownloadResource> activeMapDownloads = new ArrayList<DownloadResource>();
        String[] mapCodesArray = new Gson().fromJson(appContext.getAppPrefs().getStringPreference(ApplicationPreferences.DOWNLOAD_QUEUE_PREF_KEY), String[].class);
        if (mapCodesArray == null) {
            return activeMapDownloads;
        }
        for (String mapCode : mapCodesArray) {
            activeMapDownloads.add(allMapResources.get(mapCode));
        }
        return activeMapDownloads;
    }

    /**
     * Recursively populates list items with the corresponding parent & child data
     *
     * @param mapItem
     */
    private void populateWithChildMaps(ListItem mapItem) {
        String code;
        if (mapItem.downloadResource == null) {
            code = "";
        } else {
            code = mapItem.downloadResource.getCode();
        }

        List<ListItem> childrenItems = getChildrenOf(code);
        Collections.sort(childrenItems);
        for (ListItem childItem : childrenItems) {
            childItem.parent = mapItem;
            populateWithChildMaps(childItem);
        }
        mapItem.children = childrenItems;
    }

    /**
     * Gets the list of child items for a given code
     *
     * @param parentCode
     * @return
     */
    private List<ListItem> getChildrenOf(String parentCode) {
        List<ListItem> children = new ArrayList<ListItem>();
        for (MapDownloadResource mapResource : allMapResources.values()) {
            if (mapResource.getParentCode().equals(parentCode)) {
                ListItem listItem = new ListItem();
                listItem.name = mapResource.getName();
                listItem.downloadResource = mapResource;
                children.add(listItem);
            }
        }
        return children;
    }

    /**
     * Constructs a map from resource codes (keys )to list items (values) for the items currently displayed
     */
    private void buildCodesMap() {
        for (ListItem item : currentListItems) {
            codesMap.put(item.downloadResource.getCode(), item);
        }
    }

    /**
     * Represents the adapter associated with maps list
     */
    private class DownloadsAdapter extends BaseAdapter implements SKToolsDownloadListener {

        @Override
        public int getCount() {
            return currentListItems.size();
        }

        @Override
        public ListItem getItem(int i) {
            return currentListItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            final ListItem currentItem = getItem(position);
            View view = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.element_download_list_item, null);
            } else {
                view = convertView;
            }

            ImageView arrowImage = (ImageView) view.findViewById(R.id.arrow);
            TextView downloadSizeText = (TextView) view.findViewById(R.id.package_size);
            TextView downloadNameText = (TextView) view.findViewById(R.id.package_name);
            RelativeLayout middleLayout = (RelativeLayout) view.findViewById(R.id.middle_layout);
            ImageView startPauseImage = (ImageView) view.findViewById(R.id.start_pause);
            ImageView cancelImage = (ImageView) view.findViewById(R.id.cancel);
            TextView stateText = (TextView) view.findViewById(R.id.current_state);
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.download_progress);
            RelativeLayout progressDetailsLayout = (RelativeLayout) view.findViewById(R.id.progress_details);
            TextView percentageText = (TextView) view.findViewById(R.id.percentage);
            TextView timeLeftText = (TextView) view.findViewById(R.id.time_left);
            TextView speedText = (TextView) view.findViewById(R.id.speed);

            downloadNameText.setText(currentItem.name);

            if (currentItem.children == null || currentItem.children.isEmpty()) {
                arrowImage.setVisibility(View.GONE);
            } else {
                arrowImage.setVisibility(View.VISIBLE);
            }

            if (currentItem.downloadResource != null) {

                DownloadResource downloadResource = currentItem.downloadResource;

                boolean progressShown = downloadResource.getDownloadState() == SKToolsDownloadItem.DOWNLOADING || downloadResource.getDownloadState() == SKToolsDownloadItem.PAUSED;
                if (progressShown) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressDetailsLayout.setVisibility(View.VISIBLE);
                    progressBar.setProgress(getPercentage(downloadResource));
                    percentageText.setText(getPercentage(downloadResource) + "%");
                    if (downloadResource.getDownloadState() == SKToolsDownloadItem.PAUSED) {
                        timeLeftText.setText("-");
                        speedText.setText("-");
                    } else if (refreshDownloadEstimates) {
                        Pair<String, String> pair = calculateDownloadEstimates(downloadResource, 20);
                        speedText.setText(pair.first);
                        timeLeftText.setText(pair.second);
                        refreshDownloadEstimates = false;
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    progressDetailsLayout.setVisibility(View.GONE);
                }

                long bytesToDownload = 0;
                if (downloadResource instanceof MapDownloadResource) {
                    MapDownloadResource mapResource = (MapDownloadResource) downloadResource;
                    bytesToDownload = mapResource.getSkmAndZipFilesSize() + mapResource.getTXGFileSize();
                }

                if (bytesToDownload != 0) {
                    middleLayout.setVisibility(View.VISIBLE);
                    downloadSizeText.setVisibility(View.VISIBLE);
                    downloadSizeText.setText(convertBytesToStringRepresentation(bytesToDownload));
                } else {
                    middleLayout.setVisibility(View.GONE);
                    downloadSizeText.setVisibility(View.GONE);
                }

                switch (downloadResource.getDownloadState()) {
                    case SKToolsDownloadItem.NOT_QUEUED:
                        stateText.setText("NOT QUEUED");
                        break;
                    case SKToolsDownloadItem.QUEUED:
                        stateText.setText("QUEUED");
                        break;
                    case SKToolsDownloadItem.DOWNLOADING:
                        stateText.setText("DOWNLOADING");
                        break;
                    case SKToolsDownloadItem.DOWNLOADED:
                        stateText.setText("DOWNLOADED");
                        break;
                    case SKToolsDownloadItem.PAUSED:
                        stateText.setText("PAUSED");
                        break;
                    case SKToolsDownloadItem.INSTALLING:
                        stateText.setText("INSTALLING");
                        break;
                    case SKToolsDownloadItem.INSTALLED:
                        stateText.setText("INSTALLED");
                        break;
                    default:
                }

                if (downloadResource.getDownloadState() == SKToolsDownloadItem.NOT_QUEUED || downloadResource.getDownloadState() == SKToolsDownloadItem.DOWNLOADING ||
                        downloadResource.getDownloadState() == SKToolsDownloadItem.PAUSED) {
                    startPauseImage.setVisibility(View.VISIBLE);
                    if (downloadResource.getDownloadState() == SKToolsDownloadItem.DOWNLOADING) {
                        startPauseImage.setImageResource(R.drawable.pause);
                    } else {
                        startPauseImage.setImageResource(R.drawable.download);
                    }
                } else {
                    startPauseImage.setVisibility(View.GONE);
                }

                if (downloadResource.getDownloadState() == SKToolsDownloadItem.NOT_QUEUED || downloadResource.getDownloadState() == SKToolsDownloadItem.INSTALLING) {
                    cancelImage.setVisibility(View.GONE);
                } else {
                    cancelImage.setVisibility(View.VISIBLE);
                }

                if (downloadResource instanceof MapDownloadResource) {
                    MapDownloadResource mapResource = (MapDownloadResource) downloadResource;
                }

            } else {
                // no download resource
                downloadSizeText.setVisibility(View.GONE);
                middleLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                progressDetailsLayout.setVisibility(View.GONE);
                downloadSizeText.setVisibility(View.GONE);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentItem.children == null || currentItem.children.isEmpty()) {
                        return;
                    }
                    currentListItems = currentItem.children;
                    buildCodesMap();
                    previousListIndexes.push(listView.getFirstVisiblePosition());
                    updateListAndScrollToPosition(0);
                }
            });

            startPauseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentItem.downloadResource.getDownloadState() != SKToolsDownloadItem.DOWNLOADING) {
                        if (currentItem.downloadResource.getDownloadState() != SKToolsDownloadItem.PAUSED) {
                            activeDownloads.add(currentItem.downloadResource);
                            currentItem.downloadResource.setDownloadState(SKToolsDownloadItem.QUEUED);
                            appContext.getAppPrefs().saveDownloadQueuePreference(activeDownloads);
                            String destinationPath = appContext.getMapResourcesDirPath() + "downloads/";
                            File destinationFile = new File(destinationPath);
                            if (!destinationFile.exists()) {
                                destinationFile.mkdirs();
                            }
                            currentItem.downloadResource.setDownloadPath(destinationPath);
                            mapsDAO.updateMapResource((MapDownloadResource) currentItem.downloadResource);
                        }

                        notifyDataSetChanged();

                        List<SKToolsDownloadItem> downloadItems;
                        if (!downloadManager.isDownloadProcessRunning()) {
                            downloadItems = createDownloadItemsFromDownloadResources(activeDownloads);
                        } else {
                            List<DownloadResource> mapDownloadResources = new ArrayList<DownloadResource>();
                            mapDownloadResources.add(currentItem.downloadResource);
                            downloadItems = createDownloadItemsFromDownloadResources(mapDownloadResources);
                        }
                        downloadManager.startDownload(downloadItems);
                    } else {
                        downloadManager.pauseDownloadThread();
                    }
                }
            });

            cancelImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentItem.downloadResource.getDownloadState() != SKToolsDownloadItem.INSTALLED) {
                        boolean downloadCancelled = downloadManager.cancelDownload(currentItem.downloadResource.getCode());
                        if (!downloadCancelled) {
                            currentItem.downloadResource.setDownloadState(SKToolsDownloadItem.NOT_QUEUED);
                            currentItem.downloadResource.setNoDownloadedBytes(0);
                            mapsDAO.updateMapResource((MapDownloadResource) currentItem.downloadResource);
                            activeDownloads.remove(currentItem.downloadResource);
                            appContext.getAppPrefs().saveDownloadQueuePreference(activeDownloads);
                            notifyDataSetChanged();
                        }
                    } else {
                        boolean packageDeleted = SKPackageManager.getInstance().deleteOfflinePackage(currentItem.downloadResource.getCode());
                        if (packageDeleted) {
                            Toast.makeText(appContext, ((MapDownloadResource) currentItem.downloadResource).getName() + " was uninstalled", Toast.LENGTH_SHORT).show();
                        }
                        currentItem.downloadResource.setDownloadState(SKToolsDownloadItem.NOT_QUEUED);
                        currentItem.downloadResource.setNoDownloadedBytes(0);
                        mapsDAO.updateMapResource((MapDownloadResource) currentItem.downloadResource);
                        notifyDataSetChanged();
                    }
                }
            });

            return view;
        }

        /**
         * Calculates download estimates (for current speed and remaining time) for the currently downloading resource.
         * This estimate is based on how much was downloaded during the reference period.
         *
         * @param resource currently downloading resource
         * @param referencePeriodInSeconds the reference period (in seconds)
         * @return formatted string representations of the current download speed and remaining time
         */
        private Pair<String, String> calculateDownloadEstimates(DownloadResource resource, int referencePeriodInSeconds) {
            long referencePeriod = 1000 * referencePeriodInSeconds;
            long currentTimestamp = System.currentTimeMillis();
            long downloadPeriod = currentTimestamp - referencePeriod < downloadStartTime ? currentTimestamp - downloadStartTime : referencePeriod;
            long totalBytesDownloaded = 0;
            Iterator<Map.Entry<Long, Long>> iterator = downloadChunksMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, Long> entry = iterator.next();
                long timestamp = entry.getKey();
                long bytesDownloaded = entry.getValue();
                if (currentTimestamp - timestamp > referencePeriod) {
                    iterator.remove();
                } else {
                    totalBytesDownloaded += bytesDownloaded;
                }
            }
            float downloadPeriodSec = downloadPeriod / 1000f;
            long bytesPerSecond = Math.round(totalBytesDownloaded / downloadPeriodSec);
            String formattedTimeLeft = "";
            if (totalBytesDownloaded == 0) {
                formattedTimeLeft = "-";
            } else if (resource instanceof MapDownloadResource) {
                MapDownloadResource mapResource = (MapDownloadResource) resource;
                long remainingBytes = (mapResource.getSkmAndZipFilesSize() + mapResource.getTXGFileSize()) - mapResource.getNoDownloadedBytes();
                long timeLeft = (downloadPeriod * remainingBytes) / totalBytesDownloaded;
                formattedTimeLeft = getFormattedTime(timeLeft);
            }

            return new Pair<String, String>(convertBytesToStringRepresentation(bytesPerSecond) + "/s", formattedTimeLeft);
        }

        @Override
        public void notifyDataSetChanged() {
            ResourceDownloadsListActivity.this.findViewById(R.id.cancel_all_button).setVisibility(activeDownloads.isEmpty() ? View.GONE : View.VISIBLE);
            super.notifyDataSetChanged();
            listView.postInvalidate();
        }

        /**
         * Gets a percentage of how much was downloaded from the given resource
         *
         * @param downloadResource download resource
         * @return perecntage value
         */
        private int getPercentage(DownloadResource downloadResource) {
            int percentage = 0;
            if (downloadResource instanceof MapDownloadResource) {
                MapDownloadResource mapDownloadResource = (MapDownloadResource) downloadResource;
                percentage = (int) (((float) mapDownloadResource.getNoDownloadedBytes() / (mapDownloadResource
                        .getSkmAndZipFilesSize() + mapDownloadResource.getTXGFileSize())) * 100);
            }
            return percentage;
        }

        @Override
        public void onDownloadProgress(SKToolsDownloadItem currentDownloadItem) {
            ListItem affectedListItem = codesMap.get(currentDownloadItem.getItemCode());
            DownloadResource resource;
            boolean stateChanged = false;
            long bytesDownloadedSinceLastUpdate = 0;
            if (affectedListItem != null) {
                stateChanged = currentDownloadItem.getDownloadState() != affectedListItem.downloadResource.getDownloadState();
                bytesDownloadedSinceLastUpdate = currentDownloadItem.getNoDownloadedBytes() - affectedListItem.downloadResource.getNoDownloadedBytes();
                affectedListItem.downloadResource.setNoDownloadedBytes(currentDownloadItem.getNoDownloadedBytes());
                affectedListItem.downloadResource.setDownloadState(currentDownloadItem.getDownloadState());
                resource = affectedListItem.downloadResource;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            } else {
                resource = allMapResources.get(currentDownloadItem.getItemCode());
                bytesDownloadedSinceLastUpdate = currentDownloadItem.getNoDownloadedBytes() - resource.getNoDownloadedBytes();
                stateChanged = currentDownloadItem.getDownloadState() != resource.getDownloadState();
                resource.setNoDownloadedBytes(currentDownloadItem.getNoDownloadedBytes());
                resource.setDownloadState(currentDownloadItem.getDownloadState());
            }
            if (resource.getDownloadState() == SKToolsDownloadItem.DOWNLOADED) {
                activeDownloads.remove(resource);
                appContext.getAppPrefs().saveDownloadQueuePreference(activeDownloads);
            } else if (resource.getDownloadState() == SKToolsDownloadItem.DOWNLOADING) {
                downloadChunksMap.put(System.currentTimeMillis(), bytesDownloadedSinceLastUpdate);
                if (stateChanged) {
                    startPeriodicUpdates();
                }
            }
            if (resource.getDownloadState() != SKToolsDownloadItem.DOWNLOADING) {
                stopPeriodicUpdates();
            }
            if (stateChanged) {
                mapsDAO.updateMapResource((MapDownloadResource) resource);
            }

            appContext.getAppPrefs().saveDownloadStepPreference(currentDownloadItem.getCurrentStepIndex());
        }

        @Override
        public void onDownloadCancelled(String currentDownloadItemCode) {
            stopPeriodicUpdates();
            ListItem affectedListItem = codesMap.get(currentDownloadItemCode);
            if (affectedListItem != null) {
                affectedListItem.downloadResource.setNoDownloadedBytes(0);
                affectedListItem.downloadResource.setDownloadState(SKToolsDownloadItem.NOT_QUEUED);
                activeDownloads.remove(affectedListItem.downloadResource);
                mapsDAO.updateMapResource((MapDownloadResource) affectedListItem.downloadResource);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            } else {
                DownloadResource downloadResource = allMapResources.get(currentDownloadItemCode);
                downloadResource.setNoDownloadedBytes(0);
                downloadResource.setDownloadState(SKToolsDownloadItem.NOT_QUEUED);
                activeDownloads.remove(downloadResource);
                mapsDAO.updateMapResource((MapDownloadResource) downloadResource);
            }
            appContext.getAppPrefs().saveDownloadQueuePreference(activeDownloads);
        }

        @Override
        public void onAllDownloadsCancelled() {
            stopPeriodicUpdates();
            appContext.getAppPrefs().saveDownloadStepPreference(0);
            for (DownloadResource downloadResource : activeDownloads) {
                downloadResource.setDownloadState(SKToolsDownloadItem.NOT_QUEUED);
                downloadResource.setNoDownloadedBytes(0);
            }
            mapsDAO.clearResourcesInDownloadQueue();
            activeDownloads.clear();
            appContext.getAppPrefs().saveDownloadQueuePreference(activeDownloads);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onDownloadPaused(SKToolsDownloadItem currentDownloadItem) {
            stopPeriodicUpdates();
            ListItem affectedListItem = codesMap.get(currentDownloadItem.getItemCode());
            if (affectedListItem != null) {
                affectedListItem.downloadResource.setDownloadState(currentDownloadItem.getDownloadState());
                affectedListItem.downloadResource.setNoDownloadedBytes(currentDownloadItem.getNoDownloadedBytes());
                mapsDAO.updateMapResource((MapDownloadResource) affectedListItem.downloadResource);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            } else {
                DownloadResource downloadResource = allMapResources.get(currentDownloadItem.getItemCode());
                downloadResource.setDownloadState(currentDownloadItem.getDownloadState());
                downloadResource.setNoDownloadedBytes(currentDownloadItem.getNoDownloadedBytes());
                mapsDAO.updateMapResource((MapDownloadResource) downloadResource);
            }

            appContext.getAppPrefs().saveDownloadStepPreference(currentDownloadItem.getCurrentStepIndex());
        }

        @Override
        public void onInstallFinished(final SKToolsDownloadItem currentInstallingItem) {
            ListItem affectedListItem = codesMap.get(currentInstallingItem.getItemCode());
            final DownloadResource resource;
            if (affectedListItem != null) {
                affectedListItem.downloadResource.setDownloadState(SKToolsDownloadItem.INSTALLED);
                resource = affectedListItem.downloadResource;
                mapsDAO.updateMapResource((MapDownloadResource) affectedListItem.downloadResource);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            } else {
                resource = allMapResources.get(currentInstallingItem.getItemCode());
                resource.setDownloadState(SKToolsDownloadItem.INSTALLED);
                mapsDAO.updateMapResource((MapDownloadResource) resource);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(appContext, ((MapDownloadResource) resource).getName() + " was installed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onInstallStarted(SKToolsDownloadItem currentInstallingItem) {
            ListItem affectedListItem = codesMap.get(currentInstallingItem.getItemCode());
            if (affectedListItem != null) {
                affectedListItem.downloadResource.setDownloadState(SKToolsDownloadItem.INSTALLING);
                mapsDAO.updateMapResource((MapDownloadResource) affectedListItem.downloadResource);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            } else {
                DownloadResource downloadResource = allMapResources.get(currentInstallingItem.getItemCode());
                downloadResource.setDownloadState(SKToolsDownloadItem.INSTALLING);
                mapsDAO.updateMapResource((MapDownloadResource) downloadResource);
            }
        }

        @Override
        public void onInternetConnectionFailed(SKToolsDownloadItem currentDownloadItem, boolean responseReceivedFromServer) {
            stopPeriodicUpdates();
            appContext.getAppPrefs().saveDownloadStepPreference(currentDownloadItem.getCurrentStepIndex());
        }

        @Override
        public void onNotEnoughMemoryOnCurrentStorage(SKToolsDownloadItem currentDownloadItem) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ResourceDownloadsListActivity.this.getApplicationContext(), "Not enough memory on the storage", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (currentListItems == null || currentListItems.isEmpty()) {
            super.onBackPressed();
            return;
        }
        ListItem firstItem = currentListItems.get(0);
        if (firstItem.parent.parent == null) {
            super.onBackPressed();
        } else {
            currentListItems = currentListItems.get(0).parent.parent.children;
            buildCodesMap();
            updateListAndScrollToPosition(previousListIndexes.pop());
        }
    }

    /**
     * Triggers an update on the list and sets its position to the given value
     * @param position
     */
    private void updateListAndScrollToPosition(final int position) {
        listView.setVisibility(View.INVISIBLE);
        adapter.notifyDataSetChanged();
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(position);
                listView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Formats a given value (provided in bytes)
     *
     * @param value value (in bytes)
     * @return formatted string (value and unit)
     */
    public static String convertBytesToStringRepresentation(final long value) {
        final long[] dividers = new long[]{TERRA, GIGA, MEGA, KILO, 1};
        final String[] units = new String[]{"TB", "GB", "MB", "KB", "B"};

        String result = null;
        for (int i = 0; i < dividers.length; i++) {
            final long divider = dividers[i];
            if (value >= divider) {
                result = formatDecimals(value, divider, units[i]);
                break;
            }
        }
        if (result != null) {
            return result;
        } else {
            return "0 B";
        }
    }

    /**
     * Format the time value given as parameter (in milliseconds)
     *
     * @param time time value (provided in milliseconds)
     * @return formatted time
     */
    public static String getFormattedTime(long time) {
        String format = String.format("%%0%dd", 2);
        time = time / 1000;
        String seconds = String.format(format, time % 60);
        String minutes = String.format(format, (time % 3600) / 60);
        String hours = String.format(format, time / 3600);
        String formattedTime = hours + ":" + minutes + ":" + seconds;
        return formattedTime;
    }

    /**
     * Formats decimal numbers
     *
     * @param value the value that needs to be formatted
     * @param divider the amount to divide the value to obtain the proper unit
     * @param unit unit of the result
     * @return formatted value
     */
    private static String formatDecimals(final long value, final long divider, final String unit) {
        final double result = divider > 1 ? (double) value / (double) divider : (double) value;
        return new DecimalFormat("#,##0.#").format(result) + " " + unit;
    }

    /**
     * Generates a list of download items based on the list of resources given as input
     *
     * @param downloadResources list of resources
     * @return a list of SKToolsDownloadItem objects
     */
    private List<SKToolsDownloadItem> createDownloadItemsFromDownloadResources(List<DownloadResource>
                                                                                       downloadResources) {
        List<SKToolsDownloadItem> downloadItems = new ArrayList<SKToolsDownloadItem>();
        for (DownloadResource currentDownloadResource : downloadResources) {
            SKToolsDownloadItem currentItem = currentDownloadResource.toDownloadItem();
            if (currentDownloadResource.getDownloadState() == SKToolsDownloadItem.QUEUED) {
                currentItem.setCurrentStepIndex((byte) 0);
            } else if ((currentDownloadResource.getDownloadState() == SKToolsDownloadItem.PAUSED) || (currentDownloadResource.getDownloadState() == SKToolsDownloadItem
                    .DOWNLOADING)) {
                int downloadStepIndex = appContext.getAppPrefs().getIntPreference(ApplicationPreferences.DOWNLOAD_STEP_INDEX_PREF_KEY);
                currentItem.setCurrentStepIndex((byte) downloadStepIndex);
            }
            downloadItems.add(currentItem);
        }
        return downloadItems;
    }

    /**
     * Click handler
     * @param view
     */
    public void onClick(View view) {
        if (view.getId() == R.id.cancel_all_button) {
            boolean cancelled = downloadManager.cancelAllDownloads();
            if (!cancelled) {
                for (DownloadResource resource : activeDownloads) {
                    resource.setNoDownloadedBytes(0);
                    resource.setDownloadState(SKToolsDownloadItem.NOT_QUEUED);
                }
                activeDownloads.clear();
                appContext.getAppPrefs().saveDownloadQueuePreference(activeDownloads);
                mapsDAO.clearResourcesInDownloadQueue();
                adapter.notifyDataSetChanged();
            }
        }
    }
}