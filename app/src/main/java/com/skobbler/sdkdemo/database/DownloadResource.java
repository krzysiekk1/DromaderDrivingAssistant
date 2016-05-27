package com.skobbler.sdkdemo.database;

import com.skobbler.ngx.sdktools.download.SKToolsDownloadItem;

/**
 * Defines a resource that will be DOWNLOADED (e.g map, sound file)
 * @author CatalinM
 * @version $Revision$
 */
public abstract class DownloadResource {

    /**
     * resource code
     */
    protected String code;

    /**
     * storage path where resource will be downloaded
     */
    protected String downloadPath;

    /**
     * resource state (e.g. NOT_QUEUED, QUEUED, DOWNLOADING, ZIPPED, INSTALLING, DOWNLOADED)
     */
    private byte downloadState;

    /**
     * total number of DOWNLOADED bytes
     */
    private long noDownloadedBytes;

    /**
     * @return the resource code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the resource code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the download path for current resource
     */
    public String getDownloadPath() {
        return downloadPath;
    }

    /**
     * @param downloadPath download path for current resource
     */
    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    /**
     * @return download state for current resource
     */
    public byte getDownloadState() {
        return downloadState;
    }

    /**
     * @param downloadState download state for current resource
     */
    public void setDownloadState(byte downloadState) {
        this.downloadState = downloadState;
    }

    /**
     * gets the total number of DOWNLOADED bytes from current resource
     */
    public long getNoDownloadedBytes() {
        return noDownloadedBytes;
    }

    /**
     * @param noDownloadedBytes the total number of DOWNLOADED bytes from current resource
     */
    public void setNoDownloadedBytes(long noDownloadedBytes) {
        this.noDownloadedBytes = noDownloadedBytes;
    }

    /**
     * @return a SKToolsDownloadItem from current object
     */
    public abstract SKToolsDownloadItem toDownloadItem();
}
