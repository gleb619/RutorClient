package org.rutor.team619.rutorclient.service;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by BORIS on 18.09.2016.
 */
public class DeviceInformationService {

    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    private final Context context;
    private String uniqueID = null;

    public DeviceInformationService(Context context) {
        this.context = context;
    }

    public synchronized String id() {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = generateUID();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
        return uniqueID;
    }

    private String generateUID() {
        return UUID.randomUUID().toString().substring(0, 7).replace("-", "");
    }

}
