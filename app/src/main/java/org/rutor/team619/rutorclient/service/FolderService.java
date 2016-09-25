package org.rutor.team619.rutorclient.service;

import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;

import org.rutor.team619.rutorclient.model.settings.Settings;
import org.rutor.team619.rutorclient.util.Objects;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by BORIS on 23.09.2016.
 */
public class FolderService {

    private final Settings projectSettings;
    private final Storage storage;

    //TODO: Create business service interface, add storage there, don't save state in this service
    public FolderService(Settings project) {
        this.projectSettings = project;
        this.storage = new Storage();
    }

    private static String read(Supplier<String> getter, Function<String, String> setter, StringBuilder text) {
        String folder = getter.get();
        if (Objects.isNull(folder)) {
            folder = setter.apply(text.toString());
        }

        return folder;
    }

    public String rootDir() {
        return read(storage::getRoot, storage::setRoot, new StringBuilder()
                .append(projectSettings.value(Settings.Code.FOLDER_EXTERNAL_STORAGE))
                .append(projectSettings.value(Settings.Code.FOLDER_SEPARATOR))
                .append(projectSettings.value(Settings.Code.FOLDER_PROJECT)));
    }

    public String pagesDir() {
        return webServerFolder(storage::getPages, storage::setPages, Variables.HTML);
    }

    public String jsDir() {
        return webServerFolder(storage::getJs, storage::setJs, Variables.JS);
    }

    public String cssDir() {
        return webServerFolder(storage::getCss, storage::setCss, Variables.CSS);
    }

    public String imagesDir() {
        return webServerFolder(storage::getImages, storage::setImages, Variables.IMAGE);
    }

    public String cacheDir() {
        return webServerFolder(storage::getHttpCache, storage::setHttpCache, Variables.CACHE);
    }

    private String webServerFolder(Supplier<String> getter, Function<String, String> setter, String type) {
        return read(getter, setter, new StringBuilder(rootDir())
                .append(projectSettings.value(Settings.Code.FOLDER_SEPARATOR))
                .append(type)
                .append(projectSettings.value(Settings.Code.FOLDER_SEPARATOR)));
    }

    private interface Variables extends Serializable {

        String IMAGE = "img";
        String HTML = "page";
        String JS = "js";
        String CSS = "css";
        String CACHE = "cache";

    }

    private class Storage {

        private Reference<String> rootReference;
        private Reference<String> pagesReference;
        private Reference<String> jsReference;
        private Reference<String> cssReference;
        private Reference<String> imagesReference;
        private Reference<String> httpCacheReference;

        public String getRoot() {
            return get(rootReference);
        }

        public String setRoot(String input) {
            this.rootReference = new WeakReference<>(input);
            return getRoot();
        }

        public String getPages() {
            return get(pagesReference);
        }

        public String setPages(String input) {
            this.pagesReference = new WeakReference<>(input);
            return getPages();
        }

        public String getJs() {
            return get(jsReference);
        }

        public String setJs(String input) {
            this.jsReference = new WeakReference<>(input);
            return getJs();
        }

        public String getCss() {
            return get(cssReference);
        }

        public String setCss(String input) {
            this.cssReference = new WeakReference<>(input);
            return getCss();
        }

        public String getImages() {
            return get(imagesReference);
        }

        public String setImages(String input) {
            this.imagesReference = new WeakReference<>(input);
            return getImages();
        }

        private String get(Reference<String> reference) {
            return Objects.isNull(reference) ? null : reference.get();
        }

        public String getHttpCache() {
            return get(httpCacheReference);
        }

        public String setHttpCache(String input) {
            this.httpCacheReference = new WeakReference<>(input);
            return getHttpCache();
        }

    }

}
