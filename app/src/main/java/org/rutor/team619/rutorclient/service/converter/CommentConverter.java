package org.rutor.team619.rutorclient.service.converter;

import android.content.Context;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.rutor.team619.rutorclient.model.Comment;
import org.rutor.team619.rutorclient.service.converter.core.Converter;
import org.rutor.team619.rutorclient.util.AppUtil;

import java.util.List;

/**
 * Created by BORIS on 27.08.2016.
 */
public class CommentConverter extends DefaultConverter implements Converter<String, Elements> {

    private final String card;

    public CommentConverter(Context context) {
        this.card = readFromFile("comment-card.html", context);
    }

    @Override
    public String convert(Elements rawComments) {
        List<List<Element>> parts = AppUtil.batch(rawComments, 3);
        List<Comment> comments = parseComments(parts);
        return Stream.of(comments)
                .map(comment -> String.format(card,
                        comment.getId(),
                        comment.getCreated(),
                        comment.getAuthor(),
                        comment.getStars(),
                        comment.getBody()))
                .collect(Collectors.joining());
    }

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

    @Override
    public Class<String> support() {
        return String.class;
    }

}
