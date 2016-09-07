package org.rutor.team619.rutorclient.service;

import android.os.Environment;
import android.util.Log;

import org.rutor.team619.rutorclient.model.settings.ProjectSettings;
import org.rutor.team619.rutorclient.service.converter.DetailPageConverter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by BORIS on 16.08.2016.
 */
public class WebServer extends NanoHTTPD {

    private static final String TAG = WebServer.class.getName() + ":";

    private final String originLocation;
    private final String pagesDir;
    private final String jsDir;
    private final String cssDir;

    public WebServer(ProjectSettings project) {
        super(project.getHttpPort());

        String projectDir = new StringBuilder()
                .append(Environment.getExternalStorageDirectory().getAbsolutePath())
                .append("/")
                .append(project.getCacheDir())
                .append("/")
                .toString();

        this.pagesDir = new StringBuilder(projectDir)
                .append(DetailPageConverter.Selectors.PAGE_FOLDER)
                .append("/")
                .toString();
        this.jsDir = new StringBuilder(projectDir)
                .append(DetailPageConverter.Selectors.JS_FOLDER)
                .append("/")
                .toString();
        this.cssDir = new StringBuilder(projectDir)
                .append(DetailPageConverter.Selectors.CSS_FOLDER)
                .append("/")
                .toString();
        this.originLocation = project.getUrl() + "/";
    }

    private static String readFileExtension(String fileName, String separator) {
        return fileName.substring(fileName.lastIndexOf(separator) + 1, fileName.length());
    }

    @Override
    public Response serve(IHTTPSession session) {
        StringBuilder stringBuilder = new StringBuilder();
        String location = readFolder(session.getUri()) + readName(session.getUri());

        if (location.contains(Variables.FAVICON)) {
            return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
        }

        Log.e(TAG, "Work with " + session.getUri());

        try {
            // Open file from SD Card, read page
            FileReader index = new FileReader(location);
            BufferedReader reader = new BufferedReader(index);
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line)
                        .append(DetailPageConverter.Selectors.NEW_LINE);
            }
            reader.close();

        } catch (IOException ioe) {
            Log.w(TAG, ioe);
        }

        Response response = new Response(Response.Status.OK, readMimeType(session.getUri()), stringBuilder.toString());
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "*");
        response.addHeader("Access-Control-Allow-Methods", "*");
        response.addHeader("Access-Control-Allow-Credentials", "false");

        return response;
    }

    private String readFolder(String fileName) {
        String extension = readFileExtension(fileName, ".");
        switch (extension) {
            case DetailPageConverter.Selectors.MIME_TYPE_HTML:
                return pagesDir;
            case DetailPageConverter.Selectors.MIME_TYPE_CSS:
                return cssDir;
            case DetailPageConverter.Selectors.MIME_TYPE_JS:
                return jsDir;
        }

        return originLocation;
    }

    private String readMimeType(String fileName) {
        String extension = readFileExtension(fileName, ".");
        switch (extension) {
            case DetailPageConverter.Selectors.MIME_TYPE_HTML:
                return MIME_HTML;
            case DetailPageConverter.Selectors.MIME_TYPE_CSS:
                return Variables.MIME_CSS;
            case DetailPageConverter.Selectors.MIME_TYPE_JS:
                return Variables.MIME_JS;
        }

        return MIME_PLAINTEXT;
    }

    private String readName(String fileName) {
        return readFileExtension(fileName, "/");
    }

    public interface Variables extends Serializable {

        String FAVICON = "favicon.ico";
        String MIME_CSS = "text/css";
        String MIME_JS = "application/javascript";

    }

}
