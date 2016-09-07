package org.rutor.team619.rutorclient.model;

import org.jsoup.select.Elements;

/**
 * Created by BORIS on 27.08.2016.
 */
public class BlankElements extends Elements {

    public static Elements newList() {
        return new BlankElements();
    }

    @Override
    public String toString() {
        return "";
    }

}
