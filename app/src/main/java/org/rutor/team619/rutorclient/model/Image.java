package org.rutor.team619.rutorclient.model;

import org.rutor.team619.rutorclient.model.core.DefaultEntity;

/**
 * Created by BORIS on 07.08.2016.
 */
public class Image implements DefaultEntity {

    private int id;
    private String src;
    private String extension;
    private int index;
    private int weight;
    private int size;

    public Image() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Image(int id, String src, String extension, int index, int weight,
                 int size) {
        super();
        this.id = id;
        this.src = src;
        this.extension = extension;
        this.index = index;
        this.weight = weight;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public Image setId(int id) {
        this.id = id;
        return this;
    }

    public String getSrc() {
        return src;
    }

    public Image setSrc(String src) {
        this.src = src;
        return this;
    }

    public String getExtension() {
        return extension;
    }

    public Image setExtension(String extension) {
        this.extension = extension;
        return this;
    }

    public int getIndex() {
        return index;
    }

    public Image setIndex(int index) {
        this.index = index;
        return this;
    }

    public int getWeight() {
        return weight;
    }

    public Image setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public int getSize() {
        return size;
    }

    public Image setSize(int size) {
        this.size = size;
        return this;
    }
}
