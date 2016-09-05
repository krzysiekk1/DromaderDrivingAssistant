package com.skobbler.ngx.sdktools.onebox.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skobbler.ngx.R;
import com.skobbler.ngx.sdktools.onebox.listeners.OnItemSelectedListener;
import com.skobbler.ngx.sdktools.onebox.listeners.OnSeeMoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Adapter for the main categories.
 */
public class SKCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FOOTER_VIEW = 1;
    private HashMap<String, Integer> imageResources;

    List<CategoryListItem> categories;
    Context context;
    LayoutInflater inflater;
    private static OnItemSelectedListener onItemSelectedListener;
    public static OnSeeMoreListener onSeeMoreListener;

    public void setData(List<CategoryListItem> categories) {
        this.categories = new ArrayList<>();
        for (CategoryListItem categoryListItem : categories) {
            if (categoryListItem.isShowItem()) {
                this.categories.add(categoryListItem);
            }
        }
    }

    public SKCategoriesAdapter(Context context) {

        this.context = context;
        imageResources = new HashMap<>();
        inflater = LayoutInflater.from(context);

        imageResources.put(context.getResources().getString(R.string.category_food_type), R.drawable.onebox_cat_food_list_icon);
        imageResources.put(context.getResources().getString(R.string.category_health_type), R.drawable.onebox_cat_health_list_icon);
        imageResources.put(context.getResources().getString(R.string.category_leisure_type), R.drawable.onebox_cat_leisure_list_icon);
        imageResources.put(context.getResources().getString(R.string.category_nightlife_type), R.drawable.onebox_cat_nightlife_list_icon);
        imageResources.put(context.getResources().getString(R.string.category_public_type), R.drawable.onebox_cat_public_list_icon);
        imageResources.put(context.getResources().getString(R.string.category_service_type), R.drawable.onebox_cat_services_list_icon);
        imageResources.put(context.getResources().getString(R.string.category_shopping_type), R.drawable.onebox_cat_shopping_list_icon);
        imageResources.put(context.getResources().getString(R.string.category_sleeping_type), R.drawable.onebox_cat_sleeping_list_icon);
        imageResources.put(context.getResources().getString(R.string.category_transport_type), R.drawable.onebox_cat_transport_list_icon);
    }

    public void setOnItemClickListener(OnItemSelectedListener listener) {
        onItemSelectedListener = listener;
    }

    public void setOnSeeMoreListener(OnSeeMoreListener listener) {
        onSeeMoreListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        if (viewType == FOOTER_VIEW) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.onebox_see_more, parent, false);
            FooterViewHolder vh = new FooterViewHolder(v);
            return vh;
        }

        v = inflater.inflate(R.layout.onebox_simple_item, parent, false);
        CategoriesHolder vh = new CategoriesHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            if (holder instanceof CategoriesHolder) {
                CategoriesHolder vh = (CategoriesHolder) holder;
                if (categories.get(position).isShowItem()) {
                    vh.textItem.setText(categories.get(position).getText());
                    vh.imageItem.setImageResource(imageResources.get(categories.get(position).getText()));
                    vh.container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (onItemSelectedListener != null) {
                                onItemSelectedListener.onItemSelected(categories.get(position));
                            }
                        }
                    });
                }


            } else if (holder instanceof FooterViewHolder) {
                FooterViewHolder vh = (FooterViewHolder) holder;
                if (categoryListExpended) {
                    vh.textView.setVisibility(View.GONE);
                    categoryListExpended = false;
                } else {
                    vh.textView.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean categoryListExpended = false;

    public void updateList(List<CategoryListItem> data) {
        categories.clear();
        for(CategoryListItem categoryListItem : data){
            if(categoryListItem.isShowItem()){
                this.categories.add(categoryListItem);
            }
        }
        categoryListExpended = true;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (categories == null) {
            return 0;
        }

        if (categories.size() == 0) {
            return 1;
        }

        // Add extra view to show the footer view
        return categories.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == categories.size()) {
            //  add footer.
            return FOOTER_VIEW;
        }

        return super.getItemViewType(position);
    }


    public static class CategoriesHolder extends RecyclerView.ViewHolder {

        View container;
        ImageView imageItem;
        TextView textItem;


        public CategoriesHolder(View v) {
            super(v);
            container = v;
            textItem = (TextView) v.findViewById(R.id.list_item_text);
            imageItem = (ImageView) v.findViewById(R.id.list_item_image);

        }


    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public FooterViewHolder(final View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.see_more);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onSeeMoreListener != null) {
                        onSeeMoreListener.onSeeMoreClick(view);
                    }
                }
            });

        }

    }
}
