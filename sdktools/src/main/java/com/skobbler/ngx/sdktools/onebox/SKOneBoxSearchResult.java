package com.skobbler.ngx.sdktools.onebox;

import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.search.SKSearchResult;

/**
 * Class holding information for a search result retrieved by a search.
 */
public class SKOneBoxSearchResult {

    /**
     * Information for a search result
     */
    SKSearchResult searchResult;
    /**
     * The results that better match the exact input query
     * will be ranked higher than those that match against the grammatically or logical correct results.
     */
    int rankIndex;

    public SKOneBoxSearchResult(SKSearchResult searchResult, int rankIndex) {
        this.searchResult = searchResult;
        this.rankIndex = rankIndex;
    }

    public SKSearchResult getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(SKSearchResult searchResult) {
        this.searchResult = searchResult;
    }

    public int getRankIndex() {
        return rankIndex;
    }

    public void setRankIndex(int rankIndex) {
        this.rankIndex = rankIndex;
    }
}
