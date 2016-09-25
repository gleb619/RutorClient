package org.rutor.team619.rutorclient.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import org.rutor.team619.rutorclient.R;
import org.rutor.team619.rutorclient.app.MainApp;
import org.rutor.team619.rutorclient.model.settings.Settings;
import org.rutor.team619.rutorclient.resource.RuTorRepository;
import org.rutor.team619.rutorclient.view.activity.MainActivity;
import org.rutor.team619.rutorclient.view.activity.core.DefaultActivity;
import org.rutor.team619.rutorclient.view.adapter.MainPagePlainAdapter;
import org.rutor.team619.rutorclient.view.adapter.core.DefaultAdapter;
import org.rutor.team619.rutorclient.view.fragment.core.MainPageUnsortedFragmentWithLoading;
import org.rutor.team619.rutorclient.view.listener.HidingScrollListener;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainPagePlainFragment extends MainPageUnsortedFragmentWithLoading
//        implements SearchView.OnQueryTextListener
{

    private static final String TAG = MainPagePlainFragment.class.getName() + ":";

    @Inject
    RuTorRepository ruTorRepository;
    @Inject
    MainApp mainApp;
    @Inject
    Settings project;

    @Bind(R.id.main_page_unsorted_content)
    SwipeRefreshLayout contentView;
    @Bind(R.id.main_page_unsorted_recycler)
    RecyclerView recyclerView;

    View progressView;
    Toolbar toolbar;
    View errorPane;
    TextView errorPaneStackTrace;
    ImageView errorType;

    MainPagePlainAdapter mainPagePlainAdapter;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        recyclerView.setLayoutManager(recyclerView.getLayoutManager() != null ? recyclerView.getLayoutManager()
                : new LinearLayoutManager(getActivity()));
        mainPagePlainAdapter = mainPagePlainAdapter != null ? mainPagePlainAdapter
                : new MainPagePlainAdapter(onItemClickListener(), this.getActivity().getResources());
        recyclerView.setAdapter(mainPagePlainAdapter);
        recyclerView.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });

        loadData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

//        final MenuItem item = menu.findItem(R.id.action_search);
//        MenuItemImpl.get
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
//        searchView.setOnQueryTextListener(this);
//        searchView.setQueryHint("Search Customer");
    }

    private void hideViews() {
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));

//        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabButton.getLayoutParams();
//        int fabBottomMargin = lp.bottomMargin;
//        mFabButton.animate().translationY(mFabButton.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
//        mFabButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    @Override
    public void onViewInjected() {
//        getCurrentActivity().setSupportActionBar(toolbar);
//        getCurrentActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getCurrentActivity().getSupportActionBar().setHomeButtonEnabled(true);
//        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
//        toolbar.setSubtitle("Sub");
//        this.getActivity().setTitle(R.string.main_page_unsorted_fragment);
        contentView.setOnRefreshListener(this);
        contentView.setColorSchemeResources(R.color.step_1, R.color.step_2, R.color.step_3, R.color.step_4, R.color.step_5);
        contentView.setProgressViewOffset(false, 0, 200);

        progressView = getActivity().findViewById(R.id.progress_bar);
        toolbar = (Toolbar) getActivity().findViewById(R.id.main_toolbar);
        errorPane = getActivity().findViewById(R.id.error_pane);
        errorPaneStackTrace = (TextView) getActivity().findViewById(R.id.error_pane_stack_trace);
        errorType = (ImageView) getActivity().findViewById(R.id.error_type);
    }

    private MainPagePlainAdapter.ViewHolder.ViewHolderClickListener onItemClickListener() {
        final Map<String, String> parameters = new HashMap<>();
        return (caller, detail) -> {
            parameters.put("id", detail.getId());
            parameters.put("name", detail.getName());
            parameters.put("url", detail.getUrl());
            ((MainActivity) MainPagePlainFragment.this.getActivity())
                    .selectItem(DefaultActivity.Fragments.DETAIL_PAGE, parameters);
            showViews();
        };
    }

    @Override
    public DefaultAdapter getAdapter() {
        return mainPagePlainAdapter;
    }

    @Override
    public Context getContext() {
        return mainApp;
    }

    @Override
    public View getMainPane() {
        return recyclerView;
    }

    @Override
    public View getProgressView() {
        return progressView;
    }

    @Override
    public Settings getProjectSettings() {
        return project;
    }

    @Override
    public RuTorRepository getRepository() {
        return ruTorRepository;
    }

    @Override
    public View getErrorPane() {
        return errorPane;
    }

    @Override
    public TextView getErrorPaneStackTrace() {
        return errorPaneStackTrace;
    }

    @Override
    public ImageView getErrorType() {
        return errorType;
    }

    @Override
    public int resolveLayoutId() {
        return R.layout.fragment_main_page_plain;
    }

    @Override
    public SwipeRefreshLayout getContentView() {
        return contentView;
    }

    /*
    @Override
    public boolean onQueryTextSubmit(String query) {
//        if(query == null || query.length() == 0){
//            getAdapter().setData(getAdapter().getData());
//            getAdapter().notifyDataSetChanged();
//        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        return onQueryTextChangeData(query);
    }

    private boolean onQueryTextChangeData(String query) {
        if(query == null || query.length() == 0){
            getAdapter().setData(getAdapter().getData());

            return true;
        }

        MainPlainPage data = (MainPlainPage) getAdapter().getData();
        filter(data, query)
                .throttleFirst(300, project.getTimeoutLatency())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MainPlainPage>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showError(e);
                    }

                    @Override
                    public void onNext(final MainPlainPage mainPageUnsorted) {
                        getAdapter().setData(mainPageUnsorted);
                    }
                });
        return false;
    }
    */

}
