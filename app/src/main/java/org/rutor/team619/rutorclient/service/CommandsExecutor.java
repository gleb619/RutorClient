package org.rutor.team619.rutorclient.service;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by BORIS on 18.09.2016.
 */
public class CommandsExecutor {

    public void sendCommand(String id, String topicUrl) {
        try {
            URL url = new URL("http://rutorclient-ffffff.rhcloud.com/do-download");
            JSONObject postDataParams = new JSONObject();
            postDataParams.put("id", id);
            postDataParams.put("url", topicUrl);
            Log.e("params", postDataParams.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
