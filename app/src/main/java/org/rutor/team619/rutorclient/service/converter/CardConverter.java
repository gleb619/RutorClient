package org.rutor.team619.rutorclient.service.converter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.rutor.team619.rutorclient.model.Row;
import org.rutor.team619.rutorclient.service.converter.core.Converter;
import org.rutor.team619.rutorclient.service.storage.Storage;
import org.rutor.team619.rutorclient.util.Objects;

import static org.rutor.team619.rutorclient.service.storage.Storage.Code.CARD;

/**
 * Created by BORIS on 27.08.2016.
 */
public class CardConverter extends RowConverter implements Converter<String, Elements> {

    private final Context context;
    private final Storage<Byte, String> storage;

    public CardConverter(Context context, Storage<Byte, String> storage) {
        this.context = context;
        this.storage = storage;
        this.storage.set(CARD, this::getText);
    }

    @Override
    public String convert(Elements rows) {
        return Stream.of(rows)
                .skip(1)
                .map(tr -> convertTr(tr))
                .filter(Objects::nonNull)
                .collect(Collectors.joining());
    }

    @Nullable
    public String convertTr(Element tr) {
        Row row = parseRow(tr.getElementsByTag("td"));
        if (Objects.isNull(row)) {
            return null;
        }

        return String.format(storage.get(CARD, this::getText),
                row.getCaption().getAdaptedName(),
                row.getCaption().getOriginName(),
                row.getCaption().getYear() + " " + row.getCaption().getSubtitle(),
                row.getSize(),
                row.getSeeds(),
                row.getPeers(),
                row.getComments());
    }

    @Override
    public String getText() {
        return readFromFile("card.html", context);
    }

    @Override
    public Class<String> support() {
        return String.class;
    }

    @Override
    public Storage<Byte, String> storage() {
        return storage;
    }

}
