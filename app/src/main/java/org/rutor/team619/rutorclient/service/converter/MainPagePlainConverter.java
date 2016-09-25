package org.rutor.team619.rutorclient.service.converter;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.jsoup.nodes.Document;
import org.rutor.team619.rutorclient.model.MainPlainPage;
import org.rutor.team619.rutorclient.service.converter.core.Converter;
import org.rutor.team619.rutorclient.service.storage.Storage;
import org.rutor.team619.rutorclient.util.Objects;

import java.io.Serializable;

/**
 * Created by BORIS on 07.08.2016.
 */
public class MainPagePlainConverter extends RowConverter implements Converter<MainPlainPage, Document> {

    private static final String TAG = MainPagePlainConverter.class.getName() + ":";

    @Override
    public MainPlainPage convert(Document input) {
        return new MainPlainPage(Stream.of(input.select(Selectors.ROWS))
                .skip(1)
                .map(element -> parseRow(element.getElementsByTag(Selectors.TD)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    @Override
    public Class<MainPlainPage> support() {
        return MainPlainPage.class;
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

    public interface Selectors extends Serializable {

        String ROWS = "#index tr";
        String TD = "td";

    }

}
