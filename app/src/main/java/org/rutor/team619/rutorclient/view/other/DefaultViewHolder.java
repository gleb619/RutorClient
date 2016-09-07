package org.rutor.team619.rutorclient.view.other;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.Serializable;

import butterknife.ButterKnife;

/**
 * Created by BORIS on 04.09.2016.
 */
public abstract class DefaultViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, Serializable {

    private ViewHolderClickListener<T> viewHolderClickListener;

    public DefaultViewHolder(View itemView) {
        super(itemView);
    }

    public DefaultViewHolder(View itemView, ViewHolderClickListener<T> viewHolderClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.viewHolderClickListener = viewHolderClickListener;
        itemView.setOnClickListener(this);
    }

    public abstract T getData();

    @Override
    public void onClick(View v) {
        if (viewHolderClickListener != null) viewHolderClickListener.onItemClick(v, getData());
    }

    public interface ViewHolderClickListener<T> {
        void onItemClick(View caller, T data);
    }

}
