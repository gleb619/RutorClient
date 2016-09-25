package org.rutor.team619.rutorclient.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.rutor.team619.rutorclient.model.settings.Settings;
import org.rutor.team619.rutorclient.service.core.DefaultService;

import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by BORIS on 16.08.2016.
 */
public class HttpServer extends DefaultService {

    private static final String TAG = HttpServer.class.getName() + ":";

    @Inject
    Settings project;
    @Inject
    WebServer server;

    public HttpServer() {
        super("HttpServer");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public HttpServer(String name) {
        super(name);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("HttpServer.onHandleIntent");
    }

    @Nullable
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            server.start();
        } catch (IOException ioe) {
            Log.w(TAG, "The server could not start.");
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (server != null)
            server.stop();
    }

}
