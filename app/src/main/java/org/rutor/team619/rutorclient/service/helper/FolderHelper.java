package org.rutor.team619.rutorclient.service.helper;

import android.os.Environment;

import com.annimon.stream.Stream;

import org.rutor.team619.rutorclient.model.settings.ProjectSettings;
import org.rutor.team619.rutorclient.service.helper.core.Helper;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Arrays;

/**
 * Created by BORIS on 28.08.2016.
 */
public class FolderHelper implements Helper {

    private static final String TAG = AssetsHelper.class.getName() + ":";

    private final Reference<ProjectSettings> projectSettings;

    public FolderHelper(ProjectSettings projectSettings) {
        this.projectSettings = new SoftReference<>(projectSettings);
    }

    @Override
    public void onHelp() throws IOException {
        String projectPath = Environment.getExternalStoragePublicDirectory(
                projectSettings.get().getCacheDir())
                .getAbsolutePath();

        StringBuilder imagePath = new StringBuilder(projectPath)
                .append(Variables.SEPARATOR)
                .append(Variables.IMAGE)
                .append(Variables.SEPARATOR);

        StringBuilder htmlPath = new StringBuilder(projectPath)
                .append(Variables.SEPARATOR)
                .append(Variables.HTML)
                .append(Variables.SEPARATOR);

        StringBuilder jsPath = new StringBuilder(projectPath)
                .append(Variables.SEPARATOR)
                .append(Variables.JS)
                .append(Variables.SEPARATOR);

        StringBuilder cssPath = new StringBuilder(projectPath)
                .append(Variables.SEPARATOR)
                .append(Variables.CSS)
                .append(Variables.SEPARATOR);


        Stream.of(Arrays.asList(imagePath, htmlPath, jsPath, cssPath))
                .map(builder -> builder.toString())
                .map(path -> new File(path))
                .filter(file -> Boolean.FALSE.equals(file.exists()))
                .forEach(file -> file.mkdirs());
    }

    private interface Variables extends Serializable {

        String SEPARATOR = "/";
        String IMAGE = "img";
        String HTML = "page";
        String JS = "js";
        String CSS = "css";

    }


}
