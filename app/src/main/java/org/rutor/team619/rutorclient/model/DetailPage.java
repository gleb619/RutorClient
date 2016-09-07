package org.rutor.team619.rutorclient.model;

import org.rutor.team619.rutorclient.model.core.DefaultEntity;

/**
 * Created by BORIS on 07.08.2016.
 */
public class DetailPage implements DefaultEntity {

    private String fileName;

    public DetailPage(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
