package com.skobbler.ngx.sdktools.onebox.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skobbler.ngx.R;
import com.skobbler.ngx.SKCategories;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.sdktools.onebox.SKOneBoxSearchResult;
import com.skobbler.ngx.sdktools.onebox.fragments.OneBoxManager;
import com.skobbler.ngx.sdktools.onebox.listeners.OnItemSelectedListener;
import com.skobbler.ngx.sdktools.onebox.listeners.OnListItemSelectedListener;
import com.skobbler.ngx.sdktools.onebox.utils.SKToolsUtils;
import com.skobbler.ngx.search.SKSearchResult;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Adapter for search results.
 */
public class SKSearchResultAdapter extends RecyclerView.Adapter<SKSearchResultAdapter.ResultsHolder> {


    List<SKOneBoxSearchResult> skSearchResults;
    private HashMap<SKCategories.SKPOIMainCategory, Integer> imageResources;
    private static OnItemSelectedListener onItemSelectedListener;

    public void setmOnClickListener(View.OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    private View.OnClickListener mOnClickListener;// = new MyOnClickListener();
    private OnListItemSelectedListener onListItemSelectedListener;

    public SKSearchResultAdapter(List<SKOneBoxSearchResult> results, Context context) {
        skSearchResults = results;
        imageResources = new HashMap<>();
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_FOOD, R.drawable.onebox_cat_food_list_icon);
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_HEALTH, R.drawable.onebox_cat_health_list_icon);
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_LEISURE, R.drawable.onebox_cat_leisure_list_icon);
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_NIGHTLIFE, R.drawable.onebox_cat_nightlife_list_icon);
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_PUBLIC, R.drawable.onebox_cat_public_list_icon);
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_SERVICES, R.drawable.onebox_cat_services_list_icon);
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_SHOPPING, R.drawable.onebox_cat_shopping_list_icon);
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_ACCOMODATION, R.drawable.onebox_cat_sleeping_list_icon);
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_TRANSPORT, R.drawable.onebox_cat_transport_list_icon);

    }

    @Override
    public ResultsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.onebox_two_line_item, parent, false);
        v.setOnClickListener(mOnClickListener);
        return new ResultsHolder(v);
    }


    @Override
    public void onBindViewHolder(final ResultsHolder holder, int position) {
        final SKOneBoxSearchResult resultObject = skSearchResults.get(position);
        final  SKSearchResult result = resultObject.getSearchResult();

        if (!result.getName().isEmpty()) {
            holder.textItem.setText(result.getName());
            if (result.getMainCategory() != null) {
                holder.imageItem.setImageResource(imageResources.get(result.getMainCategory()));
            } else {
                holder.imageItem.setImageResource(R.drawable.onebox_osm_list_icon);

            }
            double distance = calculateDistance(result);
            if (distance > 0) {
                holder.distanceItem.setText(SKToolsUtils.convertAndformatDistance(distance, SKMaps.SKDistanceUnitType.DISTANCE_UNIT_KILOMETER_METERS));
            }
            StringBuilder resultSubtitle = new StringBuilder();
            if (result.getAddress().getZipCode() != null) {
                resultSubtitle.append(result.getAddress().getZipCode() + ", ");
            }
            if(result.getAddress().getStreet() != null){
                resultSubtitle.append(result.getAddress().getStreet() + ", ");
            }
            if (result.getAddress().getCity() != null) {
                resultSubtitle.append( result.getAddress().getCity() + ", ");
            }
            if (result.getAddress().getCountry() != null) {
                resultSubtitle.append(result.getAddress().getCountry());
            } else if(result.getAddress().getCountryCode() != null){
                resultSubtitle.append(result.getAddress().getCountryCode());
            }

            holder.subtitleItem.setText(resultSubtitle);

        }
    }

    public Object getItem(int position) {
        return skSearchResults.get(position);
    }

    private double calculateDistance(SKSearchResult result){
        double distance = -1;
        double[] coordinate = OneBoxManager.getCurrentPosition();
        if(coordinate !=null && result.getLocation()!=null){

            distance = SKToolsUtils.distanceBetween(new SKCoordinate(coordinate[0], coordinate[1]), result.getLocation());
        }
       return distance;
    }
    @Override
    public int getItemCount() {
        return skSearchResults.size();
    }

    public void setOnItemClickListener(OnItemSelectedListener listener) {
        onItemSelectedListener = listener;
    }
    public void setOnListItemClickListener(OnListItemSelectedListener listener) {
        onListItemSelectedListener = listener;
    }

    public void sort(final int sortType) {
        switch (sortType){
            case OneBoxManager.SORT_NAME:
                Collections.sort(skSearchResults, new Comparator<SKOneBoxSearchResult>() {
                    @Override
                    public int compare(SKOneBoxSearchResult first, SKOneBoxSearchResult second) {
                        return first.getSearchResult().getName().compareTo(second.getSearchResult().getName());
                    }
                });
                break;
            case OneBoxManager.SORT_DISTANCE:
                Collections.sort(skSearchResults, new Comparator<SKOneBoxSearchResult>() {
                    @Override
                    public int compare(SKOneBoxSearchResult first, SKOneBoxSearchResult second) {
                        double firstDistance = calculateDistance(first.getSearchResult());
                        double secondDistance = calculateDistance(second.getSearchResult());

                        return Double.compare(firstDistance, secondDistance);
                    }
                });
                break;
            case OneBoxManager.SORT_RANK:
                Collections.sort(skSearchResults, new Comparator<SKOneBoxSearchResult>() {
                    @Override
                    public int compare(SKOneBoxSearchResult first, SKOneBoxSearchResult second) {
                        return  first.getRankIndex() - second.getRankIndex();
                    }
                });
                break;
        }

        notifyDataSetChanged();
    }

    public static class ResultsHolder extends RecyclerView.ViewHolder {
        View container;

        ImageView imageItem;

        TextView textItem;

        TextView subtitleItem;

        TextView distanceItem;

        public ResultsHolder(View v) {
            super(v);
            container = v;
            textItem = (TextView) v.findViewById(R.id.list_item_text);
            imageItem = (ImageView) v.findViewById(R.id.list_item_image);
            subtitleItem = (TextView) v.findViewById(R.id.list_item_subtitle);
            distanceItem = (TextView) v.findViewById(R.id.list_item_distance);
        }

    }
}
