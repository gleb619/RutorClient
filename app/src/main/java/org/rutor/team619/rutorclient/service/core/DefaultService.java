package org.rutor.team619.rutorclient.service.core;

import android.app.IntentService;

import org.rutor.team619.rutorclient.app.MainApp;

/**
 * Created by BORIS on 16.08.2016.
 */
public abstract class DefaultService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DefaultService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((MainApp) getApplication()).inject(this);
    }

}
