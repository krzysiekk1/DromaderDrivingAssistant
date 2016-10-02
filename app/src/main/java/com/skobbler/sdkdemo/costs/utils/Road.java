package com.skobbler.sdkdemo.costs.utils;

public class Road {

    private String nr;
    private String countryCode;

    public Road(String nr, String countryCode) {
        this.nr = nr;
        this.countryCode = countryCode;
    }

    public String getNr() {
        return nr;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
