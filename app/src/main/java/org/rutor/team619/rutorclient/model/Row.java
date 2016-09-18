package org.rutor.team619.rutorclient.model;

import org.rutor.team619.rutorclient.model.core.DefaultEntity;

/**
 * Created by BORIS on 07.08.2016.
 */
public class Row implements DefaultEntity {

    private int id = -1;
    private String creationDate = "";
    private Caption caption;
    private String size = "";
    private int seeds = 0;
    private int peers = 0;
    private int comments = 0;
    private String detailUrl = "";
    private String downloadUrl = "";
    private String magnetUrl = "";
    private String name = "";

    public Row() {
        super();
    }

    public Row(int id, String creationDate, Caption caption, String size,
               int seeds, int peers, int comments, String detailUrl,
               String downloadUrl, String magnetUrl, String name) {
        super();
        this.id = id;
        this.creationDate = creationDate;
        this.caption = caption;
        this.size = size;
        this.seeds = seeds;
        this.peers = peers;
        this.comments = comments;
        this.detailUrl = detailUrl;
        this.downloadUrl = downloadUrl;
        this.magnetUrl = magnetUrl;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public Row setId(int id) {
        this.id = id;
        return this;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public Row setCreationDate(String creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public Caption getCaption() {
        return caption;
    }

    public void setCaption(Caption caption) {
        this.caption = caption;
    }

    public String getSize() {
        return size;
    }

    public Row setSize(String size) {
        this.size = size;
        return this;
    }

    public int getSeeds() {
        return seeds;
    }

    public Row setSeeds(int seeds) {
        this.seeds = seeds;
        return this;
    }

    public int getPeers() {
        return peers;
    }

    public Row setPeers(int peers) {
        this.peers = peers;
        return this;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public Row setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
        return this;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public Row setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }

    public String getMagnetUrl() {
        return magnetUrl;
    }

    public Row setMagnetUrl(String magnetUrl) {
        this.magnetUrl = magnetUrl;
        return this;
    }

    public String getName() {
        return name;
    }

    public Row setName(String name) {
        this.name = name;
        return this;
    }
}
