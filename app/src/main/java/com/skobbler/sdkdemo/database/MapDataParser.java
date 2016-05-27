package com.skobbler.sdkdemo.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import com.google.gson.stream.JsonReader;

/**
 * This class provides methods for parsing the "Maps" json file
 * Created by CatalinM on 11/11/2014.
 */
public class MapDataParser {

    /**
     * names for maps items tags
     */
    private static final String REGIONS_ID = "regions";

    private static final String REGION_CODE_ID = "regionCode";

    private static final String SUB_REGIONS_ID = "subRegions";

    private static final String SUB_REGION_CODE_ID = "subRegionCode";

    private static final String VERSION_ID = "version";

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

    private static final String XML_VERSION_ID = "xmlVersion";

    private static final String WORLD_ID = "world";

    private static final String CONTINENTS_ID = "continents";

    private static final String COUNTRIES_ID = "countries";

    private static final String CONTINENT_CODE_ID = "continentCode";

    private static final String COUNTRY_CODE_ID = "countryCode";

    private static final String CITY_CODES_ID = "cityCodes";

    private static final String CITY_CODE_ID = "cityCode";

    private static final String STATE_CODES_ID = "stateCodes";

    private static final String STATE_CODE_ID = "stateCode";

    /**
     * parses maps JSON data
     * @param maps a list of SKToolsDownloadResource items that represents the maps defined in JSON file
     * @param mapsItemsCodes a map representing the maps hierarchy defined in JSON file
     * @param regionItemsCodes a map representing the regions hierarchy defined in JSON file
     * @param inputStream input stream from JSON file
     * @throws java.io.IOException
     */
    public void parseMapJsonData(List<MapDownloadResource> maps, Map<String, String> mapsItemsCodes, Map<String, String> regionItemsCodes,
                                 InputStream inputStream) throws IOException {
        final JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            if (key != null) {
                if (key.equals(VERSION_ID) || key.equals(XML_VERSION_ID)) {
                    reader.skipValue();
                } else if (key.equals(PACKAGES_ID)) {
                    readMapsDetails(maps, reader);
                } else if (key.equals(WORLD_ID)) {
                    reader.beginObject();
                } else if (key.equals(CONTINENTS_ID)) {
                    readWorldHierarchy(mapsItemsCodes, reader);
                    reader.endObject();
                } else if (key.equals(REGIONS_ID)) {
                    readRegionsDetails(regionItemsCodes, reader);
                }
            }
        }
        reader.endObject();
    }

    /**
     * read regions details list
     * @param regionItemsCodes a map representing the regions hierarchy defined in JSON file
     * @param reader JSON file reader
     * @throws java.io.IOException
     */
    private void readRegionsDetails(Map<String, String> regionItemsCodes, JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readCurrentRegionDetails(regionItemsCodes, reader);
        }
        reader.endArray();
    }

    /**
     * read regions details list
     * @param regionItemsCodes a map representing the regions hierarchy defined in JSON file
     * @param reader JSON file reader
     * @throws java.io.IOException
     */
    private void readCurrentRegionDetails(Map<String, String> regionItemsCodes, JsonReader reader) throws IOException {
        reader.beginObject();
        String currentRegionCode = null;
        while (reader.hasNext()) {
            String key = reader.nextName();
            if (key != null) {
                if (key.equals(REGION_CODE_ID)) {
                    currentRegionCode = reader.nextString();
                } else if (key.equals(SUB_REGIONS_ID)) {
                    if (currentRegionCode != null) {
                        readSubRegionsForCurrentRegion(regionItemsCodes, currentRegionCode, reader);
                    }
                }
            }
        }
        reader.endObject();
    }

    /**
     * read sub-regions for current region
     * @param regionItemsCodes a map representing the regions hierarchy defined in JSON file
     * @param currentRegionCode current region code
     * @param reader JSON file reader
     * @throws java.io.IOException
     */
    private void readSubRegionsForCurrentRegion(Map<String, String> regionItemsCodes, String currentRegionCode, JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            String key = reader.nextName();
            if (key != null) {
                if (key.equals(SUB_REGION_CODE_ID)) {
                    String subRegionCode = reader.nextString();
                    if (subRegionCode != null) {
                        regionItemsCodes.put(subRegionCode, currentRegionCode);
                    }
                }
            }
            reader.endObject();
        }
        reader.endArray();
    }

    /**
     * read maps details list
     * @param maps a list of maps objects that will be read from JSON file
     * @param reader JSON file reader
     * @throws java.io.IOException
     */
    private void readMapsDetails(List<MapDownloadResource> maps, JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readCurrentMapDetails(maps, reader);
        }
        reader.endArray();
    }

    /**
     * read current map details
     * @param maps a list of maps objects that will be read from JSON file
     * @param reader JSON file reader
     * @throws java.io.IOException
     */
    private void readCurrentMapDetails(List<MapDownloadResource> maps, JsonReader reader) throws IOException {
        MapDownloadResource currentMap = new MapDownloadResource();
        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            if (key != null) {
                if (key.equals(PACKAGE_CODE_ID)) {
                    currentMap.setCode(reader.nextString());
                } else if (key.equals(TYPE_ID)) {
                    currentMap.setSubType(getMapType(reader.nextInt()));
                } else if (key.equals(LANGUAGES_ID)) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        readCurrentMapNames(currentMap, reader);
                    }
                    reader.endArray();
                } else if (key.equals(BBOX_ID)) {
                    readCurrentMapBoundingBoxDetails(currentMap, reader);
                } else if (key.equals(SKM_SIZE_ID)) {
                    currentMap.setSkmFileSize(reader.nextLong());
                } else if (key.equals(FILE_ID)) {
                    currentMap.setSkmFilePath(reader.nextString());
                } else if (key.equals(NB_ZIP_ID)) {
                    currentMap.setZipFilePath(reader.nextString());
                } else if (key.equals(UNZIP_SIZE_ID)) {
                    currentMap.setUnzippedFileSize(reader.nextLong());
                } else if (key.equals(TEXTURE_ID)) {
                    readCurrentMapTXGDetails(currentMap, reader);
                } else if (key.equals(SIZE_ID)) {
                    currentMap.setSkmAndZipFilesSize(reader.nextLong());
                } else {
                    // for now, we skip the elevation tag
                    reader.skipValue();
                }
            }
        }
        reader.endObject();

        if ((currentMap.getCode() != null) && (currentMap.getSubType() != null)) {
            removeNullValuesIfExist(currentMap);
            maps.add(currentMap);
        }
    }

    /**
     * read current map names
     * @param currentMap current map whose name will be read from JSON file
     * @param reader JSON file reader
     * @throws java.io.IOException
     */
    private void readCurrentMapNames(MapDownloadResource currentMap, JsonReader reader) throws IOException {
        String currentMapName = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            if (key != null) {
                if (key.equals(TL_NAME_ID)) {
                    currentMapName = reader.nextString();
                } else if (key.equals(LNG_CODE_ID)) {
                    if (currentMapName != null) {
                        currentMap.setName(currentMapName, reader.nextString());
                    }
                }
            }
        }
        reader.endObject();
    }

    /**
     * read current map TXG details
     * @param currentMap current map whose TXG details will be read from JSON file
     * @param reader JSON file reader
     * @throws java.io.IOException
     */
    private void readCurrentMapTXGDetails(MapDownloadResource currentMap, JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            if (key != null) {
                if (key.equals(TEXTURES_BIG_FILE_ID)) {
                    currentMap.setTXGFilePath(reader.nextString());
                } else if (key.equals(SIZE_BIG_FILE_ID)) {
                    currentMap.setTXGFileSize(reader.nextLong());
                } else {
                    // for now, we skip the tags referring ZIP files details related to TXG files
                    reader.skipValue();
                }
            }
        }
        reader.endObject();
    }

    /**
     * read current map bounding box details
     * @param currentMap current map whose bounding box will be read from JSON file
     * @param reader JSON file reader
     * @throws java.io.IOException
     */
    private void readCurrentMapBoundingBoxDetails(MapDownloadResource currentMap, JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            if (key != null) {
                if (key.equals(LAT_MAX_ID)) {
                    currentMap.setBbLatMax(reader.nextDouble());
                } else if (key.equals(LAT_MIN_ID)) {
                    currentMap.setBbLatMin(reader.nextDouble());
                } else if (key.equals(LONG_MAX_ID)) {
                    currentMap.setBbLongMax(reader.nextDouble());
                } else if (key.equals(LONG_MIN_ID)) {
                    currentMap.setBbLongMin(reader.nextDouble());
                }
            }
        }
        reader.endObject();
    }

    /**
     * read world hierarchy for maps items
     * @param mapsItemsCodes a map of type (code ; parentCode) that contains all maps items codes
     * @param reader JSON file reader
     * @throws java.io.IOException
     */
    private void readWorldHierarchy(Map<String, String> mapsItemsCodes, JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readContinentsHierarchy(mapsItemsCodes, reader);
        }
        reader.endArray();
    }

    /**
     * read continents hierarchy for maps items
     * @param mapsItemsCodes a map of type (code ; parentCode) that contains all maps items codes
     * @param reader JSON file reader
     * @throws java.io.IOException
     */
    private void readContinentsHierarchy(Map<String, String> mapsItemsCodes, JsonReader reader) throws IOException {
        String currentContinentCode = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            if (key != null) {
                if (key.equals(CONTINENT_CODE_ID)) {
                    currentContinentCode = reader.nextString();
                    if (currentContinentCode != null) {
                        mapsItemsCodes.put(currentContinentCode, "");
                    }
                } else if (key.equals(COUNTRIES_ID)) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        readCountriesHierarchy(mapsItemsCodes, currentContinentCode, reader);
                    }
                    reader.endArray();
                }
            }
        }
        reader.endObject();
    }

    /**
     * read countries hierarchy for maps items
     * @param mapsItemsCodes a map of type (code ; parentCode) that contains all maps items codes
     * @param currentContinentCode current continent code
     * @param reader JSON file reader
     * @throws java.io.IOException
     */
    private void readCountriesHierarchy(Map<String, String> mapsItemsCodes, String currentContinentCode, JsonReader reader) throws IOException {
        String currentCountryCode = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            if (key != null) {
                if (key.equals(COUNTRY_CODE_ID)) {
                    currentCountryCode = reader.nextString();
                    if ((currentContinentCode != null) && (currentCountryCode != null)) {
                        mapsItemsCodes.put(currentCountryCode, currentContinentCode);
                    }
                } else if (key.equals(CITY_CODES_ID)) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        readCitiesHierarchy(mapsItemsCodes, currentCountryCode, reader);
                    }
                    reader.endArray();
                } else if (key.equals(STATE_CODES_ID)) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        readStatesHierarchy(mapsItemsCodes, currentCountryCode, reader);
                    }
                    reader.endArray();
                }
            }
        }
        reader.endObject();
    }

    /**
     * read states hierarchy for maps items
     * @param mapsItemsCodes a map of type (code ; parentCode) that contains all maps items codes
     * @param currentCountryCode current country code
     * @param reader JSON file reader
     * @throws java.io.IOException
     */
    private void readStatesHierarchy(Map<String, String> mapsItemsCodes, String currentCountryCode, JsonReader reader) throws IOException {
        String currentStateCode = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            if (key != null) {
                if (key.equals(STATE_CODE_ID)) {
                    currentStateCode = reader.nextString();
                    if ((currentStateCode != null) && (currentCountryCode != null)) {
                        mapsItemsCodes.put(currentStateCode, currentCountryCode);
                    }
                } else if (key.equals(CITY_CODES_ID)) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        readCitiesHierarchy(mapsItemsCodes, currentStateCode, reader);
                    }
                    reader.endArray();
                }
            }
        }
        reader.endObject();
    }

    /**
     * read cities hierarchy for maps items
     * @param mapsItemsCodes a map of type (code ; parentCode) that contains all maps items codes
     * @param currentParentCode current parent code
     * @param reader JSON file reader
     * @throws java.io.IOException
     */
    private void readCitiesHierarchy(Map<String, String> mapsItemsCodes, String currentParentCode, JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            if (key != null) {
                if (key.equals(CITY_CODE_ID)) {
                    String currentCityCode = reader.nextString();
                    if ((currentCityCode != null) && (currentParentCode != null)) {
                        mapsItemsCodes.put(currentCityCode, currentParentCode);
                    }
                }
            }
        }
        reader.endObject();
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