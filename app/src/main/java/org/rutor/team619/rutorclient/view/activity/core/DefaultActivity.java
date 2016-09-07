package org.rutor.team619.rutorclient.view.activity.core;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import org.rutor.team619.rutorclient.app.MainApp;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import dagger.ObjectGraph;

/**
 * Created by BORIS on 17.08.2016.
 */
public abstract class DefaultActivity extends AppCompatActivity {

    protected static final String TAG = DefaultActivity.class.getName() + ":";

    protected void onAfterCreate() {
        getApplicationContext().inject(this);
        ButterKnife.bind(this);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }

        return null;
    }

    @Override
    public MainApp getApplicationContext() {
        return (MainApp) super.getApplicationContext();
    }

    public abstract ObjectGraph getObjectGraph();

    public <T> T inject(T instance) {
        if (getObjectGraph() != null) {
            return getObjectGraph().inject(instance);
        }

        return null;
    }

    public enum Fragments implements Serializable {

        BUG_REPORT(-2),
        DETAIL_PAGE(-1),
        MAIN_PAGE_GROUPED(1),
        MAIN_PAGE_PLAIN(0);

        private final int id;

        Fragments(int id) {
            this.id = id;
        }

        public static Fragments valueOf(int id) {
            switch (id) {
                case -2:
                    return BUG_REPORT;
                case -1:
                    return DETAIL_PAGE;
                case 0:
                    return MAIN_PAGE_PLAIN;
                case 1:
                    return MAIN_PAGE_GROUPED;
            }

            throw new IllegalArgumentException("Can't work with this id: " + id);
        }

        public int getId() {
            return id;
        }
    }

}
