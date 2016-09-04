package com.skobbler.ngx.sdktools.onebox;

import android.content.Context;

import com.skobbler.ngx.R;
import com.skobbler.ngx.SKCategories;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.sdktools.onebox.utils.SKToolsUtils;
import com.skobbler.ngx.search.SKNearbySearchSettings;
import com.skobbler.ngx.search.SKOnelineSearchSettings;
import com.skobbler.ngx.search.SKSearchListener;
import com.skobbler.ngx.search.SKSearchManager;

import java.util.HashMap;

/**
 * Class which handles search methods.
 */
public class SKToolsSearchServiceManager {

    private HashMap<String, Object> categoryMap;
    private Context context;


    public SKToolsSearchServiceManager(Context context) {
        this.context = context;

    }

    /**
     * Initialize SKCategories for nearby search result
     */
    private void initCategories() {
        categoryMap = new HashMap<>();
        categoryMap.put(context.getString(R.string.category_food_type), SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_FOOD);
        categoryMap.put(context.getString(R.string.category_health_type), SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_HEALTH);
        categoryMap.put(context.getString(R.string.category_leisure_type), SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_LEISURE);
        categoryMap.put(context.getString(R.string.category_nightlife_type), SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_NIGHTLIFE);
        categoryMap.put(context.getString(R.string.category_public_type), SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_PUBLIC);
        categoryMap.put(context.getString(R.string.category_service_type), SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_SERVICES);
        categoryMap.put(context.getString(R.string.category_shopping_type), SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_SHOPPING);
        categoryMap.put(context.getString(R.string.category_sleeping_type), SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_ACCOMODATION);
        categoryMap.put(context.getString(R.string.category_transport_type), SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_TRANSPORT);
    }

    public Object getCategoryForString(String term) {
        initCategories();
        return categoryMap.get(term);
    }

    /**
     * Makes a request to the name browser
     *
     */
    public void nbCategorySearch(SKToolsSearchObject searchObject, SKSearchListener listener) {
        SKSearchManager searchManager = new SKSearchManager(listener);

        if (searchObject.getSearchCategories() != null) {
            SKNearbySearchSettings skNearbySearchSettings = new SKNearbySearchSettings();
            skNearbySearchSettings.setLocation(searchObject.getLocation());
            skNearbySearchSettings.setSearchMode(SKToolsUtils.isInternetAvailable(context) ? SKSearchManager.SKSearchMode.ONLINE : SKSearchManager.SKSearchMode.OFFLINE);
            skNearbySearchSettings.setSearchCategories(searchObject.getSearchCategories());
            skNearbySearchSettings.setRadius(searchObject.getRadius());
            skNearbySearchSettings.setSearchResultSortType(SKNearbySearchSettings.SKSearchResultSortType.MATCH_SORT);
            skNearbySearchSettings.setSearchType(SKNearbySearchSettings.SKSearchType.POIS);
            skNearbySearchSettings.setSearchResultsNumber(searchObject.getItemsPerPage());

            searchManager.nearbySearch(skNearbySearchSettings);
        }else {
            searchManager.onelineSearch(getOneLineSearchSettings(searchObject.getSearchTerm(),searchObject.getLocation()));

        }
    }

    /**
     * Return  input for a oneline search.
     * @param term
     * @param coordinate
     * @return
     */
    private SKOnelineSearchSettings getOneLineSearchSettings(String term, SKCoordinate coordinate) {
        SKOnelineSearchSettings onelineSearchSettings = new SKOnelineSearchSettings(term,  SKToolsUtils.isInternetAvailable(context)?  SKSearchManager.SKSearchMode.ONLINE :  SKSearchManager.SKSearchMode.OFFLINE);
        onelineSearchSettings.setGpsCoordinates(coordinate);
        onelineSearchSettings.setOnlineGeocoder(SKOnelineSearchSettings.SKGeocoderType.MAP_SEARCH_OSM);

        return onelineSearchSettings;
    }

    /**
     * Cancels the previously initiated search.
     */
    public void cancelSearch(SKSearchListener listener){
        SKSearchManager searchManager = new SKSearchManager(listener);
        searchManager.cancelSearch();

    }


}


