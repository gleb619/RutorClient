package org.rutor.team619.rutorclient.service;

import android.util.Log;

import org.rutor.team619.rutorclient.model.settings.Settings;
import org.rutor.team619.rutorclient.service.converter.DetailPageConverter;

import java.io.BufferedReader;
import java.io.File;
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
    private final FolderService folderService;


    public WebServer(Settings project, FolderService folderService) {
        super(project.getHttpPort());

        this.folderService = folderService;
        this.originLocation = project.getUrl() + "/";
    }

    private static String readFileExtension(String fileName, String separator) {
        return fileName.substring(fileName.lastIndexOf(separator) + 1, fileName.length());
    }

    private static void modifyResponse(Response response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "*");
        response.addHeader("Access-Control-Allow-Methods", "*");
        response.addHeader("Access-Control-Allow-Credentials", "false");
    }

    private static String readFile(String location) {
        StringBuilder stringBuilder = new StringBuilder();
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

        return stringBuilder.toString();
    }

    private static String readName(String fileName) {
        return readFileExtension(fileName, File.separator);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String location = readFolder(session.getUri()) + readName(session.getUri());

        if (location.contains(Variables.FAVICON)) {
            return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
        }

        Log.e(TAG, "Work with " + session.getUri());
        String text = readFile(location);
        Response response = new Response(Response.Status.OK, readMimeType(session.getUri()), text);
        modifyResponse(response);

        return response;
    }

    private String readFolder(String fileName) {
        String extension = readFileExtension(fileName, ".");
        switch (extension) {
            case DetailPageConverter.Selectors.MIME_TYPE_HTML:
                return folderService.pagesDir();
            case DetailPageConverter.Selectors.MIME_TYPE_CSS:
                return folderService.cssDir();
            case DetailPageConverter.Selectors.MIME_TYPE_JS:
                return folderService.jsDir();
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

    public interface Variables extends Serializable {

        String FAVICON = "favicon.ico";
        String MIME_CSS = "text/css";
        String MIME_JS = "application/javascript";

    }

}
