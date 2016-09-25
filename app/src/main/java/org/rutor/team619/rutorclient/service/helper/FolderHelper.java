package org.rutor.team619.rutorclient.service.helper;

import android.util.Log;

import com.annimon.stream.Stream;

import org.rutor.team619.rutorclient.model.settings.Settings;
import org.rutor.team619.rutorclient.service.FolderService;
import org.rutor.team619.rutorclient.service.helper.core.Helper;

import java.io.File;
import java.io.IOException;

/**
 * Created by BORIS on 28.08.2016.
 */
public class FolderHelper implements Helper {

    private static final String TAG = AssetsHelper.class.getName() + ":";

    private final Settings projectSettings;
    private final FolderService folderService;

    public FolderHelper(Settings projectSettings, FolderService folderService) {
        this.projectSettings = projectSettings;
        this.folderService = folderService;
    }

    @Override
    public void onHelp() throws IOException {
        Stream.of(
                folderService.imagesDir(),
                folderService.pagesDir(),
                folderService.jsDir(),
                folderService.cssDir(),
                folderService.cacheDir())
                .map(path -> new File(path))
                .filter(file -> Boolean.FALSE.equals(file.exists()))
                .peek(file -> Log.d(TAG, "Create file " + file.getPath()))
                .forEach(file -> file.mkdirs());
    }

}
