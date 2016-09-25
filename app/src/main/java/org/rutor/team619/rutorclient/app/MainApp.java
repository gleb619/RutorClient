package org.rutor.team619.rutorclient.app;

import android.app.Application;
import android.os.Looper;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;

import dagger.ObjectGraph;

//import android.support.multidex.MultiDexApplication;

/**
 * Created by BORIS on 26.07.2015.
 */
//public class MainApp extends MultiDexApplication {
public class MainApp extends Application {

    private static final String TAG = MainApp.class.getName() + ":";
    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        } else {
            LeakCanary.install(this);
        }

        objectGraph = ObjectGraph.create(new AppModule(this));
        objectGraph.inject(this);

//        if (BuildConfig.DEBUG) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()
//                    .build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build());
//        }

        changeExceptionHandler();
    }

    private void changeExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {

            Log.e(TAG, "uncaughtException#" +
                            "\n\t ex: " + ex +
                            "\n\t ex: " + thread +
                            "\n\t --------------------"
                    , ex
            );

            new Thread(() -> {
                Log.e(TAG, "uncaughtException#detect global error");

                Looper.prepare();
//                        Intent errorIntent = new Intent(MainApp.this.getApplicationContext(),
//                                MainActivity.class);
//                        errorIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        MainApp.this.getApplicationContext().startActivity(errorIntent);
//                        android.os.Process.killProcess(android.os.Process.myPid());
//                        Looper.loop();
                System.exit(2); //OPTIONAL and not suggested n

                Log.e(TAG, "uncaughtException#prepare exit");

            }).start();

        });
    }

    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    public <T> T inject(T instance) {
        if (getObjectGraph() != null) {
            return getObjectGraph().inject(instance);
        }

        return null;
    }
}
