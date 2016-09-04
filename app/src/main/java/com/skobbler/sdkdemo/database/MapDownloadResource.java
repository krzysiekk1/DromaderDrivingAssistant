package com.skobbler.sdkdemo.database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import com.skobbler.ngx.packages.SKPackageManager;
import com.skobbler.ngx.packages.SKPackageURLInfo;
import com.skobbler.ngx.sdktools.download.SKToolsDownloadItem;
import com.skobbler.ngx.sdktools.download.SKToolsDownloadManager;
import com.skobbler.ngx.sdktools.download.SKToolsFileDownloadStep;

/**
 * Defines a map resource that will be DOWNLOADED
 * @author CatalinM
 * @version $Revision$
 */
public class MapDownloadResource extends DownloadResource implements Comparable<MapDownloadResource> {

    /**
     * resource parent code
     */
    private String parentCode;

    /**
     * resource name in different languages
     */
    private java.util.Map<String, String> names;

    /**
     * resource sub-type (e.g. continent, country, city, state for map resource)
     */
    private String subType;

    /**
     * SKM file size
     */
    private long skmFileSize;

    /**
     * SKM + ZIP file size
     */
    private long skmAndZipFilesSize;

    /**
     * txg file size
     */
    private long txgFileSize;

    /**
     * UNZIPPED file size for zip file
     */
    private long unzippedFileSize;

    /**
     * .SKM file path
     */
    private String skmFilePath;

    /**
     * .ZIP file path
     */
    private String zipFilePath;

    /**
     * .TXG file path
     */
    private String txgFilePath;

    /**
     * bounding box minimum longitude
     */
    private double bbLongMin;

    /**
     * bounding box maximum longitude
     */
    private double bbLongMax;

    /**
     * bounding box minimum latitude
     */
    private double bbLatMin;

    /**
     * bounding box maximum latitude
     */
    private double bbLatMax;

    /**
     * flag resource id
     */
    private int flagID;

    /**
     * constructs an object of SKDownloadResource type
     */
    public MapDownloadResource() {
        names = new LinkedHashMap<String, String>();
    }

    /**
     * @return the resource parent code
     */
    public String getParentCode() {
        return parentCode;
    }

    /**
     * @param parentCode the resource parent code
     */
    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    /**
     * @return the resource name
     */
    public String getName() {
        String localLanguage = Locale.getDefault().getLanguage();
        if (localLanguage.startsWith(MapsDAO.ENGLISH_LANGUAGE_CODE)) {
            localLanguage = MapsDAO.ENGLISH_LANGUAGE_CODE;
        }
        if (names != null) {
            if (names.get(localLanguage) == null) {
                return names.get(Locale.ENGLISH.getLanguage());
            } else {
                return names.get(localLanguage);
            }
        } else {
            return "";
        }
    }

    /**
     * sets the name in all languages
     * @param newNames resource names in all languages
     */
    public void setNames(String newNames) {
        this.names = new LinkedHashMap<String, String>();
        final String[] keyValuePairs = newNames.split(";");
        for (final String keyValue : keyValuePairs) {
            final String[] newName = keyValue.split("=");
            this.names.put(newName[0], newName[1]);
        }
    }

    /**
     * @param name the resource name for a certain language
     */
    public void setName(String name, String language) {
        names.put(language, name);
    }

    /**
     * @return names in all languages for current resource
     */
    public java.util.Map<String, String> getNames() {
        return names;
    }

    /**
     * @return the resource sub-type
     */
    public String getSubType() {
        return subType;
    }

    /**
     * @param subType the resource sub-type
     */
    public void setSubType(String subType) {
        this.subType = subType;
    }

    /**
     * @return SKM file size
     */
    public long getSkmFileSize() {
        return skmFileSize;
    }

    /**
     * @param skmFileSize SKM file size
     */
    public void setSkmFileSize(long skmFileSize) {
        this.skmFileSize = skmFileSize;
    }

    /**
     * @return the SKM + ZIP files size
     */
    public long getSkmAndZipFilesSize() {
        return skmAndZipFilesSize;
    }

    /**
     * @param skmAndZipFilesSize the SKM + ZIP files size
     */
    public void setSkmAndZipFilesSize(long skmAndZipFilesSize) {
        this.skmAndZipFilesSize = skmAndZipFilesSize;
    }

    /**
     * @return the TXG file size
     */
    public long getTXGFileSize() {
        return txgFileSize;
    }

    /**
     * @param txgFileSize the TXG file size
     */
    public void setTXGFileSize(long txgFileSize) {
        this.txgFileSize = txgFileSize;
    }

    /**
     * @return the unzippedSize for ZIP file
     */
    public long getUnzippedFileSize() {
        return unzippedFileSize;
    }

    /**
     * @param unzippedFileSize the unzippedSize for ZIP file
     */
    public void setUnzippedFileSize(long unzippedFileSize) {
        this.unzippedFileSize = unzippedFileSize;
    }

    /**
     * @return the SKM file path
     */
    public String getSKMFilePath() {
        return skmFilePath;
    }

    /**
     * @param skmFilePath the SKM file path
     */
    public void setSkmFilePath(String skmFilePath) {
        this.skmFilePath = skmFilePath;
    }

    /**
     * @return the ZIP file path
     */
    public String getZipFilePath() {
        return zipFilePath;
    }

    /**
     * @param zipFilePath the ZIP file path
     */
    public void setZipFilePath(String zipFilePath) {
        this.zipFilePath = zipFilePath;
    }

    /**
     * @return the TXG file path
     */
    public String getTXGFilePath() {
        return txgFilePath;
    }

    /**
     * @param txgFilePath the TXG file path
     */
    public void setTXGFilePath(String txgFilePath) {
        this.txgFilePath = txgFilePath;
    }

    /**
     * @return bounding-box longitude minim
     */
    public double getBbLongMin() {
        return bbLongMin;
    }

    /**
     * @param bbLongMin bounding-box longitude minim
     */
    public void setBbLongMin(double bbLongMin) {
        this.bbLongMin = bbLongMin;
    }

    /**
     * @return bounding-box longitude maxim
     */
    public double getBbLongMax() {
        return bbLongMax;
    }

    /**
     * @param bbLongMax bounding-box longitude maxim
     */
    public void setBbLongMax(double bbLongMax) {
        this.bbLongMax = bbLongMax;
    }

    /**
     * @return bounding-box latitude minim
     */
    public double getBbLatMin() {
        return bbLatMin;
    }

    /**
     * @param bbLatMin bounding-box latitude minim
     */
    public void setBbLatMin(double bbLatMin) {
        this.bbLatMin = bbLatMin;
    }

    /**
     * @return bounding-box latitude maxim
     */
    public double getBbLatMax() {
        return bbLatMax;
    }

    /**
     * @param bbLatMax bounding-box latitude maxim
     */
    public void setBbLatMax(double bbLatMax) {
        this.bbLatMax = bbLatMax;
    }

    /**
     * @return the flag ID for current resource
     */
    public int getFlagID() {
        return flagID;
    }

    /**
     * @param flagID flag ID for current resource
     */
    public void setFlagID(int flagID) {
        this.flagID = flagID;
    }

    @Override
    public int compareTo(MapDownloadResource another) {
        String firstName = getName(), secondName = (another != null) ? another.getName() : null;
        if ((firstName != null) && (secondName != null)) {
            return firstName.toLowerCase().compareTo(secondName.toLowerCase());
        } else if (firstName != null) {
            return -1;
        } else if (secondName != null) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object another) {
        if (another == null) {
            return false;
        } else if (!(another instanceof MapDownloadResource)) {
            return false;
        } else {
            MapDownloadResource anotherResource = (MapDownloadResource) another;
            return this.getCode().equals(anotherResource.getCode());
        }
    }

    @Override
    public SKToolsDownloadItem toDownloadItem() {
        SKPackageURLInfo info = SKPackageManager.getInstance().getURLInfoForPackageWithCode(code, skmFilePath.startsWith("custom-packages"));
        List<SKToolsFileDownloadStep> downloadSteps = new ArrayList<SKToolsFileDownloadStep>();
        downloadSteps.add(new SKToolsFileDownloadStep(info.getMapURL(), new StringBuilder(downloadPath).append(code)
                .append(SKToolsDownloadManager.SKM_FILE_EXTENSION).toString(), skmFileSize));
        if (txgFileSize != 0) {
            downloadSteps.add(new SKToolsFileDownloadStep(info.getTexturesURL(), new StringBuilder(downloadPath).append(code)
                    .append(SKToolsDownloadManager.TXG_FILE_EXTENSION).toString(), txgFileSize));
        }
        if (unzippedFileSize != 0) {
            downloadSteps.add(new SKToolsFileDownloadStep(info.getNameBrowserFilesURL(), new StringBuilder(downloadPath).append(code)
                    .append(SKToolsDownloadManager.ZIP_FILE_EXTENSION).toString(), (skmAndZipFilesSize - skmFileSize)));
        }
        SKToolsDownloadItem currentItem = new SKToolsDownloadItem(code, downloadSteps, getDownloadState(), (unzippedFileSize != 0), true);
        currentItem.setNoDownloadedBytes(getNoDownloadedBytes());
        return currentItem;
    }
}