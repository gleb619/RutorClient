package org.rutor.team619.rutorclient.view.fragment.core;

import android.support.v4.widget.SwipeRefreshLayout;

import static org.rutor.team619.rutorclient.model.settings.Settings.Code.FORCE_UPDATE;

/**
 * Created by BORIS on 12.11.2015.
 */
public abstract class RefreshableFragment extends FragmentWithLoading implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = RefreshableFragment.class.getName() + ":";

    @Override
    public void onRefresh() {
        getContentView().setRefreshing(true);
        getProjectSettings().update(FORCE_UPDATE, Boolean.TRUE);
        isRunning = false;
        loadData();
        getContentView().postDelayed(() ->
                getContentView().setRefreshing(false), 500);
    }

    protected abstract SwipeRefreshLayout getContentView();

}
