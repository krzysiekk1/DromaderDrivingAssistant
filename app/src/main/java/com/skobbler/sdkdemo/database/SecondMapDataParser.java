package com.skobbler.sdkdemo.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.skobbler.ngx.util.SKLogging;

/**
 * This class provides methods for parsing the "Maps" json file
 * Created by CatalinM on 11/11/2014.
 */
public class SecondMapDataParser {

    /**
     * names for maps items tags
     */
    private static final String REGIONS_ID = "regions";

    private static final String REGION_CODE_ID = "regionCode";

    private static final String SUB_REGIONS_ID = "subRegions";

    private static final String SUB_REGION_CODE_ID = "subRegionCode";

    private static final String PACKAGES_ID = "packages";

    private static final String PACKAGE_CODE_ID = "packageCode";

    private static final String FILE_ID = "file";

    private static final String SIZE_ID = "size";

    private static final String UNZIP_SIZE_ID = "unzipsize";

    private static final String TYPE_ID = "type";

    private static final String LANGUAGES_ID = "languages";

    private static final String TL_NAME_ID = "tlName";

    private static final String LNG_CODE_ID = "lngCode";

    private static final String BBOX_ID = "bbox";

    private static final String LAT_MIN_ID = "latMin";

    private static final String LAT_MAX_ID = "latMax";

    private static final String LONG_MIN_ID = "longMin";

    private static final String LONG_MAX_ID = "longMax";

    private static final String SKM_SIZE_ID = "skmsize";

    private static final String NB_ZIP_ID = "nbzip";

    private static final String TEXTURE_ID = "texture";

    private static final String TEXTURES_BIG_FILE_ID = "texturesbigfile";

    private static final String SIZE_BIG_FILE_ID = "sizebigfile";

    private static final String WORLD_ID = "world";

    private static final String CONTINENTS_ID = "continents";

    private static final String COUNTRIES_ID = "countries";

    private static final String CONTINENT_CODE_ID = "continentCode";

    private static final String COUNTRY_CODE_ID = "countryCode";

    private static final String CITY_CODES_ID = "cityCodes";

    private static final String CITY_CODE_ID = "cityCode";

    private static final String STATE_CODES_ID = "stateCodes";

    private static final String STATE_CODE_ID = "stateCode";

    private static final String TAG = "SKToolsMapDataParser";

    /**
     * parses maps JSON data
     * @param maps a list of SKToolsDownloadResource items that represents the maps defined in JSON file
     * @param mapsItemsCodes a map representing the maps hierarchy defined in JSON file
     * @param regionItemsCodes a map representing the regions hierarchy defined in JSON file
     * @param inputStream input stream from JSON file
     * @throws java.io.IOException
     */
    public void parseMapJsonData(List<MapDownloadResource> maps, Map<String, String> mapsItemsCodes, Map<String, String> regionItemsCodes,
                                 InputStream inputStream) throws JSONException, IOException {
        System.out.println("Catalin ; start parsing !!!");
        long startTime = System.currentTimeMillis();
        JSONObject reader = new JSONObject(convertJSONFileContentToAString(inputStream));
        JSONArray regionsArray = reader.getJSONArray(REGIONS_ID);
        if (regionsArray != null) {
            readUSRegionsHierarchy(regionItemsCodes, regionsArray);
        }
        JSONArray packagesArray = reader.getJSONArray(PACKAGES_ID);
        if (packagesArray != null) {
            readMapsPackages(maps, packagesArray);
        }
        JSONObject worldObject = reader.getJSONObject(WORLD_ID);
        if (worldObject != null) {
            JSONArray continentsArray = worldObject.getJSONArray(CONTINENTS_ID);
            if (continentsArray != null) {
                readWorldHierarchy(mapsItemsCodes, continentsArray);
            }
        }
        /*-for (Map.Entry<String, String> currentEntry : mapsItemsCodes.entrySet()) {
            System.out.println("Catalin ; key = " + currentEntry.getKey() + " ; value = " + currentEntry.getValue());
        }*/
        System.out.println("Catalin ; total loading time = " + (System.currentTimeMillis() - startTime) + " ; maps size = " + maps.size());
    }

    /**
     * read the JSON file and converts it to String using StringWriter
     * @param inputStream JSON file stream
     * @throws java.io.IOException
     */
    private String convertJSONFileContentToAString(InputStream inputStream) throws IOException {
        char[] buffer = new char[1024];
        Writer stringWriter = new StringWriter();
        try {
            Reader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int n;
            while ((n = bufferedReader.read(buffer)) != -1) {
                stringWriter.write(buffer, 0, n);
            }
        } finally {
            stringWriter.close();
        }
        return stringWriter.toString();
    }

    /**
     * read maps packages list
     * @param maps a list of maps objects that will be read from JSON file
     * @param packagesArray packages array
     */
    private void readMapsPackages(List<MapDownloadResource> maps, JSONArray packagesArray) {
        for (int i = 0; i < packagesArray.length(); i++) {
            JSONObject currentPackageObject = null;
            try {
                currentPackageObject = packagesArray.getJSONObject(i);
            } catch (JSONException ex) {
                SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
            }
            if (currentPackageObject != null) {
                MapDownloadResource currentMap = new MapDownloadResource();
                try {
                    currentMap.setCode(currentPackageObject.getString(PACKAGE_CODE_ID));
                } catch (JSONException ex) {
                    SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                }
                try {
                    currentMap.setSubType(getMapType(currentPackageObject.getInt(TYPE_ID)));
                } catch (JSONException ex) {
                    SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                }
                try {
                    JSONArray currentMapNames = currentPackageObject.getJSONArray(LANGUAGES_ID);
                    if (currentMapNames != null) {
                        for (int j = 0; j < currentMapNames.length(); j++) {
                            JSONObject currentMapNameObject = currentMapNames.getJSONObject(j);
                            if (currentMapNameObject != null) {
                                String currentMapName = currentMapNameObject.getString(TL_NAME_ID);
                                if (currentMapName != null) {
                                    currentMap.setName(currentMapName, currentMapNameObject.getString(LNG_CODE_ID));
                                }
                            }
                        }
                    }
                } catch (JSONException ex) {
                    SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                }
                try {
                    JSONObject currentMapBoundingBox = currentPackageObject.getJSONObject(BBOX_ID);
                    if (currentMapBoundingBox != null) {
                        currentMap.setBbLatMax(currentMapBoundingBox.getDouble(LAT_MAX_ID));
                        currentMap.setBbLatMin(currentMapBoundingBox.getDouble(LAT_MIN_ID));
                        currentMap.setBbLongMax(currentMapBoundingBox.getDouble(LONG_MAX_ID));
                        currentMap.setBbLongMin(currentMapBoundingBox.getDouble(LONG_MIN_ID));
                    }
                } catch (JSONException ex) {
                    SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                }
                try {
                    currentMap.setSkmFileSize(currentPackageObject.getLong(SKM_SIZE_ID));
                } catch (JSONException ex) {
                    SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                }
                try {
                    currentMap.setSkmFilePath(currentPackageObject.getString(FILE_ID));
                } catch (JSONException ex) {
                    SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                }
                try {
                    currentMap.setZipFilePath(currentPackageObject.getString(NB_ZIP_ID));
                } catch (JSONException ex) {
                    SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                }
                try {
                    currentMap.setUnzippedFileSize(currentPackageObject.getLong(UNZIP_SIZE_ID));
                } catch (JSONException ex) {
                    SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                }
                try {
                    JSONObject currentMapTXGDetails = currentPackageObject.getJSONObject(TEXTURE_ID);
                    if (currentMapTXGDetails != null) {
                        currentMap.setTXGFilePath(currentMapTXGDetails.getString(TEXTURES_BIG_FILE_ID));
                        currentMap.setTXGFileSize(currentMapTXGDetails.getLong(SIZE_BIG_FILE_ID));
                    }
                } catch (JSONException ex) {
                    SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                }
                try {
                    currentMap.setSkmAndZipFilesSize(currentPackageObject.getLong(SIZE_ID));
                } catch (JSONException ex) {
                    SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                }
                if ((currentMap.getCode() != null) && (currentMap.getSubType() != null)) {
                    removeNullValuesIfExist(currentMap);
                    maps.add(currentMap);
                }
            }
        }
    }

    /**
     * read US regions hierarchy
     * @param regionItemsCodes a map representing the regions hierarchy defined in JSON file
     * @param regionsArray regions array
     * @throws org.json.JSONException
     */
    private void readUSRegionsHierarchy(Map<String, String> regionItemsCodes, JSONArray regionsArray) throws JSONException {
        for (int i = 0; i < regionsArray.length(); i++) {
            JSONObject currentRegionObject = regionsArray.getJSONObject(i);
            if (currentRegionObject != null) {
                String currentRegionCode = currentRegionObject.getString(REGION_CODE_ID);
                if (currentRegionCode != null) {
                    JSONArray subRegions = currentRegionObject.getJSONArray(SUB_REGIONS_ID);
                    if (subRegions != null) {
                        for (int j = 0; j < subRegions.length(); j++) {
                            JSONObject currentSubRegionObject = subRegions.getJSONObject(j);
                            if (currentSubRegionObject != null) {
                                String subRegionCode = currentSubRegionObject.getString(SUB_REGION_CODE_ID);
                                if (subRegionCode != null) {
                                    regionItemsCodes.put(subRegionCode, currentRegionCode);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * read world hierarchy for maps items
     * @param mapsItemsCodes a map of type (code ; parentCode) that contains all maps items codes
     * @param continentsArray continents array
     */
    private void readWorldHierarchy(Map<String, String> mapsItemsCodes, JSONArray continentsArray) {
        for (int i = 0; i < continentsArray.length(); i++) {
            try {
                JSONObject currentContinentObject = continentsArray.getJSONObject(i);
                if (currentContinentObject != null) {
                    try {
                        String currentContinentCode = currentContinentObject.getString(CONTINENT_CODE_ID);
                        if (currentContinentCode != null) {
                            mapsItemsCodes.put(currentContinentCode, "");
                            JSONArray countriesArray = null;
                            try {
                                countriesArray = currentContinentObject.getJSONArray(COUNTRIES_ID);
                            } catch (JSONException ex) {
                                SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                            }
                            if (countriesArray != null) {
                                readCountriesHierarchy(mapsItemsCodes, currentContinentCode, countriesArray);
                            }
                        }
                    } catch (JSONException ex) {
                        SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                    }
                }
            } catch (JSONException ex) {
                SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
            }
        }
    }

    /**
     * read countries hierarchy for maps items
     * @param mapsItemsCodes a map of type (code ; parentCode) that contains all maps items codes
     * @param currentContinentCode current continent code
     * @param countriesArray countries array
     */
    private void readCountriesHierarchy(Map<String, String> mapsItemsCodes, String currentContinentCode, JSONArray countriesArray) {
        for (int i = 0; i < countriesArray.length(); i++) {
            try {
                JSONObject currentCountryObject = countriesArray.getJSONObject(i);
                if (currentCountryObject != null) {
                    try {
                        String currentCountryCode = currentCountryObject.getString(COUNTRY_CODE_ID);
                        if ((currentContinentCode != null) && (currentCountryCode != null)) {
                            mapsItemsCodes.put(currentCountryCode, currentContinentCode);
                            try {
                                JSONArray citiesArray = currentCountryObject.getJSONArray(CITY_CODES_ID);
                                if (citiesArray != null) {
                                    readCitiesHierarchy(mapsItemsCodes, currentCountryCode, citiesArray);
                                }
                            } catch (JSONException ex) {
                                SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                            }
                            try {
                                JSONArray statesArray = currentCountryObject.getJSONArray(STATE_CODES_ID);
                                if (statesArray != null) {
                                    readStatesHierarchy(mapsItemsCodes, currentCountryCode, statesArray);
                                }
                            } catch (JSONException ex) {
                                SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                            }
                        }
                    } catch (JSONException ex) {
                        SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                    }
                }
            } catch (JSONException ex) {
                SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
            }
        }
    }

    /**
     * read states hierarchy for maps items
     * @param mapsItemsCodes a map of type (code ; parentCode) that contains all maps items codes
     * @param currentCountryCode current country code
     * @param statesArray states array
     */
    private void readStatesHierarchy(Map<String, String> mapsItemsCodes, String currentCountryCode, JSONArray statesArray) {
        for (int i = 0; i < statesArray.length(); i++) {
            try {
                JSONObject currentStateObject = statesArray.getJSONObject(i);
                if (currentStateObject != null) {
                    try {
                        String currentStateCode = currentStateObject.getString(STATE_CODE_ID);
                        if ((currentStateCode != null) && (currentCountryCode != null)) {
                            mapsItemsCodes.put(currentStateCode, currentCountryCode);
                            try {
                                JSONArray citiesArray = currentStateObject.getJSONArray(CITY_CODES_ID);
                                if (citiesArray != null) {
                                    readCitiesHierarchy(mapsItemsCodes, currentStateCode, citiesArray);
                                }
                            } catch (JSONException ex) {
                                SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                            }
                        }
                    } catch (JSONException ex) {
                        SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                    }
                }
            } catch (JSONException ex) {
                SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
            }
        }
    }

    /**
     * read cities hierarchy for maps items
     * @param mapsItemsCodes a map of type (code ; parentCode) that contains all maps items codes
     * @param currentParentCode current parent code
     * @param citiesArray cities array
     */
    private void readCitiesHierarchy(Map<String, String> mapsItemsCodes, String currentParentCode, JSONArray citiesArray) {
        for (int i = 0; i < citiesArray.length(); i++) {
            try {
                JSONObject currentCityObject = citiesArray.getJSONObject(i);
                if (currentCityObject != null) {
                    try {
                        String currentCityCode = currentCityObject.getString(CITY_CODE_ID);
                        if ((currentCityCode != null) && (currentParentCode != null)) {
                            mapsItemsCodes.put(currentCityCode, currentParentCode);
                        }
                    } catch (JSONException ex) {
                        SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
                    }
                }
            } catch (JSONException ex) {
                SKLogging.writeLog(TAG, ex.getMessage(), SKLogging.LOG_DEBUG);
            }
        }
    }

    /**
     * @param mapTypeInt an integer associated with map type
     * @return the String associated with map type
     */
    private String getMapType(int mapTypeInt) {
        switch (mapTypeInt) {
            case 0:
                return MapsDAO.COUNTRY_TYPE;
            case 1:
                return MapsDAO.CITY_TYPE;
            case 2:
                return MapsDAO.CONTINENT_TYPE;
            case 3:
                return MapsDAO.REGION_TYPE;
            case 4:
                return MapsDAO.STATE_TYPE;
            default:
                return "";
        }
    }

    /**
     * removes null attributes for current map
     * @param currentMap current map that is parsed
     */
    private void removeNullValuesIfExist(MapDownloadResource currentMap) {
        if (currentMap.getParentCode() == null) {
            currentMap.setParentCode("");
        }
        if (currentMap.getDownloadPath() == null) {
            currentMap.setDownloadPath("");
        }
        if (currentMap.getSKMFilePath() == null) {
            currentMap.setSkmFilePath("");
        }
        if (currentMap.getZipFilePath() == null) {
            currentMap.setZipFilePath("");
        }
        if (currentMap.getTXGFilePath() == null) {
            currentMap.setTXGFilePath("");
        }
    }
}