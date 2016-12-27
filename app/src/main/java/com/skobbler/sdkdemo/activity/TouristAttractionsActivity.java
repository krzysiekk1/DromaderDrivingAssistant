package com.skobbler.sdkdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skobbler.ngx.SKCategories;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.map.SKAnimationSettings;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.positioner.SKPosition;
import com.skobbler.ngx.positioner.SKPositionerManager;
import com.skobbler.ngx.sdktools.onebox.utils.SKToolsUtils;
import com.skobbler.ngx.search.SKNearbySearchSettings;
import com.skobbler.ngx.search.SKSearchListener;
import com.skobbler.ngx.search.SKSearchManager;
import com.skobbler.ngx.search.SKSearchResult;
import com.skobbler.ngx.search.SKSearchStatus;
import com.skobbler.ngx.util.SKLogging;
import com.skobbler.sdkdemo.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Krzysiek
 */

// activity in which a nearby search for some main categories is performed
public class TouristAttractionsActivity extends Activity implements SKSearchListener {

    // the main categories for which the nearby search will be executed
    private static final int[] searchCategories = new int[] {
            SKCategories.SKPOICategory.SKPOI_CATEGORY_ATTRACTION.getValue(),
            SKCategories.SKPOICategory.SKPOI_CATEGORY_MUSEUM.getValue(),
            SKCategories.SKPOICategory.SKPOI_CATEGORY_ZOO.getValue(),
            SKCategories.SKPOICategory.SKPOI_CATEGORY_AMUSEMENT_PARK.getValue(),
            SKCategories.SKPOICategory.SKPOI_CATEGORY_WATER_PARK.getValue(),
            SKCategories.SKPOICategory.SKPOI_CATEGORY_STADIUM2.getValue()
    };

    short radius = 20000;   // 20 km

    private SKCategories.SKPOICategory selectedCategory;
    List<SKCategories.SKPOICategory> viewCategories;
    private ListView listView;
    private TextView operationInProgressLabel;
    private ResultsListAdapter adapter;

    SKSearchManager searchManager;
    SKNearbySearchSettings searchObject;
    SKPosition currentPosition;
    SKCoordinate currentCoordinate;
    SKSearchStatus status;

    // search results grouped by their main category field
    private Map<SKCategories.SKPOICategory, List<SKSearchResult>> results =
            new LinkedHashMap<SKCategories.SKPOICategory, List<SKSearchResult>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        operationInProgressLabel = (TextView) findViewById(R.id.label_operation_in_progress);
        listView = (ListView) findViewById(R.id.list_view);
        operationInProgressLabel.setText(getResources().getString(R.string.searching));

        startSearch();
    }

    // initiates a nearby search with the specified categories
    private void startSearch() {
        searchManager = new SKSearchManager(this);
        searchObject = new SKNearbySearchSettings();
        currentPosition = SKPositionerManager.getInstance().getCurrentGPSPosition(true);
        currentCoordinate = currentPosition.getCoordinate();
        searchObject.setLocation(currentCoordinate);
        searchObject.setRadius(radius);
        searchObject.setSearchResultsNumber(100);
        searchObject.setSearchCategories(searchCategories);
        searchObject.setSearchTerm(""); // all
        searchObject.setSearchMode(SKSearchManager.SKSearchMode.OFFLINE);
        status = searchManager.nearbySearch(searchObject);
        if (status != SKSearchStatus.SK_SEARCH_NO_ERROR) {
            SKLogging.writeLog("SKSearchStatus: ", status.toString(), 0);
            if (status == SKSearchStatus.SK_SEARCH_NO_MAP_INFORMATION) {
                Toast.makeText(this, "Unknown GPS location", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "An unknown error occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void buildResultsMap(List<SKSearchResult> searchResults) {
        viewCategories = new ArrayList<SKCategories.SKPOICategory>();
        for (SKSearchResult result : searchResults) {
            if (!results.containsKey(result.getCategory())) {
                results.put(SKCategories.SKPOICategory.forInt(result.getCategory().getValue()), new ArrayList<SKSearchResult>());
                viewCategories.add(result.getCategory());
            }
            results.get(result.getCategory()).add(result);
        }
    }

    @Override
    public void onReceivedSearchResults(final List<SKSearchResult> searchResults) {
        buildResultsMap(searchResults);
        operationInProgressLabel.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        adapter = new ResultsListAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                if (selectedCategory == null) {
                    selectedCategory = SKCategories.SKPOICategory.forInt(viewCategories.get(position).getValue());
                    adapter.notifyDataSetChanged();
                } else {
                    String POIName = results.get(selectedCategory).get(position).getName();
                    final DialogMessage dm = new DialogMessage(TouristAttractionsActivity.this);
                    dm.setMessage(POIName, R.string.show_on_map, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            SKAnnotation annotation = new SKAnnotation(5);
                            annotation.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_BLUE);
                            annotation.setLocation(results.get(selectedCategory).get(position).getLocation());
                            SKMapViewHolder mapViewHolder = MapActivity.getMapViewHolder();
                            SKMapSurfaceView mapView = mapViewHolder.getMapSurfaceView();
                            mapView.addAnnotation(annotation, SKAnimationSettings.ANIMATION_NONE);
                            mapView.setZoom(13);
                            mapView.animateToLocation(results.get(selectedCategory).get(position).getLocation(), 0);
                            finish();
                        }
                    }, R.string.cancel_dm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dm.cancel();
                        }
                    });
                    dm.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (selectedCategory == null) {
            super.onBackPressed();
        } else {
            selectedCategory = null;
            adapter.notifyDataSetChanged();
        }
    }

    private class ResultsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (selectedCategory == null) {
                return results.size();
            } else {
                return results.get(selectedCategory).size();
            }
        }

        @Override
        public Object getItem(int position) {
            if (selectedCategory == null) {
                return results.get(viewCategories.get(position).getValue());
            } else {
                return results.get(selectedCategory).get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.layout_search_list_item, null);
            } else {
                view = convertView;
            }

            if (selectedCategory == null) {
                ((TextView) view.findViewById(R.id.title)).setText(viewCategories.get(position).toString()
                        .replace("SKPOI_CATEGORY_", "").replaceAll("_", " ").replaceAll("[0-9]", ""));
                ((TextView) view.findViewById(R.id.subtitle)).setText("number of POIs: "
                                                                        + results.get(viewCategories.get(position)).size());
            } else {
                SKSearchResult result = results.get(selectedCategory).get(position);
                ((TextView) view.findViewById(R.id.title)).setText(!result.getName().equals("") ? result.getName()
                        : result.getCategory().toString());
                int distance = (int) SKToolsUtils.distanceBetween(searchObject.getLocation(), result.getLocation());
                ((TextView) view.findViewById(R.id.subtitle)).setText("distance: " + String.valueOf(distance) + " m");
            }
            return view;
        }
    }
}
