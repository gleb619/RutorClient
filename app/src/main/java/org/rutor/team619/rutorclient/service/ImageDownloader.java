package org.rutor.team619.rutorclient.service;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.rutor.team619.colorthief.ColorThief;
import org.rutor.team619.rutorclient.R;
import org.rutor.team619.rutorclient.service.core.Clearable;
import org.rutor.team619.rutorclient.util.Objects;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by BORIS on 21.08.2016.
 */
public class ImageDownloader implements Clearable {

    private static final String TAG = HttpServer.class.getName() + ":";

    public final WebAppInterface webAppInterface;
    private final String address;
    private final Resources resources;
    private final CommandsExecutor commandsExecutor;
    private final DeviceInformationService deviceInformationService;

    private transient Toolbar toolbar;
    private transient Window window;
    private transient WebView view;

    public ImageDownloader(String address, Resources resources, CommandsExecutor commandsExecutor, DeviceInformationService deviceInformationService) {
        this.commandsExecutor = commandsExecutor;
        this.deviceInformationService = deviceInformationService;
        this.webAppInterface = new WebAppInterface();
        this.address = address;
        this.resources = resources;
    }

    public void loadImage(String url) {
        Observable.defer(() -> Observable.just(getBitmapFromURL(url)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(Objects::nonNull)
                .subscribe(bitmap -> {
                    List<Integer> colors = getColors(bitmap);
                    updateToolbarColor(colors.get(0));

                    Observable.defer(() -> Observable.just(convertToCssStyles(colors)))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(css -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    getView().evaluateJavascript(new StringBuilder()
                                            .append("(function() { ")
                                            .append("$('head').append('<style> ")
                                            .append(css)
                                            .append("</style>');")
                                            .append("})();")
                                            .toString(), null);
                                }
                            }, e -> Log.e(TAG, "ERROR: ", e));
                }, e -> {
                    Log.e(TAG, "ERROR: ", e);
                });
    }

    private void updateToolbarColor(Integer color) {
        if (Objects.isNull(getToolbar()) || Objects.isNull(getWindow())) {
            return;
        }

        getToolbar().setBackgroundColor(color);
        getToolbar().getBackground().setAlpha(235);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int statusBarColor = isColorDark(color) ? ligthenColor(color) : darkenColor(color);
            getWindow().setStatusBarColor(statusBarColor);
            getWindow().setNavigationBarColor(statusBarColor);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            updateMargin(toolbar);
            updateMargin(view);
        }
    }

    private void updateMargin(View view) {
        changeMargin(view, getStatusBarHeight());
    }

    private void clearMargin(View view) {
        changeMargin(view, 0);
    }

    private void changeMargin(View view, int height) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.setMargins(0, height, 0, 0);
        view.setLayoutParams(params);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //TODO: refactor this part
    private String convertToCssStyles(List<Integer> colors) {
        final int[] index = {0};
        List<String> textRules = Stream.of(colors)
                .map(color -> createCssRule(index, color, "color"))
                .map(builder -> builder.toString())
                .collect(Collectors.toList());
        index[0] = 0;
        List<String> boxRules = Stream.of(colors)
                .map(color -> createCssRule(index, color, "background-color"))
                .map(builder -> builder.toString())
                .collect(Collectors.toList());
        index[0] = 0;
        List<String> borderRules = Stream.of(colors)
                .map(color -> createCssRule(index, color, "border-color"))
                .map(builder -> builder.toString())
                .collect(Collectors.toList());

        List<String> mainColor = Stream.of(Arrays.asList(textRules.get(0), boxRules.get(0), borderRules.get(0)))
                .map(text -> text.replace(".c-", ".main-").replaceAll("-\\d+\\{", "{"))
                .collect(Collectors.toList());

        Integer darkestIndex = Stream.of(colors)
                .map(color -> new Pair<>(colors.indexOf(color), getDarkness(color)))
                .max((first, second) -> Double.compare(first.second, second.second))
                .map(pair -> pair.first)
                .orElse(colors.get(0));

        Integer lightnessIndex = Stream.of(colors)
                .map(color -> new Pair<>(colors.indexOf(color), getDarkness(color)))
                .min((first, second) -> Double.compare(first.second, second.second))
                .map(pair -> pair.first)
                .orElse(colors.get(0));

        List<String> darkestColor = Stream.of(Arrays.asList(textRules.get(darkestIndex), boxRules.get(darkestIndex), borderRules.get(darkestIndex)))
                .map(text -> text.replace(".c-", ".dark-").replaceAll("-\\d+\\{", "{"))
                .collect(Collectors.toList());

        List<String> lightnessColor = Stream.of(Arrays.asList(textRules.get(lightnessIndex), boxRules.get(lightnessIndex), borderRules.get(lightnessIndex)))
                .map(text -> text.replace(".c-", ".light-").replaceAll("-\\d+\\{", "{"))
                .collect(Collectors.toList());

        return new StringBuilder()
                .append(Stream.of(textRules).collect(Collectors.joining()))
                .append(Stream.of(boxRules).collect(Collectors.joining()))
                .append(Stream.of(borderRules).collect(Collectors.joining()))
                .append(Stream.of(mainColor).collect(Collectors.joining()))
                .append(Stream.of(darkestColor).collect(Collectors.joining()))
                .append(Stream.of(lightnessColor).collect(Collectors.joining()))
                .toString();
    }

    private StringBuilder createCssRule(int[] index, Integer color, String rule) {
        return new StringBuilder(".c-")
                .append(rule)
                .append("-")
                .append(index[0]++)
                .append("{")
                .append(rule)
                .append(":")
                .append("#" + Integer.toHexString(color).substring(2))
                .append(";")
                .append("}");
    }

    public List<Integer> getColors(Bitmap bitmap) {
        int[][] colors = detectColors(bitmap);

        if (colors != null && colors.length > 0) {
            return Stream.of(colors)
                    .map(rgb -> Integer.valueOf(Color.rgb(rgb[0], rgb[1], rgb[2])))
                    .collect(Collectors.toList());
        }

        return Arrays.asList(Color.parseColor("#E60000"));
    }

    public int[][] detectColors(Bitmap bitmap) {
        int[][] colors = null;
        try {
            colors = ColorThief.getPalette(bitmap);
        } catch (Exception e) {
            Log.e(TAG, "ERROR: ", e);
        }
        return colors;
    }

    public int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.7f;
        return Color.HSVToColor(hsv);
    }

    public int ligthenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 1.3f;
        return Color.HSVToColor(hsv);
    }

    public Bitmap getBitmapFromURL(String src) {
        Bitmap myBitmap = null;

        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            myBitmap = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            Log.e(TAG, "ERROR: ", e);
        }

        return myBitmap;
    }

    public boolean isColorDark(int color) {
        return getDarkness(color) >= 0.5;
    }

    private double getDarkness(int color) {
        return 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public WebView getView() {
        return view;
    }

    public void setView(WebView view) {
        this.view = view;
    }

    public String getAddress() {
        return address;
    }

    public WebAppInterface getWebAppInterface() {
        return webAppInterface;
    }

    @Override
    public void clear() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            clearMargin(toolbar);
            clearMargin(view);
        }

        getToolbar().getBackground().setAlpha(255);
        getWindow().setStatusBarColor(resources.getColor(R.color.primary_dark));
        getWindow().setNavigationBarColor(resources.getColor(R.color.primary_dark));
        getToolbar().setBackgroundColor(resources.getColor(R.color.primary));
        getToolbar().setTitle(resources.getString(R.string.app_name));

        toolbar = null;
        window = null;
        view = null;
    }

    public class WebAppInterface {

        @JavascriptInterface
        public void onFoundBiggestImage(String url) {
            loadImage(url);
        }

        @JavascriptInterface
        public String originAddress() {
            return getAddress();
        }

        @JavascriptInterface
        public void onWarning(String message) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }

        @JavascriptInterface
        public void remoteDownload(String url) {
            commandsExecutor.sendCommand(deviceInformationService.id(), url);
        }

    }
}
