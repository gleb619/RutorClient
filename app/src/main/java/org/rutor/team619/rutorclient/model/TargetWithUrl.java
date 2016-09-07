package org.rutor.team619.rutorclient.model;

import com.squareup.picasso.Target;

/**
 * Created by BORIS on 29.11.2015.
 */
public abstract class TargetWithUrl implements Target {

    private String url;

    public TargetWithUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
