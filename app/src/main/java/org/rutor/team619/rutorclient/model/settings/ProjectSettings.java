package org.rutor.team619.rutorclient.model.settings;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;

/**
 * Created by BORIS on 31.10.2015.
 */
public final class ProjectSettings implements Serializable {

    private final RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.BASIC;
    private final int httpConnectTimeout = 9000;
    private final int httpReadTimeout = 30000;
    private final long timeout = 100;
    private final long retry = 1;
    private final String url = "http://rutor.info";
    private final TimeUnit timeoutLatency = TimeUnit.SECONDS;
    private String cacheDir = "RUTOR_CLIENT";
    private int httpPort = 8080;
    private String address = "127.0.0.1";

    public int getHttpConnectTimeout() {
        return httpConnectTimeout;
    }

    public int getHttpReadTimeout() {
        return httpReadTimeout;
    }

    public RestAdapter.LogLevel getLogLevel() {
        return logLevel;
    }

    public long getRetry() {
        return retry;
    }

    public long getTimeout() {
        return timeout;
    }

    public String getUrl() {
        return url;
    }

    public TimeUnit getTimeoutLatency() {
        return timeoutLatency;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public void setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
