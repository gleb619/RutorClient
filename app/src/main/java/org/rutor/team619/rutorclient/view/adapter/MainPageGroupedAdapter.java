package org.rutor.team619.rutorclient.view.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.rutor.team619.rutorclient.R;
import org.rutor.team619.rutorclient.model.MainGroupedPage;
import org.rutor.team619.rutorclient.model.Row;
import org.rutor.team619.rutorclient.model.TopicDetail;
import org.rutor.team619.rutorclient.util.Objects;
import org.rutor.team619.rutorclient.view.adapter.core.DefaultAdapter;
import org.rutor.team619.rutorclient.view.other.DefaultViewHolder;

import java.io.Serializable;

import butterknife.Bind;

/**
 * Created by BORIS on 31.10.2015.
 */
public class MainPageGroupedAdapter extends RecyclerView.Adapter<MainPageGroupedAdapter.ViewHolder> implements DefaultAdapter<MainGroupedPage> {

    private static final String TAG = MainPageGroupedAdapter.class.getName() + ":";

    private final int TYPE_HEADER = 2;
    private final int TYPE_ITEM = 1;
    private final ViewHolder.ViewHolderClickListener viewHolderClickListener;
    private final Resources resources;
    private final Context context;
    private MainGroupedPage mainGroupedPage;
    private int currentGroup;

    public MainPageGroupedAdapter(ViewHolder.ViewHolderClickListener viewHolderClickListener, Resources resources, Context context) {
        this.viewHolderClickListener = viewHolderClickListener;
        this.resources = resources;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        System.out.println("MainPageGroupedAdapter.onCreateViewHolder#viewType: " + viewType);
        if (viewType == TYPE_ITEM) {
            final View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main_page_plain, parent, false);
            return new ViewHolder(rowView, viewHolderClickListener);
        } else if (viewType == TYPE_HEADER) {
            View rowView = rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_grouped_header, parent, false);
            TypedValue tv = new TypedValue();
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.getDisplayMetrics());
            rowView.setMinimumHeight(actionBarHeight);
            return new ViewHolder(rowView);
        } else {
            throw new IllegalArgumentException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (Objects.isNull(getData())
                || Objects.isNull(getData().getGroups())
                || getData().getGroups().isEmpty()
                || Objects.isNull(getData().getGroups().get(currentGroup).getRows())
                || getData().getGroups().get(currentGroup).getRows().isEmpty()
                || isPositionHeader(position)) {
            return;
        }

        if (position > 0) {
            position--;
        }

        Row row = null;
        try {
            row = getData().getGroups().get(currentGroup).getRows().get(position);
        } catch (Exception e) {
            Log.e(TAG, "ERROR:", e);
        }

        if (row != null) {
            holder.setId(row.getId() + "");
            holder.setName(row.getName());
            holder.setUrl(row.getDetailUrl());
            holder.torrentName.setText(row.getCaption().getTitle());
            holder.date.setText(row.getCaption().getYear());
            holder.seed.setText(new Integer(row.getSeeds()).toString());
            holder.peer.setText(new Integer(row.getPeers()).toString());
            holder.description.setText(row.getCaption().getSubtitle());
            holder.dateTorrent.setText(row.getCreationDate());
            if (position % 2 == 1) {
                holder.row.setBackgroundColor(resources.getColor(R.color.row_even_color));
            } else {
                holder.row.setBackgroundColor(resources.getColor(R.color.row_odd_color));
            }
        }
    }

    @Override
    public int getItemCount() {
        return getBasicItemCount() + 1;
    }

    public int getBasicItemCount() {
        if (Objects.isNull(getData())
                || Objects.isNull(getData().getGroups())
                || Objects.isNull(getData().getGroups().get(currentGroup).getRows())) {

            return 0;
        }

        return getData().getGroups().get(currentGroup).getRows().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public MainGroupedPage getData() {
        return mainGroupedPage;
    }

    @Override
    public void setData(MainGroupedPage data) {
        this.mainGroupedPage = data;
        notifyDataSetChanged();
    }

    public void setCurrentGroup(int currentGroup) {
        this.currentGroup = currentGroup;
        notifyDataSetChanged();
    }

    public void nextGroup() {
        int groupIndex = currentGroup++;
        setCurrentGroup(groupIndex > mainGroupedPage.getGroups().size() ? mainGroupedPage.getGroups().size() : groupIndex);
    }

    public void previousGroup() {
        int groupIndex = currentGroup--;
        setCurrentGroup(groupIndex < 0 ? 0 : groupIndex);
    }


    public static class ViewHolder extends DefaultViewHolder {

        @Bind(R.id.main_page_plan_row_view)
        LinearLayout row;
        @Bind(R.id.main_page_plan_name)
        TextView torrentName;
        @Bind(R.id.main_page_plan_description)
        TextView description;
        @Bind(R.id.main_page_plan_date_torrent)
        TextView dateTorrent;
        @Bind(R.id.main_page_plan_date)
        TextView date;
        @Bind(R.id.main_page_plan_seed)
        TextView seed;
        @Bind(R.id.main_page_plan_peer)
        TextView peer;

        private String id;
        private String name;
        private String url;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public ViewHolder(View itemView, ViewHolderClickListener viewHolderClickListener) {
            super(itemView, viewHolderClickListener);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public Serializable getData() {
            return new TopicDetail(id, name, url);
        }
    }

}
