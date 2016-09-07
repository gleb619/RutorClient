package org.rutor.team619.rutorclient.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;

import org.rutor.team619.rutorclient.R;
import org.rutor.team619.rutorclient.model.settings.ProjectSettings;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by BORIS on 01.12.2015.
 */
public class AppUtil {

    private static final String TAG = AppUtil.class.getName() + ":";


    private final Context context;
    private final ProjectSettings projectSettings;

    public AppUtil(Context context, ProjectSettings projectSettings) {
        this.context = context;
        this.projectSettings = projectSettings;
    }

    public static <T> List<List<T>> batch(List<T> source, int length) {
        if (length <= 0) throw new IllegalArgumentException("length = " + length);
        int size = source.size();
        if (size <= 0) return new ArrayList<>();
        int fullChunks = (size - 1) / length;

        return IntStream.range(0, fullChunks + 1)
                .mapToObj(n ->
                        source.subList(n * length, n == fullChunks ? size : (n + 1) * length))
                .collect(Collectors.toList());
    }

    public <T> Observable<T> convert(Observable<T> observable) {
        return observable
                .timeout(projectSettings.getTimeout(), projectSettings.getTimeoutLatency())
                .retry(projectSettings.getRetry())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public <T> Observable<T> convert(T object) {
        return convert(Observable.just(object));
    }

    public Observable<String> test() {
        return rx.Observable.defer(() -> Observable.just(testData()));
    }

    @Nullable
    private String testData() {
        if (!isNetworkEnabled()) {
            return context.getResources().getString(R.string.message_no_network);
        } else if (!isInternetConnectionEstableshed2()) {
            return context.getResources().getString(R.string.message_no_internet);
        } else if (!isWiFiEnabled()) {
            return context.getResources().getString(R.string.message_no_wifi);
        }

        return null;
    }

    public boolean isNetworkEnabled() {
        boolean result = false;

        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            result = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception e) {
            Log.e(TAG, "#ERROR: ", e);
        }

        return result;
    }

    public boolean isWiFiEnabled() {
        boolean result = false;

        try {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            result = mWifi.isConnected() && mWifi.isAvailable();
        } catch (Exception e) {
            Log.e(TAG, "#ERROR: ", e);
        }

        return result;
    }

    public boolean isMobileConnectionEnabled() {
        boolean result = false;

        try {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            result = !(connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                    && connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getReason().equals("dataDisabled"));
        } catch (Exception e) {
            Log.e(TAG, "#ERROR: ", e);
        }

        return result;
    }

    public boolean isInternetConnectionEstableshed() {
        if (isNetworkEnabled()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e(TAG, "#ERROR: ", e);
            }
        }

        return false;
    }

    public boolean isInternetConnectionEstableshed2() {
        if (isNetworkEnabled()) {
            try {
                Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
                int returnVal = p1.waitFor();
                boolean reachable = (returnVal == 0);
                if (reachable) {
                    return reachable;
                }
            } catch (Exception e) {
                Log.e(TAG, "#ERROR: ", e);
            }
        }

        return false;
    }

    public String ip4() {
        try {
            return Stream.of(Collections.list(NetworkInterface.getNetworkInterfaces()))
                    .flatMap(ne -> Stream.of(Collections.list(ne.getInetAddresses())))
                    .filter(InetAddress::isLoopbackAddress)
                    .map(InetAddress::getHostAddress)
                    .map(Object::toString)
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            Log.e(TAG, "ERROR: ", e);
        }

        return null;
    }

    public String ip4_2() {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    public String ip4_3() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (isIPv4)
                            return sAddr;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "ERROR: ", e);
        } // for now eat exceptions

        return "";
    }

    public String ip4_4() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i(TAG, "***** IP=" + ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException e) {
            Log.e(TAG, "ERROR: ", e);
        }

        return null;
    }

    public String ip4_5() {
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();

        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));
    }

    public String ip4_6() {
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        String ipAddress = inetAddress.getHostAddress().toString();
                        Log.e("IP address", "" + ipAddress);
                        return ipAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        return null;
    }

    public String ip4_7() {
        return "localhost";
    }

}
