package org.rutor.team619.rutorclient.view.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.rutor.team619.rutorclient.R;
import org.rutor.team619.rutorclient.app.MainApp;
import org.rutor.team619.rutorclient.model.settings.ProjectSettings;
import org.rutor.team619.rutorclient.service.WebServer;
import org.rutor.team619.rutorclient.util.AppUtil;
import org.rutor.team619.rutorclient.util.Objects;
import org.rutor.team619.rutorclient.view.activity.core.DefaultActivity;
import org.rutor.team619.rutorclient.view.fragment.BugReportFragment;
import org.rutor.team619.rutorclient.view.fragment.DetailPageFragment;
import org.rutor.team619.rutorclient.view.fragment.MainPageGroupedFragment;
import org.rutor.team619.rutorclient.view.fragment.MainPagePlainFragment;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import dagger.ObjectGraph;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends DefaultActivity {

    @Inject
    MainApp mainApp;
    @Inject
    AppUtil appUtil;
    @Inject
    ProjectSettings project;
    @Inject
    WebServer server;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.fragment_container)
    View coordinatorLayout;
    @Bind(R.id.menuListView)
    ListView menuListView;
    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    private ActionBarDrawerToggle drawerToggle;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            super.onAfterCreate();

            if (savedInstanceState == null) {
                selectItem(Fragments.MAIN_PAGE_PLAIN);
            }

//        startService(new Intent(mainApp, HttpServer.class));

            appUtil.test()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            testResult -> {
                                if (Objects.nonNull(testResult)) {
                                    Snackbar.make(coordinatorLayout, testResult, Snackbar.LENGTH_LONG).show();
                                }

                                System.out.println("MainActivity.onCreate, available ips: " + Arrays.asList(
                                        appUtil.ip4(),
                                        appUtil.ip4_2(),
                                        appUtil.ip4_3(),
                                        appUtil.ip4_4(),
                                        appUtil.ip4_5(),
                                        appUtil.ip4_6(),
                                        appUtil.ip4_7()
                                ));
                                project.setAddress(appUtil.ip4_7());
                            },
                            e -> Log.e(TAG, "ERROR:", e)
                    );

            createSideBar(toolbar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    protected void onDestroy() {
//        stopService(new Intent(mainApp, HttpServer.class));
//        super.onDestroy();
//    }

    @Override
    //TODO: Repair http server, move it to intentService.
    protected void onResume() {
        super.onResume();

        try {
            if (server != null) {
                server.start();
            }
        } catch (IOException e) {
            Log.e(TAG, "ERROR: ", e);
            server = null;
        }
    }

    @Override
    //TODO: Repair http server, move it to intentService.
    protected void onPause() {
        super.onPause();

        if (server != null) {
            server.stop();
            server = null;
        }
    }

    //TODO: Write only 1 toolbar, remove syncState. if nothing else helps then write cusom button and open drawer programmatically. https://github.com/codepath/android_guides/wiki/Fragment-Navigation-Drawer
    private void createSideBar(Toolbar toolbar) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            getSupportActionBar().setDisplayShowCustomEnabled(true);
//            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        if (drawerToggle == null) {
            drawerToggle = new ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    toolbar,
                    R.string.drawer_open,
                    R.string.drawer_close
            ) {

                public void onDrawerClosed(View view) {
//                    supportInvalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView) {
//                    supportInvalidateOptionsMenu();
                    InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
                }
            };

            drawerLayout.setDrawerListener(drawerToggle);
            adapter = menuListView.getAdapter() != null ? menuListView.getAdapter() :
                    new ArrayAdapter<>(this, R.layout.sidebar_menu_item, getResources().getStringArray(R.array.drawer_menu_options));
            menuListView.setAdapter(adapter);
            menuListView.setOnItemClickListener(new DrawerItemClickListener());
            drawerToggle.syncState();
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        } else {
            drawerToggle.syncState();
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onPrepareOptionsMenu(Menu menu) {
            boolean drawerOpen = drawerLayout.isDrawerOpen(Gravity.LEFT);
            menu.findItem(R.id.action_search).setVisible(!drawerOpen);
            return super.onPrepareOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (drawerToggle.onOptionsItemSelected(item)) {
                return true;
            }

            switch(item.getItemId()) {
                case R.id.action_search:
                    Log.d(TAG, "onOptionsItemSelected action_search");
                    return true;
                case R.id.action_settings:
                    Log.d(TAG, "onOptionsItemSelected action_settings");
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (drawerToggle != null) {
            drawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getVisibleFragment();

        if (fragment != null && fragment.isVisible()) {
            selectItem(Fragments.MAIN_PAGE_PLAIN);
        } else if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    public void selectItem(Fragments type) {
        try {
            selectItemData(type, null);
        } catch (Exception e) {
            Log.e(TAG, "ERROR:", e);
        }
    }

    public void selectItem(Fragments type, Map<String, String> params) {
        try {
            selectItemData(type, params);
        } catch (Exception e) {
            Log.e(TAG, "ERROR:", e);
        }
    }

    private void selectItemData(Fragments type, Map<String, String> params) {
        Fragment fragment = null;
        switch (type) {
            case BUG_REPORT:
                fragment = new BugReportFragment();
                break;
            case DETAIL_PAGE:
                fragment = new DetailPageFragment();

                Bundle args = new Bundle();
                for (String key : params.keySet()) {
                    args.putString(key, params.get(key));
                }
                fragment.setArguments(args);
                break;
            case MAIN_PAGE_PLAIN:
                fragment = new MainPagePlainFragment();
                break;
            case MAIN_PAGE_GROUPED:
                fragment = new MainPageGroupedFragment();
                break;
            default:
                break;
        }

        if (fragment == null) {
            Log.e(TAG, "Error. Fragment is not created");
            return;
        }

        try {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, type + "")
                    .commit();
        } catch (Exception e) {
            Log.e(TAG, "ERROR:", e);
        }

        //TODO: Write normal logic here
        menuListView.setItemChecked(type.getId(), true);

//        try {
//            setTitle((String) menuListView.getItemAtPosition(type.getId()));
//        } catch (Exception e) {
//            Log.e(TAG, "ERROR:", e);
//        }

        if (drawerLayout != null) drawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public ObjectGraph getObjectGraph() {
        if (mainApp != null) {
            return mainApp.getObjectGraph();
        }

        return null;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(Fragments.valueOf(position));
        }
    }

}
