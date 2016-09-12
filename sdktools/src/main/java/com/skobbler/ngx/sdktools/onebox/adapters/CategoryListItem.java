package com.skobbler.ngx.sdktools.onebox.adapters;

/**
 * Class that holds category list items(e.g Food, Transport, Services etc.)
 */
public class CategoryListItem {
    private String text;
    boolean showItem;

    public CategoryListItem(String text, boolean showItem) {
        this.text = text;
        this.showItem = showItem;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isShowItem() {
        return showItem;
    }

    public void setShowItem(boolean showItem) {
        this.showItem = showItem;
    }

    @Override
    public String toString() {
        return "Category " + text + " show item = " + showItem;
    }
}
