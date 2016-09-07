package org.rutor.team619.rutorclient.view.fragment.core;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.rutor.team619.rutorclient.view.activity.MainActivity;

import butterknife.ButterKnife;

/**
 * Created by BORIS on 06.09.2016.
 */
public abstract class InjectableFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        getCurrentActivity().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(resolveLayoutId(), container, false);
        ButterKnife.bind(this, view);
        onViewInjected();

        return view;
    }

    public MainActivity getCurrentActivity() {
        return (MainActivity) this.getActivity();
    }

    protected abstract void onViewInjected();

    public abstract int resolveLayoutId();

}
