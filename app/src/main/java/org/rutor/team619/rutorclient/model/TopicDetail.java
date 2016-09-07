package org.rutor.team619.rutorclient.model;

import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by BORIS on 21.08.2016.
 */
public class TopicDetail implements Serializable {

    private final String id;
    private final String name;
    private final String url;

    public TopicDetail(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public TopicDetail(TopicDetail topicDetalization) {
        this(topicDetalization.getId(), topicDetalization.getName(), topicDetalization.getUrl());
    }

    public TopicDetail(Bundle arguments) {
        this(arguments.getString("id"), arguments.getString("name"), arguments.getString("url"));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
