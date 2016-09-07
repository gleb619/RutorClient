package org.rutor.team619.rutorclient.app;

//import android.support.multidex.MultiDexApplication;

import android.os.Looper;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;

import dagger.ObjectGraph;

/**
 * Created by BORIS on 26.07.2015.
 */
public class MainApp extends MultiDexApplication {
//public class MainApp extends Application {

    private static final String TAG = MainApp.class.getName() + ":";
    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
//        new Runnable() {
//            @Override
//            public void run() {
//                objectGraph = ObjectGraph.create(new AppModule(test()));
        //        objectGraph = ObjectGraph.create(Arrays.asList(
        //                new AppModule(this))
        //                new ModelService(),
        //                new SecurityService()
        //        );
//                objectGraph.inject(this);
//            }
//        }.run();
        objectGraph = ObjectGraph.create(new AppModule(this));
        objectGraph.inject(this);
        LeakCanary.install(this);

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
