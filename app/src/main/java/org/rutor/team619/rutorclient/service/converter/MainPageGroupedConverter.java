package org.rutor.team619.rutorclient.service.converter;

import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.rutor.team619.rutorclient.model.Group;
import org.rutor.team619.rutorclient.model.MainGroupedPage;
import org.rutor.team619.rutorclient.model.Row;
import org.rutor.team619.rutorclient.service.converter.core.Converter;
import org.rutor.team619.rutorclient.service.storage.Storage;
import org.rutor.team619.rutorclient.util.AppUtil;
import org.rutor.team619.rutorclient.util.Objects;

import java.io.Serializable;
import java.util.List;

/**
 * Created by BORIS on 07.08.2016.
 */
public class MainPageGroupedConverter extends RowConverter implements Converter<MainGroupedPage, Document> {

    private static final String TAG = MainPageGroupedConverter.class.getName() + ":";

    @Override
    public MainGroupedPage convert(Document input) {
        List<List<Element>> rawGroups = AppUtil.batch(
                Stream.of(input.select(Selectors.GROUP).first().children())
                        .skip(1)
                        .collect(Collectors.toList()), 2);
        List<Group> groups = Stream.of(rawGroups)
                .map(group -> convertToGroup(group))
                .collect(Collectors.toList());

        return new MainGroupedPage(groups);
    }

    @Override
    public Class<MainGroupedPage> support() {
        return MainGroupedPage.class;
    }

    @Override
    public Storage<Byte, String> storage() {
        return null;
    }

    @Override
    public String getText() {
        return null;
    }

    /* =================== */

    @NonNull
    private Group convertToGroup(List<Element> group) {
        int index = 0;
        Element header = group.get(index++);
        Element topics = group.get(index++);
        List<Row> rows = Stream.of(topics.select(Selectors.ROWS))
                .skip(1)
                .map(tr -> parseRow(tr.getElementsByTag(Selectors.TD)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new Group(header.text(), rows);
    }

    public interface Selectors extends Serializable {

        String GROUP = "#index";
        String ROWS = "tbody > tr";
        String TD = "td";

    }

}
