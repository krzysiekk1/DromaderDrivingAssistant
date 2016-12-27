package com.skobbler.sdkdemo.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.skobbler.ngx.SKCategories;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.map.SKAnimationSettings;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.routing.SKViaPoint;
import com.skobbler.ngx.sdktools.onebox.SKOneBoxSearchResult;
import com.skobbler.ngx.sdktools.onebox.SKToolsSearchObject;
import com.skobbler.ngx.sdktools.onebox.SKToolsSearchServiceManager;
import com.skobbler.ngx.sdktools.onebox.adapters.CategoryListItem;
import com.skobbler.ngx.sdktools.onebox.adapters.SKCategoriesAdapter;
import com.skobbler.ngx.sdktools.onebox.adapters.SKSearchResultAdapter;
import com.skobbler.ngx.sdktools.onebox.fragments.OneBoxManager;
import com.skobbler.ngx.sdktools.onebox.listeners.OnItemSelectedListener;
import com.skobbler.ngx.sdktools.onebox.listeners.mOnClickListener;
import com.skobbler.ngx.sdktools.onebox.utils.DividerItemDecoration;
import com.skobbler.ngx.search.SKSearchListener;
import com.skobbler.ngx.search.SKSearchResult;
import com.skobbler.sdkdemo.R;
import com.skobbler.sdkdemo.activity.DialogMessage;
import com.skobbler.sdkdemo.activity.MapActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.skobbler.sdkdemo.activity.MapActivity.RED_PIN_ICON_ID;
import static com.skobbler.sdkdemo.activity.MapActivity.VIA_POINT_ICON_ID;

/**
 * Created by Jakub Solawa on 14.11.2016.
 */
public class OneBoxExtFragment extends Fragment implements SKSearchListener, View.OnClickListener {

    public static boolean ONEBOX_ACTIVATED = false;
    public static final int STATE_DEFAULT = 0, STATE_SHOWING_CATEGORY_RESULTS = 1, STATE_SHOWING_CATEGORIES_EXPANDED = 2, STATE_SHOWING_RESULTS = 3;
    public static final int HIDE = 0, SHOW_CROSS = 1, SHOW_SORT = 2;
    /**
     * OneBox state
     */
    private static int previousState, internalState = 0;
    /**
     * Edit text for entering the search term
     */
    EditText searchFieldEditable;
    /**
     * RecycleView for search categories and search results
     */
    protected RecyclerView recyclerViewCategories;
    /**
     * Adapter for categories
     */
    SKCategoriesAdapter adapterCategories;
    /**
     * List of categories(Food,Services, etc.)
     */
    List<CategoryListItem> categories = new ArrayList<>();

    /**
     * Manager for handling search
     */
    SKToolsSearchServiceManager searchServiceManager;
    /**
     * Clear search term
     */
    ImageButton clearSearchField;
    /**
     * View for empty results
     */
    TextView noResultsView;
    /**
     * Sort index
     */
    private int rankIndex;
    private Activity activity;

    SKAnnotation annotationViaPoint = new SKAnnotation(VIA_POINT_ICON_ID);
    SKAnnotation annotationEndPoint = new SKAnnotation(RED_PIN_ICON_ID);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(com.skobbler.ngx.R.layout.onebox_fragment, container, false);

        searchFieldEditable = (EditText) view.findViewById(com.skobbler.ngx.R.id.search_field_editable);
        recyclerViewCategories = (RecyclerView) view.findViewById(com.skobbler.ngx.R.id.onebox_recycle_view);
        noResultsView = (TextView) view.findViewById(com.skobbler.ngx.R.id.no_results_view);

        searchFieldEditable.setOnClickListener(this);
        ImageButton backButton = (ImageButton) view.findViewById(com.skobbler.ngx.R.id.search_back_button);
        backButton.setOnClickListener(this);
        clearSearchField = (ImageButton) view.findViewById(com.skobbler.ngx.R.id.search_clear);
        return view;
    }

    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        searchServiceManager = new SKToolsSearchServiceManager(getActivity());

        adapterCategories = new SKCategoriesAdapter(getActivity());
        adapterCategories.setData(categories);
        recyclerViewCategories.setAdapter(adapterCategories);
        recyclerViewCategories.setHasFixedSize(true);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration categoryListDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        recyclerViewCategories.addItemDecoration(categoryListDecoration);

        ((SKCategoriesAdapter) recyclerViewCategories.getAdapter()).setOnItemClickListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(CategoryListItem item) {

                previousState = internalState;
                double[] coordinate = OneBoxManager.getCurrentPosition();
                SKCategories.SKPOIMainCategory skpoiMainCategory = (SKCategories.SKPOIMainCategory) searchServiceManager.getCategoryForString(item.getText());
                int[] poiCategories = {skpoiMainCategory.getValue()};

                SKToolsSearchObject searchObject = new SKToolsSearchObject((short) 1000, new SKCoordinate(coordinate[0], coordinate[1]), poiCategories, 20);
                searchServiceManager.nbCategorySearch(searchObject, OneBoxExtFragment.this);
                changeOneBoxState(STATE_SHOWING_CATEGORY_RESULTS, item.getText());

            }
        });
    }

    /**
     * Initialize SKPOICategories for search
     */
    @Override
    public void onReceivedSearchResults(List<SKSearchResult> list) {

        rankIndex = list.size();
        if (list.size() == 0) {
            recyclerViewCategories.setVisibility(View.GONE);
            noResultsView.setVisibility(View.VISIBLE);
            hideSoftKeyboard(searchFieldEditable);
        } else {
            recyclerViewCategories.setVisibility(View.VISIBLE);
            noResultsView.setVisibility(View.GONE);

            changeRightButtonState(clearSearchField, SHOW_SORT);

            final List<SKOneBoxSearchResult> resultList = new ArrayList<>();
            for (SKSearchResult result : list) {
                SKOneBoxSearchResult searchObject = new SKOneBoxSearchResult(result, rankIndex);
                if (!result.getName().isEmpty()) {
                    resultList.add(searchObject);
                }
                rankIndex--;

            }
            SKSearchResultAdapter adapter = new SKSearchResultAdapter(resultList, getActivity());
            recyclerViewCategories.setAdapter(adapter);
            hideSoftKeyboard(recyclerViewCategories);

            ((SKSearchResultAdapter) recyclerViewCategories.getAdapter()).setmOnClickListener(new mOnClickListener() {
                @Override
                public void onClick(final View view) {
                    final int itemPosition = recyclerViewCategories.getChildPosition(view);
                    String locationName = resultList.get(itemPosition).getSearchResult().getName();
                    final DialogMessage dm = new DialogMessage(getActivity());

                    dm.setMessage(locationName, R.string.end_point_nav, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            ((MapActivity)getActivity()).setDestinationPoint(resultList.get(itemPosition).getSearchResult().getLocation());
                            recyclerViewCategories.setVisibility(View.VISIBLE);
                            noResultsView.setVisibility(View.GONE);
                            changeRightButtonState(clearSearchField, HIDE);
                            getFragmentManager().popBackStack();
                            getActivity().getActionBar().show();
                            hideSoftKeyboard(searchFieldEditable);
                            ONEBOX_ACTIVATED = false;
                            annotationEndPoint.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_RED);
                            annotationEndPoint.setLocation(resultList.get(itemPosition).getSearchResult().getLocation());
                            SKMapViewHolder mapViewHolder = MapActivity.getMapViewHolder();
                            SKMapSurfaceView mapView = mapViewHolder.getMapSurfaceView();
                            mapView.addAnnotation(annotationEndPoint, SKAnimationSettings.ANIMATION_NONE);
                            mapView.setZoom(13);
                            mapView.animateToLocation(resultList.get(itemPosition).getSearchResult().getLocation(), 0);
                        }
                    }, R.string.cancel_dm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dm.cancel();
                                }
                    },
                            R.string.select_via_point, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((MapActivity)getActivity()).setViaPoint(new SKViaPoint(VIA_POINT_ICON_ID,resultList.get(itemPosition).getSearchResult().getLocation()));
                                    recyclerViewCategories.setVisibility(View.VISIBLE);
                                    noResultsView.setVisibility(View.GONE);
                                    changeRightButtonState(clearSearchField, HIDE);
                                    getFragmentManager().popBackStack();
                                    getActivity().getActionBar().show();
                                    hideSoftKeyboard(searchFieldEditable);
                                    ONEBOX_ACTIVATED = false;

                                    annotationViaPoint.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_PURPLE);
                                    annotationViaPoint.setLocation(resultList.get(itemPosition).getSearchResult().getLocation());
                                    SKMapViewHolder mapViewHolder = MapActivity.getMapViewHolder();
                                    SKMapSurfaceView mapView = mapViewHolder.getMapSurfaceView();
                                    mapView.addAnnotation(annotationViaPoint, SKAnimationSettings.ANIMATION_NONE);
                                    mapView.setZoom(13);
                                    mapView.animateToLocation(resultList.get(itemPosition).getSearchResult().getLocation(), 0);
                                }
                        });
                    dm.show();

                }
            });
            System.out.println("wszystko ok");
        }
    }

    /**
     * Handles back button pressed events
     */
    public void handleBackButtonPressed() {
        recyclerViewCategories.setVisibility(View.VISIBLE);
        noResultsView.setVisibility(View.GONE);

        changeRightButtonState(clearSearchField, HIDE);
        if (internalState == STATE_SHOWING_CATEGORIES_EXPANDED) {
            for (int i = 4; i < categories.size(); i++) {
                categories.get(i).setShowItem(false);
            }
            adapterCategories.setData(categories);
            adapterCategories.updateList(categories);
            SKCategoriesAdapter.categoryListExpended = false;
            changeOneBoxState(STATE_DEFAULT, "Search text here");
        } else if (internalState == STATE_SHOWING_CATEGORY_RESULTS) {
            if (previousState == STATE_DEFAULT || previousState == STATE_SHOWING_RESULTS) {
                recyclerViewCategories.setAdapter(adapterCategories);
                changeOneBoxState(STATE_DEFAULT, "Search text here");

            } else if (previousState == STATE_SHOWING_CATEGORIES_EXPANDED) {
                for (CategoryListItem categoryListItem : categories) {
                    categoryListItem.setShowItem(true);
                }
                adapterCategories.updateList(categories);
                recyclerViewCategories.setAdapter(adapterCategories);
                changeOneBoxState(STATE_SHOWING_CATEGORIES_EXPANDED, "OpenStreetMap");
            }
        } else if (internalState == STATE_SHOWING_RESULTS) {
            searchFieldEditable.removeTextChangedListener(customTextWatcher);
            recyclerViewCategories.setAdapter(adapterCategories);
            hideSoftKeyboard(searchFieldEditable);
            changeOneBoxState(STATE_DEFAULT, "Search text here");

        } else if (internalState == STATE_DEFAULT) {
            getFragmentManager().popBackStack();
            getActivity().getActionBar().show();
            hideSoftKeyboard(searchFieldEditable);
            ONEBOX_ACTIVATED = false;

        }
    }

    /**
     * Change OneBox state
     * @param internal - the current state
     * @param text- set text in edit text
     */
    private void changeOneBoxState(int internal, String text) {
        switch (internal) {
            case STATE_DEFAULT:
                internalState = 0;
                searchFieldEditable.setEnabled(true);
                searchFieldEditable.setClickable(true);
                searchFieldEditable.getText().clear();
                searchFieldEditable.setHint(text);
                break;
            case STATE_SHOWING_CATEGORY_RESULTS:
                internalState = 1;
                searchFieldEditable.setEnabled(false);
                searchFieldEditable.setClickable(false);
                searchFieldEditable.setHint(Html.fromHtml(text + " <b>Results</b>"));
                searchFieldEditable.setHintTextColor(ContextCompat.getColor(getActivity(), com.skobbler.ngx.R.color.white));
                break;
            case STATE_SHOWING_CATEGORIES_EXPANDED:
                internalState = 2;
                searchFieldEditable.setEnabled(false);
                searchFieldEditable.setClickable(false);
                searchFieldEditable.setHint(Html.fromHtml(text + " <b>Categories</b>"));
                searchFieldEditable.setHintTextColor(ContextCompat.getColor(getActivity(), com.skobbler.ngx.R.color.white));
                break;
            case STATE_SHOWING_RESULTS:
                internalState = 3;
                break;
        }
    }

    /**
     * Change state for sorting button
     * @param auxButton
     * @param state
     */
    public void changeRightButtonState(ImageButton auxButton, int state) {
        int duration = 300;
        auxButton.setVisibility(View.VISIBLE);
        switch (state) {
            case SHOW_SORT:
                auxButton.setImageResource(com.skobbler.ngx.R.drawable.icon_sort_white);
                auxButton.animate().alpha(1).setDuration(duration);
                auxButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initSortLayout();

                    }
                });
                break;
            case SHOW_CROSS:
                auxButton.setImageResource(com.skobbler.ngx.R.drawable.icon_cancel_003_white);
                auxButton.animate().alpha(1).setDuration(duration);
                auxButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recyclerViewCategories.setVisibility(View.VISIBLE);
                        noResultsView.setVisibility(View.GONE);
                        recyclerViewCategories.setAdapter(adapterCategories);
                        hideSoftKeyboard(searchFieldEditable);
                        changeOneBoxState(STATE_DEFAULT, "Search text here");
                        changeRightButtonState(clearSearchField, HIDE);
                    }
                });
                break;
            case HIDE:
                auxButton.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * Initialize sort component
     */
    private void initSortLayout() {
        PopupMenu sortMenu = new PopupMenu(getActivity(), clearSearchField);
        sortMenu.inflate(com.skobbler.ngx.R.menu.onebox_sort);
        sortMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == com.skobbler.ngx.R.id.sort_name) {
                    ((SKSearchResultAdapter) recyclerViewCategories.getAdapter()).sort(OneBoxManager.SORT_NAME);
                } else if (id == com.skobbler.ngx.R.id.sort_distance) {
                    ((SKSearchResultAdapter) recyclerViewCategories.getAdapter()).sort(OneBoxManager.SORT_DISTANCE);
                } else if (id == com.skobbler.ngx.R.id.sort_rank) {
                    ((SKSearchResultAdapter) recyclerViewCategories.getAdapter()).sort(OneBoxManager.SORT_RANK);
                }
                return false;
            }
        });

        sortMenu.show();
    }

    /**
     * Perform OneLine search after 1 second
     */
    protected TextWatcher customTextWatcher = new TextWatcher() {
        private Timer timer = new Timer();
        private final long DELAY = 1000;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (timer != null) {
                changeRightButtonState(clearSearchField, SHOW_CROSS);
                timer.cancel();
            }
        }

        @Override
        public void afterTextChanged(final Editable editable) {
            timer.cancel();
            timer = new Timer();
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            String term = searchFieldEditable.getText().toString();
                            performSearch(term);
                        }
                    },
                    DELAY
            );
        }
    };

    /**
     * Searches in one country after an term passed in one line. Only one
     * search operation is possible at the time.This automatically cancel any
     * previous search.
     * @param term
     */
    private void performSearch(String term) {
        double[] coordinate = OneBoxManager.getCurrentPosition();
        if (!term.isEmpty()){
            SKToolsSearchObject searchObject = new SKToolsSearchObject(term,new SKCoordinate(coordinate[0],coordinate[1]));
            searchServiceManager.nbCategorySearch(searchObject, OneBoxExtFragment.this);
        }
        changeOneBoxState(STATE_SHOWING_RESULTS, null);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == com.skobbler.ngx.R.id.search_field_editable) {
            searchFieldEditable.addTextChangedListener(customTextWatcher);
            searchFieldEditable.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_SEARCH) {
                        String term = searchFieldEditable.getText().toString();
                        performSearch(term);
                    }
                    return false;
                }
            });

        } else if (id == com.skobbler.ngx.R.id.search_back_button) {
            handleBackButtonPressed();
        }
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
