package org.rutor.team619.rutorclient.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Stream;

import org.rutor.team619.rutorclient.R;
import org.rutor.team619.rutorclient.app.MainApp;
import org.rutor.team619.rutorclient.model.TopicDetail;
import org.rutor.team619.rutorclient.model.settings.Settings;
import org.rutor.team619.rutorclient.resource.RuTorRepository;
import org.rutor.team619.rutorclient.view.activity.MainActivity;
import org.rutor.team619.rutorclient.view.activity.core.DefaultActivity;
import org.rutor.team619.rutorclient.view.adapter.MainPageGroupedAdapter;
import org.rutor.team619.rutorclient.view.adapter.core.DefaultAdapter;
import org.rutor.team619.rutorclient.view.fragment.core.RefreshableFragment;
import org.rutor.team619.rutorclient.view.listener.HidingScrollListener;
import org.rutor.team619.rutorclient.view.other.DefaultViewHolder;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainPageGroupedFragment extends RefreshableFragment {

    private static final String TAG = MainPageGroupedFragment.class.getName() + ":";

    @Inject
    RuTorRepository ruTorRepository;
    @Inject
    MainApp mainApp;
    @Inject
    Settings project;

    @Bind(R.id.main_page_grouped_tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.main_page_grouped_content)
    SwipeRefreshLayout contentView;
    @Bind(R.id.main_page_grouped_listbox)
    RecyclerView recyclerView;

    //    @Bind(R.id.progress_bar)
    View progressView;
    //    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    //    @Bind(R.id.error_pane)
    View errorPane;
    //    @Bind(R.id.error_pane_stack_trace)
    TextView errorPaneStackTrace;
    //    @Bind(R.id.error_type)
    ImageView errorType;

    MainPageGroupedAdapter mainPageGroupedAdapter;
    //    GroupPageAdapter groupPageAdapter;
//    GroupPageAdapter2 groupPageAdapter;
    HidingScrollListener hidingScrollListener;
    ViewPager.OnPageChangeListener listener;
    boolean dataLoaded = false;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mainPageGroupedAdapter = mainPageGroupedAdapter != null ? mainPageGroupedAdapter
                : new MainPageGroupedAdapter(onItemClickListener(), getActivity().getResources(), mainApp);

        hidingScrollListener = new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        };

        recyclerView.setLayoutManager(recyclerView.getLayoutManager() != null ? recyclerView.getLayoutManager()
                : new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(mainPageGroupedAdapter);
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

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mainPageGroupedAdapter.setCurrentGroup(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    private void hideViews() {
        toolbar.animate().translationY(-toolbar.getHeight())
                .setInterpolator(new AccelerateInterpolator(2));
        if (dataLoaded) {
            tabLayout.animate().translationY(0)
                    .setInterpolator(new AccelerateInterpolator(2));
        }
    }

    private void showViews() {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        if (dataLoaded) {
            tabLayout.animate().translationY(toolbar.getHeight())
                    .setInterpolator(new DecelerateInterpolator(2));
        }
    }

    @Override
    public void onViewInjected() {
//        toolbar.showOverflowMenu();
//        getCurrentActivity().setSupportActionBar(toolbar);
//        getCurrentActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getCurrentActivity().getSupportActionBar().setHomeButtonEnabled(true);
//        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
//        toolbar.setSubtitle("Sub");
//        getActivity().setTitle(R.string.main_page_grouped_fragment);
        contentView.setOnRefreshListener(this);
        contentView.setColorSchemeResources(R.color.step_1, R.color.step_2, R.color.step_3, R.color.step_4, R.color.step_5);
        contentView.setProgressViewOffset(false, 0, 200);

        progressView = getActivity().findViewById(R.id.progress_bar);
        toolbar = (Toolbar) getActivity().findViewById(R.id.main_toolbar);
        errorPane = getActivity().findViewById(R.id.error_pane);
        errorPaneStackTrace = (TextView) getActivity().findViewById(R.id.error_pane_stack_trace);
        errorType = (ImageView) getActivity().findViewById(R.id.error_type);

        loadData();
    }

    private DefaultViewHolder.ViewHolderClickListener<TopicDetail> onItemClickListener() {
        final Map<String, String> parameters = new HashMap<>();
        return (caller, detail) -> {
            parameters.put("id", detail.getId());
            parameters.put("name", detail.getName());
            parameters.put("url", detail.getUrl());
            ((MainActivity) MainPageGroupedFragment.this.getActivity())
                    .selectItem(DefaultActivity.Fragments.DETAIL_PAGE, parameters);
            showViews();
        };
    }

    @Override
    protected void loadDataProcess() {
        getRepository().mainPageGrouped()
                .timeout(getProjectSettings().getTimeout(), getProjectSettings().getTimeoutLatency())
                .retry(getProjectSettings().getRetry())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        mainGroupedPage -> {
                            showProgress(false);
                            tabLayout.removeAllTabs();
                            Stream.of(mainGroupedPage.getGroups())
                                    .forEach(group -> tabLayout.addTab(tabLayout.newTab().setText(group.getName())));
                            getAdapter().setData(mainGroupedPage);
                            showTabs();
                            dataLoaded = true;
                        },
                        e -> showError(e)
                );
    }

    private void showTabs() {
        tabLayout.animate().translationY(toolbar.getHeight())
                .setInterpolator(new DecelerateInterpolator(2));
        TypedValue tv = new TypedValue();
        if (getActivity() != null && getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            tabLayout.setMinimumHeight(actionBarHeight);
        }

//        TypedValue tv = new TypedValue();
//        if (getActivity() != null && getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
//            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
//            Animation anim = new Animation() {
//                @Override
//                protected void applyTransformation(float interpolatedTime, Transformation t) {
//                    TabLayout.LayoutParams params = (TabLayout.LayoutParams) tabLayout.getLayoutParams();
//                    params.topMargin = (int) (actionBarHeight * interpolatedTime);
//                    tabLayout.setLayoutParams(params);
//                }
//            };
//            anim.setDuration(500); // in ms
//            tabLayout.startAnimation(anim);
//            tabLayout.setMinimumHeight(actionBarHeight);
//        }
    }


    @Override
    public SwipeRefreshLayout getContentView() {
        return contentView;
    }

    @Override
    public DefaultAdapter getAdapter() {
        return mainPageGroupedAdapter;
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
        return R.layout.fragment_main_page_grouped;
    }

}
