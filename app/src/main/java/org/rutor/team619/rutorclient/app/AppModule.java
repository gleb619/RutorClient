package org.rutor.team619.rutorclient.app;


import android.content.Context;

import org.rutor.team619.rutorclient.service.HttpServer;
import org.rutor.team619.rutorclient.view.activity.MainActivity;
import org.rutor.team619.rutorclient.view.fragment.DetailPageFragment;
import org.rutor.team619.rutorclient.view.fragment.DeviceIdentificationFragment;
import org.rutor.team619.rutorclient.view.fragment.MainPageGroupedFragment;
import org.rutor.team619.rutorclient.view.fragment.MainPagePlainFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by BORIS on 26.07.2015.
 */
@Module(
        includes = {
                MainModule.class
        },
        injects = {
                MainActivity.class,
                MainPageGroupedFragment.class,
                MainPagePlainFragment.class,
                DetailPageFragment.class,
                DeviceIdentificationFragment.class,
                HttpServer.class,
                MainApp.class
        }
)
public class AppModule {

    private final MainApp mainApp;

    public AppModule(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @Provides
    @Singleton
    public MainApp provideApplication() {
        return mainApp;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return mainApp;
    }

}
