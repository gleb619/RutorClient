package org.rutor.team619.rutorclient.service.converter;

import android.content.Context;

import org.jsoup.nodes.Element;
import org.rutor.team619.rutorclient.model.Row;
import org.rutor.team619.rutorclient.service.converter.core.Converter;

/**
 * Created by BORIS on 27.08.2016.
 */
public class CardConverter extends RowConverter implements Converter<String, Element> {

    private final String card;

    public CardConverter(Context context) {
        this.card = readFromFile("card.html", context);
    }

    @Override
    public String convert(Element tr) {
        Row row = parseRow(tr.getElementsByTag("td"));
        return String.format(card,
                row.getCaption().getAdaptedName(),
                row.getCaption().getOriginName(),
                row.getCaption().getYear() + " " + row.getCaption().getSubtitle(),
                row.getSize(),
                row.getSeeds(),
                row.getPeers(),
                row.getComments());
    }

    @Override
    public Class<String> support() {
        return String.class;
    }

}
