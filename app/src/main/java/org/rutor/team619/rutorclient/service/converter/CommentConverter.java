package org.rutor.team619.rutorclient.service.converter;

import android.content.Context;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.rutor.team619.rutorclient.model.Comment;
import org.rutor.team619.rutorclient.service.converter.core.Converter;
import org.rutor.team619.rutorclient.service.storage.Storage;
import org.rutor.team619.rutorclient.util.AppUtil;

import java.util.List;

import static org.rutor.team619.rutorclient.service.storage.Storage.Code.COMMENT_CARD;

/**
 * Created by BORIS on 27.08.2016.
 */
public class CommentConverter extends DefaultConverter implements Converter<String, Elements> {

    private final Context context;
    private final Storage<Byte, String> storage;

    public CommentConverter(Context context, Storage<Byte, String> storage) {
        this.context = context;
        this.storage = storage;
        this.storage.set(COMMENT_CARD, this::getText);
    }

    @Override
    public String convert(Elements rawComments) {
        List<List<Element>> parts = AppUtil.batch(rawComments, 3);
        List<Comment> comments = parseComments(parts);
        return Stream.of(comments)
                .map(comment -> String.format(storage.get(COMMENT_CARD, this::getText),
                        comment.getId(),
                        comment.getCreated(),
                        comment.getAuthor(),
                        comment.getStars(),
                        comment.getBody()))
                .collect(Collectors.joining());
    }

    @Override
    public Storage<Byte, String> storage() {
        return storage;
    }

    @Override
    public String getText() {
        return readFromFile("comment-card.html", context);
    }

    @Override
    public Class<String> support() {
        return String.class;
    }

    /* =================== */

    private List<Comment> parseComments(List<List<Element>> comments) {
        return Stream.of(comments)
                .map(comment -> {

                    int index = 0;
                    Element head = comment.get(index++);
                    Element body = comment.get(index++);
                    Elements tds = head.getElementsByTag("td");
                    index = 0;
                    String userName = tds.get(index++).text();
                    String creationDate = tds.get(index++).text();
                    String mark = parseInteger(tds.get(index++).toString()).toString();
                    String commentId = tds.get(index++).getElementsByTag("span").first().attr("id");
                    String text = body.getElementsByTag("td").html().toString();

                    return new Comment(commentId, userName, creationDate, mark, text);
                })
                .collect(Collectors.toList());
    }

}
