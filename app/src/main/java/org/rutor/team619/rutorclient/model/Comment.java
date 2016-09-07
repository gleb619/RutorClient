package org.rutor.team619.rutorclient.model;

import org.rutor.team619.rutorclient.model.core.DefaultEntity;

/**
 * Created by BORIS on 02.09.2016.
 */
public class Comment implements DefaultEntity {

    private final String id;
    private final String author;
    private final String created;
    private final String stars;
    private final String body;

    public Comment(String id, String author, String created, String stars, String body) {
        this.id = id;
        this.author = author;
        this.created = created;
        this.stars = stars;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public String getCreated() {
        return created;
    }

    public String getId() {
        return id;
    }

    public String getStars() {
        return stars;
    }

    public String getAuthor() {
        return author;
    }
}
