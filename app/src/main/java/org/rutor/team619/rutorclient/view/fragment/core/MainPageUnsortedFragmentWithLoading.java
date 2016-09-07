package org.rutor.team619.rutorclient.view.fragment.core;

import android.support.v4.widget.SwipeRefreshLayout;

import org.rutor.team619.rutorclient.model.MainPlainPage;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by BORIS on 31.10.2015.
 */
public abstract class MainPageUnsortedFragmentWithLoading extends FragmentWithLoading {

    private static final String TAG = MainPageUnsortedFragmentWithLoading.class.getName() + ":";
    private final int PARTIAL_COUNT = 3, PARTIAL_LENGTH = 3;
    private final int PERCENTAGE_MATCH = 60;

    public abstract SwipeRefreshLayout getContentView();

    @Override
    public void onRefresh() {
        getContentView().setRefreshing(true);
        isRunning = false;
        loadData();
        getContentView().postDelayed(() ->
                getContentView().setRefreshing(false), 1000);
    }

    /*
    private int isInteger(String input) {
        int output = -1;
        if (input == null || input.length() == 0) {
            return output;
        }

        input = input.replaceAll("[^0-9]", "");
        try {
            output = Integer.parseInt(input);
        } catch (NumberFormatException e) {

        }

        return output;
    }

    private Set<String> splitQuery(String term) {
		final Set<String> partialSet = new HashSet();
        if (term == null || term.length() == 0) {
            return partialSet;
        }

        term = term.replaceAll("[^0-9a-zA-ZА-Яа-я]+", "").replaceAll("\\s+", "").toLowerCase();
        final int availDistance = Math.max(term.length() - PARTIAL_LENGTH, 0);
        for (int i = 0; i < PARTIAL_COUNT; i++) {
            final int pos0 = (PARTIAL_COUNT > 1) ? availDistance * i / (PARTIAL_COUNT - 1) : 0;
            final int pos1 = Math.min(pos0 + PARTIAL_LENGTH, term.length());

            partialSet.add(term.substring(pos0, pos1));
        }

        return partialSet;
    }

    protected Observable<MainPlainPage> filter(final MainPlainPage mainPageUnsorted, final String queryString) {
        Observable<MainPlainPage> output = Observable.create(new Observable.OnSubscribe<MainPlainPage>() {
            @Override
            public void call(Subscriber<? super MainPlainPage> subscriber) {
                final MainPlainPage filteredModelList = callData();
                subscriber.onNext(filteredModelList);
                subscriber.onCompleted();
            }

            @NonNull
            private MainPlainPage callData() {
                final int intQuery = isInteger(queryString);
                final List<Row> filteredModelList = new ArrayList<>();
                final MainPlainPage output = null;
//                        new MainUnsortedPage();
                if(1==1){
                    throw new NullPointerException("Implement this part");
                }

                if (intQuery > -1) {
                    for (Row model : mainPageUnsorted.getRows()) {
                        if (model.getPeers() == intQuery) {
                            filteredModelList.add(model);
                        } else if (model.getSeeds() == intQuery) {
                            filteredModelList.add(model);
                        } else if (model.getComments() == intQuery) {
                            filteredModelList.add(model);
                        }
                    }
                } else {
                    final Set<String> query = splitQuery(queryString);
                    for (Row model : mainPageUnsorted.getRows()) {
                        final String creationDate = model.getCreationDate().toLowerCase();
                        final String size = model.getSize().toLowerCase();
                        final String title = model.getCaption().getTitle().toLowerCase();
                        final String subtitle = model.getCaption().getSubtitle().toLowerCase();
                        final String year = model.getCaption().getYear().toLowerCase();

                        int numberOfMatches = 0;
                        int percentageMatch;

                        for (String term : query) {
                            if (creationDate.contains(term) || size.contains(term) ||
                                    title.contains(term) || subtitle.contains(term) ||
                                    year.contains(term)) {

                                numberOfMatches++;
                            }
                        }

                        percentageMatch = (numberOfMatches * 1) / (query.size() * 1) * 100;
                        if (percentageMatch >= PERCENTAGE_MATCH) {
                            filteredModelList.add(model);
                        }
                    }
                }

//                output.setRows(filteredModelList);

                return output;
            }
        });

        return output;
    }
    */
    @Override
    protected void loadDataProcess() {
        if (!isRunning) {
            showProgress(true);

            if (getRepository() == null) {
                return;
            }

            getRepository().mainPagePlain()
                    .timeout(getProjectSettings().getTimeout(), getProjectSettings().getTimeoutLatency())
                    .retry(getProjectSettings().getRetry())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<MainPlainPage>() {
                        @Override
                        public void onCompleted() {
                            showProgress(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            showError(e);
                        }

                        @Override
                        public void onNext(MainPlainPage response) {
                            getAdapter().setData(response);
                        }
                    });
        }
    }

}
