package org.rutor.team619.rutorclient.model;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

/**
 * Created by BORIS on 27.08.2016.
 */
public class BlankElement extends Element {

    public BlankElement() {
        super(Tag.valueOf("br"), "");
    }

    public BlankElement(Tag tag, String baseUri) {
        super(tag, baseUri);
    }

    public BlankElement(Tag tag, String baseUri, Attributes attributes) {
        super(tag, baseUri, attributes);
    }

    public static BlankElement newOne() {
        return new BlankElement();
    }

    @Override
    public void remove() {

    }

    @Override
    public String attr(String attributeKey) {
        return "";
    }

    @Override
    public String toString() {
        return "";
    }

}
