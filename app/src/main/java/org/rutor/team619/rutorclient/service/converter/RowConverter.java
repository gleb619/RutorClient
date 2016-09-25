package org.rutor.team619.rutorclient.service.converter;

import org.jsoup.nodes.Element;
import org.rutor.team619.rutorclient.model.Row;

import java.io.Serializable;
import java.util.List;

/**
 * Created by BORIS on 27.08.2016.
 */
public abstract class RowConverter extends DefaultConverter {

    protected static Row parseRow(List<Element> torrentRow) {
        Row row = null;

        if (torrentRow.size() >= 4) {
            row = new Row();
            int torrentRowIndex = parseRowUrls(torrentRow, row);

            if (torrentRow.size() == 4) {
                row.setSize(parseString(torrentRow.get(torrentRowIndex++).text()));
            } else if (torrentRow.size() >= 5) {
                row.setComments(parseInteger(torrentRow.get(torrentRowIndex++).text()));
                row.setSize(parseString(torrentRow.get(torrentRowIndex++).text()));
            }

            List<Element> downloadInfoDetails = torrentRow.get(torrentRowIndex++).getElementsByTag(Selectors.SPAN);
            if (downloadInfoDetails.size() >= 2) {
                int downloadInfoDetailsIndex = 0;
                row.setSeeds(parseInteger(downloadInfoDetails.get(downloadInfoDetailsIndex++).text()))
                        .setPeers(parseInteger(downloadInfoDetails.get(downloadInfoDetailsIndex++).text()));
            }
        }

        return row;
    }

    protected static int parseRowUrls(List<Element> torrentRow, Row row) {
        int torrentRowIndex = 0;
        row.setCreationDate(torrentRow.get(torrentRowIndex++).text());
        List<Element> captionDetails = torrentRow.get(torrentRowIndex++).getElementsByTag(Selectors.LINK);
        if (captionDetails.size() >= 3) {
            int captionDetailsIndex = 0;

            row.setDownloadUrl(captionDetails.get(captionDetailsIndex++).attr(Selectors.HREF))
                    .setId(parseId(row.getDownloadUrl()))
                    .setMagnetUrl(captionDetails.get(captionDetailsIndex++).attr(Selectors.HREF))
                    .setDetailUrl(captionDetails.get(captionDetailsIndex).attr(Selectors.HREF))
                    .setName(parseTorrentName(captionDetails.get(captionDetailsIndex).attr(Selectors.HREF)))
                    .setCaption(parseCaption(captionDetails.get(captionDetailsIndex++).text()));
        }

        return torrentRowIndex;
    }
    
    /* =================== */

    public interface Selectors extends Serializable {

        String LINK = "a";
        String HREF = "href";
        String SPAN = "span";

    }

}
