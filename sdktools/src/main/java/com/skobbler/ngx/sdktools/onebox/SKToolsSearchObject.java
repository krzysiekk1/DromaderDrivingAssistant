package com.skobbler.ngx.sdktools.onebox;

import com.skobbler.ngx.SKCoordinate;

/**
 * Stores input parameters for a search.
 */
public class SKToolsSearchObject {

    /**
     * Search radius in meters.
     */
    private short radius;

    /**
     * Specifies the search text (e.g. bar, hotel, restaurant).
     */
    private String searchTerm;

    /**
     * The location where the search takes place.
     */
    private SKCoordinate location = new SKCoordinate();

    /**
     * The code for the country. This is optional; if no country code is
     * specified the search is made on all the available packages, when
     * searching on device, and on entire world on server. Setting a value will
     * help for better results
     */
    private String countryCode;

    /**
     * Poi types
     */
    private int[] searchCategories;

    /**
     * Maximum search results number.
     */
    private int itemsPerPage;

    public SKToolsSearchObject(String searchTerm, SKCoordinate location) {
        this.searchTerm = searchTerm;
        this.location = location;
    }

    public SKToolsSearchObject(short radius, SKCoordinate location, int[] searchCategories, int itemsPerPage) {
        this.radius = radius;
        this.location = location;
        this.searchCategories = searchCategories;
        this.itemsPerPage = itemsPerPage;
    }

    public short getRadius() {
        return radius;
    }

    public void setRadius(short radius) {
        this.radius = radius;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public SKCoordinate getLocation() {
        return location;
    }

    public void setLocation(SKCoordinate location) {
        this.location = location;
    }

    public int[] getSearchCategories() {
        return searchCategories;
    }

    public void setSearchCategories(int[] searchCategories) {
        this.searchCategories = searchCategories;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }
}
