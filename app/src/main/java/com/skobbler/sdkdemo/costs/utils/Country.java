package com.skobbler.sdkdemo.costs.utils;

/**
 * Created by Krzysiek on 31.08.2016.
 */
public enum Country {

    NIEMCY ("Niemcy", "DE", 0),
    POLSKA ("Polska", "PL", 0);
    // ...

    private final String name;
    private final String code;
    private final int vignetteCost; // in EUR

    private Country(String name, String code, int vignetteCost) {
        this.name = name;
        this.code = code;
        this.vignetteCost = vignetteCost;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getVignetteCost(){
        return vignetteCost;
    }

}
