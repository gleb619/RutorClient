package org.rutor.team619.rutorclient.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.rutor.team619.rutorclient.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class BugReportFragment extends Fragment {

    private static final String TAG = BugReportFragment.class.getName() + ":";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bug, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }


}
