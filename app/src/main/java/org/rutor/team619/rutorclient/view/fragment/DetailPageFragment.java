package org.rutor.team619.rutorclient.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import org.rutor.team619.rutorclient.R;
import org.rutor.team619.rutorclient.app.MainApp;
import org.rutor.team619.rutorclient.model.TopicDetail;
import org.rutor.team619.rutorclient.model.settings.ProjectSettings;
import org.rutor.team619.rutorclient.resource.RuTorRepository;
import org.rutor.team619.rutorclient.service.ImageDownloader;
import org.rutor.team619.rutorclient.util.AppUtil;
import org.rutor.team619.rutorclient.view.adapter.core.DefaultAdapter;
import org.rutor.team619.rutorclient.view.fragment.core.FragmentWithLoading;

import javax.inject.Inject;

import butterknife.Bind;
import rx.Observable;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailPageFragment extends FragmentWithLoading {

    private static final String TAG = DetailPageFragment.class.getName() + ":";
    @Inject
    RuTorRepository ruTorRepository;
    @Inject
    ImageDownloader imageDownloader;
    @Inject
    MainApp mainApp;
    @Inject
    ProjectSettings project;
    @Inject
    AppUtil appUtil;
    //    @Bind(R.id.page_detail_content_view)
//    SwipeRefreshLayout contentView;
    @Bind(R.id.page_detail_browser)
    WebView webView;

    //    @Bind(R.id.page_detail_progress_bar)
    View progressView;
    //    @Bind(R.id.error_pane)
    View errorPane;
    //    @Bind(R.id.error_pane_stack_trace)
    TextView errorPaneStackTrace;
    //    @Bind(R.id.error_type)
    ImageView errorType;
    //    @Bind(R.id.detail_page_toolbar)
    Toolbar toolbar;

    private TopicDetail topicDetalization;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        webView.canGoBack();
        webView.canGoForward();
        webView.clearHistory();
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(imageDownloader.getWebAppInterface(), "RutorClient");

        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

//        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//
//        if (Build.VERSION.SDK_INT >= 19) {
//            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        } else {
//            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }
//
//        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        topicDetalization = new TopicDetail(getArguments());
        toolbar.setTitle(topicDetalization.getName());

        //TODO: Hidewrap image direction
        loadData();
    }

    @Override
    protected void onViewInjected() {
//      ((MainActivity) getActivity()).setSupportActionBar(toolbar);
//      ((MainActivity) getActivity()).createSideBar(toolbar);

        progressView = getActivity().findViewById(R.id.progress_bar);
        toolbar = (Toolbar) getActivity().findViewById(R.id.main_toolbar);
        errorPane = getActivity().findViewById(R.id.error_pane);
        errorPaneStackTrace = (TextView) getActivity().findViewById(R.id.error_pane_stack_trace);
        errorType = (ImageView) getActivity().findViewById(R.id.error_type);

        imageDownloader.setView(webView);
        imageDownloader.setToolbar(toolbar);
        imageDownloader.setWindow(getActivity().getWindow());
    }

    @Override
    public void onStop() {
        imageDownloader.clear();
        super.onStop();
    }

    @Override
    protected void loadDataProcess() {
        Observable.defer(() -> ruTorRepository.detailPage(topicDetalization.getId(), topicDetalization.getName()))
                .flatMap(appUtil::convert)
                .subscribe(detailPage -> {
                    showProgress(false);
                    webView.loadUrl(detailPage.getFileName());
                }, e -> {
                    showError(e);
                });
    }

    @Override
    public View getMainPane() {
        return webView;
    }

    @Override
    public View getProgressView() {
        return progressView;
    }

    @Override
    public Context getContext() {
        return mainApp;
    }

    @Override
    public RuTorRepository getRepository() {
        return ruTorRepository;
    }

    @Override
    public ProjectSettings getProjectSettings() {
        return project;
    }

    @Override
    public DefaultAdapter getAdapter() {
        return null;
    }

    @Override
    public View getErrorPane() {
        return errorType;
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
        return R.layout.fragment_detail;
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    /* =================== */

    private class MyBrowser extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains(project.getUrl())) {
                webView.loadUrl(url);
            } else {
                Snackbar.make(webView, "Sorry, Can't load this url", Snackbar.LENGTH_LONG).show();
                Log.e(TAG, "Can't load url: " + url);
            }

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            showProgress(false);
            toolbar.setTitle(view.getTitle());
        }
    }

}
