package com.skobbler.ngx.sdktools.onebox.fragments;


import android.app.Fragment;
import android.graphics.Color;
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


import com.skobbler.ngx.R;
import com.skobbler.ngx.SKCategories;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.sdktools.onebox.SKOneBoxSearchResult;
import com.skobbler.ngx.sdktools.onebox.SKToolsSearchObject;
import com.skobbler.ngx.sdktools.onebox.adapters.CategoryListItem;
import com.skobbler.ngx.sdktools.onebox.listeners.OnSeeMoreListener;
import com.skobbler.ngx.sdktools.onebox.utils.DividerItemDecoration;
import com.skobbler.ngx.sdktools.onebox.listeners.OnItemSelectedListener;
import com.skobbler.ngx.sdktools.onebox.adapters.SKCategoriesAdapter;
import com.skobbler.ngx.sdktools.onebox.adapters.SKSearchResultAdapter;
import com.skobbler.ngx.sdktools.onebox.SKToolsSearchServiceManager;
import com.skobbler.ngx.search.SKSearchListener;
import com.skobbler.ngx.search.SKSearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class which handles UI for onebox component
 */
public class OneBoxFragment extends Fragment implements SKSearchListener, View.OnClickListener {

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
    RecyclerView recyclerViewCategories;
    /**
     * Adapter for categories
     */
    SKCategoriesAdapter adapterCategories;
    /**
     * List of categories(Food,Services, etc.)
     */
    List<CategoryListItem> categories;

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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.onebox_fragment, container, false);

        searchFieldEditable = (EditText) view.findViewById(R.id.search_field_editable);
        recyclerViewCategories = (RecyclerView) view.findViewById(R.id.onebox_recycle_view);
        noResultsView = (TextView) view.findViewById(R.id.no_results_view);


        searchFieldEditable.setOnClickListener(this);
        ImageButton backButton = (ImageButton) view.findViewById(R.id.search_back_button);
        backButton.setOnClickListener(this);
        clearSearchField = (ImageButton) view.findViewById(R.id.search_clear);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initCategories();
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

                SKToolsSearchObject searchObject = new SKToolsSearchObject((short)1000,new SKCoordinate(coordinate[0],coordinate[1]),poiCategories,20);
                searchServiceManager.nbCategorySearch(searchObject,OneBoxFragment.this);
                changeOneBoxState(STATE_SHOWING_CATEGORY_RESULTS, item.getText());

            }
        });

        ((SKCategoriesAdapter) recyclerViewCategories.getAdapter()).setOnSeeMoreListener(new OnSeeMoreListener() {
            @Override
            public void onSeeMoreClick(View listParent) {
                for (CategoryListItem categoryListItem : categories) {
                    categoryListItem.setShowItem(true);
                }
                adapterCategories.updateList(categories);
                changeOneBoxState(STATE_SHOWING_CATEGORIES_EXPANDED, "OpenStreetMap");

            }
        });


    }

    /**
     * Initialize SKPOICategories for search
     */
    private void initCategories() {
        categories = new ArrayList<>();
        categories.add(new CategoryListItem("Food", true));
        categories.add(new CategoryListItem("Health", true));
        categories.add(new CategoryListItem("Leisure", true));
        categories.add(new CategoryListItem("Nightlife", true));
        categories.add(new CategoryListItem("Public", false));
        categories.add(new CategoryListItem("Services", false));
        categories.add(new CategoryListItem("Sleeping", false));
        categories.add(new CategoryListItem("Shopping", false));
        categories.add(new CategoryListItem("Transport", false));

    }


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

            List<SKOneBoxSearchResult> resultList = new ArrayList<>();
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
                searchFieldEditable.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                break;
            case STATE_SHOWING_CATEGORIES_EXPANDED:
                internalState = 2;
                searchFieldEditable.setEnabled(false);
                searchFieldEditable.setClickable(false);
                searchFieldEditable.setHint(Html.fromHtml(text + " <b>Categories</b>"));
                searchFieldEditable.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.white));
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
                auxButton.setImageResource(R.drawable.icon_sort_white);
                auxButton.animate().alpha(1).setDuration(duration);
                auxButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initSortLayout();

                    }
                });
                break;
            case SHOW_CROSS:
                auxButton.setImageResource(R.drawable.icon_cancel_003_white);
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
        sortMenu.inflate(R.menu.onebox_sort);
        sortMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.sort_name) {
                    ((SKSearchResultAdapter) recyclerViewCategories.getAdapter()).sort(OneBoxManager.SORT_NAME);
                } else if (id == R.id.sort_distance) {
                    ((SKSearchResultAdapter) recyclerViewCategories.getAdapter()).sort(OneBoxManager.SORT_DISTANCE);
                } else if (id == R.id.sort_rank) {
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
            searchServiceManager.nbCategorySearch(searchObject, OneBoxFragment.this);

        }
        changeOneBoxState(STATE_SHOWING_RESULTS, null);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.search_field_editable) {
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

        } else if (id == R.id.search_back_button) {
            handleBackButtonPressed();
        }
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
