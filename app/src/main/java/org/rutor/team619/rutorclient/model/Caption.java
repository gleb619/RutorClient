package org.rutor.team619.rutorclient.model;

import org.rutor.team619.rutorclient.model.core.DefaultEntity;

/**
 * Created by BORIS on 07.08.2016.
 */
public final class Caption implements DefaultEntity {

    private final String adaptedName;
    private final String originName;
    private final String subtitle;
    private final String year;

    public Caption(String title, String subtitle, String year) {
        this.adaptedName = (title != null && title.contains("/")) ? title.split("/")[0] : title;
        this.originName = (title != null && title.contains("/")) ? title.split("/")[1] : title;
        this.subtitle = subtitle;
        this.year = year;
    }

    public Caption(String title) {
        this.adaptedName = null;
        this.originName = title;
        this.subtitle = null;
        this.year = null;
    }

    public String getAdaptedName() {
        return adaptedName;
    }

    public String getOriginName() {
        return originName;
    }

    public String getTitle() {
        if (adaptedName == null) {
            return originName;
        } else {
            return adaptedName + "/" + originName;
        }
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getYear() {
        return year;
    }

}
