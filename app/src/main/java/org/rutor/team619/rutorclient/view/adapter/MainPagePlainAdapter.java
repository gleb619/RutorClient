package org.rutor.team619.rutorclient.view.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.rutor.team619.rutorclient.R;
import org.rutor.team619.rutorclient.model.MainPlainPage;
import org.rutor.team619.rutorclient.model.Row;
import org.rutor.team619.rutorclient.model.TopicDetail;
import org.rutor.team619.rutorclient.view.adapter.core.DefaultAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by BORIS on 31.10.2015.
 */
public class MainPagePlainAdapter extends RecyclerView.Adapter<MainPagePlainAdapter.ViewHolder> implements DefaultAdapter<MainPlainPage> {

    private static final String TAG = MainPagePlainAdapter.class.getName() + ":";

    private final int TYPE_HEADER = 2;
    private final int TYPE_ITEM = 1;
    private final ViewHolder.ViewHolderClickListener viewHolderClickListener;
    private final Resources resources;
    private MainPlainPage mainPageUnsorted;

    public MainPagePlainAdapter(ViewHolder.ViewHolderClickListener viewHolderClickListener, Resources resources) {
        this.viewHolderClickListener = viewHolderClickListener;
        this.resources = resources;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            final View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main_page_plain, parent, false);
            return new ViewHolder(rowView, viewHolderClickListener);
        } else if (viewType == TYPE_HEADER) {
            final View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_header, parent, false);
            return new ViewHolder(rowView);
        } else {
            throw new IllegalArgumentException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mainPageUnsorted == null || mainPageUnsorted.getRows().size() == 0 || isPositionHeader(position)) {
            return;
        }

        if (position > 0) {
            position--;
        }

        Row row = null;
        try {
            row = mainPageUnsorted.getRows().get(position);
        } catch (Exception e) {
            Log.e(TAG, "ERROR:", e);
        }

        if (row != null) {
            holder.setId(row.getId() + "");
            holder.setName(row.getName());
            holder.setUrl(row.getDetailUrl());
            try {
                holder.torrentName.setText(row.getCaption().getTitle());
                holder.date.setText(row.getCaption().getYear());
                holder.seed.setText(new Integer(row.getSeeds()).toString());
                holder.peer.setText(new Integer(row.getPeers()).toString());
                holder.description.setText(row.getCaption().getSubtitle());
                holder.dateTorrent.setText(row.getCreationDate());
                if (position % 2 == 1) {
//                    holder.row.setBackground(resources.getDrawable(R.drawable.row_even_color));
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                        holder.row.setBackground(resources.getDrawable(R.drawable.row_item_even));
//                    }
//                    else {
//                        holder.row.setBackgroundDrawable(resources.getDrawable(R.drawable.row_item_even));
//                    }
                    holder.row.setBackgroundColor(resources.getColor(R.color.row_even_color));
                } else {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                        holder.row.setBackground(resources.getDrawable(R.drawable.row_item_odd));
//                    }
//                    else {
//                        holder.row.setBackgroundDrawable(resources.getDrawable(R.drawable.row_item_odd));
//                    }
                    holder.row.setBackgroundColor(resources.getColor(R.color.row_odd_color));
                }
            } catch (Exception e) {
                Log.e(TAG, "ERROR:", e);
            }
        }
    }

    @Override
    public int getItemCount() {
        return getBasicItemCount() + 1;
    }

    public int getBasicItemCount() {
        if (mainPageUnsorted == null) return 0;
        return mainPageUnsorted.getRows() == null ? 0 : mainPageUnsorted.getRows().size();
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
    public MainPlainPage getData() {
        return mainPageUnsorted;
    }

    @Override
    public void setData(MainPlainPage data) {
        this.mainPageUnsorted = data;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static final String TAG = ViewHolder.class.getName() + ":";

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

        private ViewHolderClickListener viewHolderClickListener;

        private String id;
        private String name;
        private String url;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public ViewHolder(View itemView, ViewHolderClickListener viewHolderClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.viewHolderClickListener = viewHolderClickListener;
            itemView.setOnClickListener(this);
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
        public void onClick(View v) {
            viewHolderClickListener.onItemClick(v, new TopicDetail(id, name, url));
        }

        public interface ViewHolderClickListener {
            void onItemClick(View caller, TopicDetail detalization);
        }

    }

}
