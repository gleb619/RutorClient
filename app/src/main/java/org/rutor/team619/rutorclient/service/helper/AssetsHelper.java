package org.rutor.team619.rutorclient.service.helper;

import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.annimon.stream.Stream;

import org.rutor.team619.rutorclient.model.settings.ProjectSettings;
import org.rutor.team619.rutorclient.service.helper.core.Helper;
import org.rutor.team619.rutorclient.util.Objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

/**
 * Created by BORIS on 14.08.2016.
 */
public class AssetsHelper implements Helper {

    private static final String TAG = AssetsHelper.class.getName() + ":";

    private final Reference<AssetManager> assetManager;
    private final Reference<ProjectSettings> projectSettings;

    public AssetsHelper(AssetManager assetManager, ProjectSettings project) {
        this.assetManager = new SoftReference<>(assetManager);
        this.projectSettings = new SoftReference<>(project);
    }

    private static String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();

        return ret;
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();

        return sb.toString();
    }

    @Override
    public void onHelp() throws IOException {
        String[] files = assetManager.get().list("");
        if (files == null) {
            return;
        }
        String projectPath = Environment.getExternalStoragePublicDirectory(
                projectSettings.get().getCacheDir())
                .getAbsolutePath();

        String jsPath = new StringBuilder(projectPath)
                .append(Variables.SEPARATOR)
                .append(Variables.JS)
                .append(Variables.SEPARATOR)
                .toString();
        String cssPath = new StringBuilder(projectPath)
                .append(Variables.SEPARATOR)
                .append(Variables.CSS)
                .append(Variables.SEPARATOR)
                .toString();

        Stream.of(files)
                .filter(fileName -> hasExtension(fileName))
                .peek(fileName -> Log.d(TAG, String.format("Work with %s from assets", fileName)))
                .map(fileName -> createFile(jsPath, cssPath, fileName))
                .filter(file -> Objects.nonNull(file))
                .forEach(file -> moveFile(file));

        assetManager.clear();
        projectSettings.clear();
    }

    private void moveFile(File file) {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = assetManager.get().open(file.getName());
            out = new FileOutputStream(file);
            copyFile(in, out);
            Log.d(TAG, String.format("Successfully replaced file from assets, new path is %s", file.getCanonicalPath()));
        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to copy asset file: %s", file.getName()), e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Log.e(TAG, String.format("FATAL ERROR#Failed to copy asset file: %s", file.getName()), e);
            }
        }
    }

    private File createFile(String jsPath, String cssPath, String filename) {
        File outFile;
        if (Variables.JS.equals(parseExtension(filename))) {
            outFile = new File(jsPath, filename);
            Log.d(TAG, "Work with js, filename: " + filename);
        } else if (Variables.CSS.equals(parseExtension(filename))) {
            outFile = new File(cssPath, filename);
            Log.d(TAG, "Work with css, filename: " + filename);
        } else {
            outFile = null;
            Log.w(TAG, "Unknown type of filename, file will be skipped, filename: " + filename);
        }

        if (Objects.nonNull(outFile)) {
            try {
                outFile.createNewFile();
            } catch (IOException e) {
                Log.d(TAG, "ERROR: ", e);
                outFile = null;
            }
        }

        return outFile;
    }

    private boolean hasExtension(String fileName) {
        return fileName.lastIndexOf('.') > -1;
    }

    private String parseExtension(String fileName) {
        String extension;
        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        } else {
            extension = "";
        }

        return extension;
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private interface Variables extends Serializable {

        String SEPARATOR = "/";
        String JS = "js";
        String CSS = "css";

    }

}
