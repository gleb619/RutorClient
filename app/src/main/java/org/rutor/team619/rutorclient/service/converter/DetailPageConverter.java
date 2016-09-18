package org.rutor.team619.rutorclient.service.converter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.rutor.team619.rutorclient.model.BlankElement;
import org.rutor.team619.rutorclient.model.BlankElements;
import org.rutor.team619.rutorclient.model.DetailPage;
import org.rutor.team619.rutorclient.model.settings.ProjectSettings;
import org.rutor.team619.rutorclient.service.converter.core.Converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by BORIS on 07.08.2016.
 */
public class DetailPageConverter extends DefaultConverter implements Converter<DetailPage, Document> {

    private static final String TAG = DetailPageConverter.class.getName() + ":";

    private final ProjectSettings project;
    private final String template;
    private final CardConverter cardConverter;
    private final CommentConverter commentConverter;


    public DetailPageConverter(ProjectSettings project, Context context, CardConverter cardConverter, CommentConverter commentConverter) {
        this.project = project;
        this.template = readFromFile("template.html", context);
        this.cardConverter = cardConverter;
        this.commentConverter = commentConverter;
    }

    @Override
    public DetailPage convert(Document input) {
        String fileName = parseIdentifier(input);

//        if(!containsInCache(fileName)){
        createNewOne(input, fileName);
//        }

        return new DetailPage(new StringBuilder(Selectors.PROTOCOL_HTTP)
                .append(project.getAddress())
                .append(Selectors.COLON)
                .append(project.getHttpPort())
                .append(Selectors.BACKSLASH)
                .append(fileName)
                .toString());
    }

    private String parseIdentifier(Document document) {
        String id;
        if (document.location() != null && document.location().length() > 0) {
            id = document.location();
        } else if (document.title() != null && document.title().length() > 0) {
            id = document.title().hashCode() + "";
        } else {
            id = "";
        }

        return parseFileName(id);
    }

    private boolean containsInCache(String fileName) {
        return new File(new StringBuilder(getStorageDir())
                .append(fileName)
                .toString()).exists();
    }

    private String getStorageDir() {
        return new StringBuilder(Environment.getExternalStoragePublicDirectory(
                project.getCacheDir()).getAbsolutePath())
                .append(Selectors.PAGE_FOLDER)
                .append("/")
                .toString();
    }

    private void createNewOne(Document document, String fileName) {
        if (!isExternalStorageWritable() || document == null || fileName == null) {
            return;
        }

        try {
            String content;
            Element htmlBody = document.body();
            String H1header = htmlBody.select(Selectors.HEADER).text();
            Element mainContent = htmlBody.select(Selectors.CONTENT).first();

            Element mainLink = Optional.ofNullable(htmlBody.select(Selectors.TOPIC_ID).first())
                    .orElse(BlankElement.newOne());
            String id = Integer.toString(parseId(mainLink.attr("href").toString()));
            String link = mainLink.attr("href");
            Element table = mainContent.select(Selectors.MAIN_TABLE).first();
            String topicSpecification = Optional.ofNullable(table.select(Selectors.TOPIC_DETAILS)
                    .first()).orElse(BlankElement.newOne()).toString();
            Element boundedContentElement = Optional.ofNullable(table.select(Selectors.TOPIC_BOUNDED).first())
                    .orElse(BlankElement.newOne());
            Elements commentsContentElement = Optional.ofNullable(mainContent.select(Selectors.TOPIC_COMMENTS))
                    .orElse(BlankElements.newList());

            Optional.ofNullable(table.select(Selectors.TOPIC_FILE).first()).orElse(BlankElement.newOne()).remove();
            Optional.ofNullable(table.select(Selectors.TOPIC_BOUNDED).first()).orElse(BlankElement.newOne()).remove();
            Optional.ofNullable(table.getElementsByTag(Selectors.TR).first()).orElse(BlankElement.newOne()).remove();

            String distributionDetails = Optional.ofNullable(table.getElementsByTag(Selectors.TR))
                    .orElse(BlankElements.newList()).toString();
            String boundedContent = Stream.of(boundedContentElement.select(Selectors.TOPIC_BOUNDED_DETAILS))
                    .skip(1)
                    .map(tr -> cardConverter.convert(tr))
                    .collect(Collectors.joining());
            String commentsContent = commentConverter.convert(commentsContentElement);

            topicSpecification = topicSpecification.replace("hidehead", "hidehead plus");

            content = String.format(template,
                    H1header,
                    link,
                    topicSpecification,
                    distributionDetails,
                    boundedContent,
                    id,
                    commentsContent);

            if (isExternalStorageWritable()) {
                try {
                    writeText(content, fileName);
                } catch (IOException e) {
                    Log.e(TAG, "ERROR: ", e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "ERROR:", e);
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private String parseFileName(String location) {
        return new StringBuilder(location.replaceAll("[^A-z0-9]+", ""))
                .append(Selectors.EXTENSION)
                .toString();
    }

    private void writeText(String text, String name) throws IOException {
        File file = getStorageDir(name);
        FileOutputStream stream = null;

        try {
            stream = new FileOutputStream(file);
            stream.write(text.getBytes("UTF-8"));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "ERROR: ", e);
        } catch (IOException e) {
            Log.e(TAG, "ERROR: ", e);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private File getStorageDir(String fileName) throws IOException {
        File storageDir = new File(getStorageDir());
        if (!storageDir.mkdirs()) {
            Log.e(TAG, "Directory not created, fileName: " + fileName);
        }

        File output = new File(storageDir.getAbsolutePath() + "/" + fileName);
        output.createNewFile();

        return output;
    }

    @Override
    public Class<DetailPage> support() {
        return DetailPage.class;
    }

    public interface Selectors extends Serializable {

        String PAGE_FOLDER = "/page";
        String CSS_FOLDER = "/css";
        String JS_FOLDER = "/js";
        String MIME_TYPE_JS = "js";
        String MIME_TYPE_CSS = "css";
        String MIME_TYPE_HTML = "html";
        String NEW_LINE = "\n";
        String HEADER = "#all > h1";
        String CONTENT = "#content";
        String MAIN_TABLE = "#details > tbody";
        String TR = "tr";
        String TOPIC_ID = "#download > a:nth-child(2)";
        String TOPIC_DETAILS = "tr:nth-child(1) > td:nth-child(2)";
        String TOPIC_BOUNDED = "tr:nth-child(11)";
        String TOPIC_BOUNDED_DETAILS = "#index > fieldset > table > tbody > tr";
        String TOPIC_FILE = "tr:nth-child(12)";
        String TOPIC_COMMENTS = "#content > table:nth-child(16) > tbody > tr";

        String[] HEAD_JUNK = new String[]{
                "head > script:nth-child(6)",
                "head > script:nth-child(6)",
                "head > script:nth-child(6)",
//                "head > link:nth-child(2)",
        };
        String[] BODY_JUNK = new String[]{
                "#content > center:first-child",
                "#details > tbody > tr:nth-child(1) > td:nth-child(1)",
        };
        String COLON = ":";
        String BACKSLASH = "/";
        String PROTOCOL_FILE = "file://";
        String PROTOCOL_HTTP = "http://";
        String EXTENSION = ".html";
        String EXTENSION_CSS = "" +
                "<!--   /* ================= */ -->" +
                "<meta name='viewport' content='width=device-width, initial-scale=1'>" +
//                "<link rel='stylesheet' href='http://fonts.googleapis.com/css?family=Raleway:400,300,600' type='text/css'>" +
                "<link rel='stylesheet' href='../css/normalize.css' type='text/css'>" + //media='bogus'
                "<link rel='stylesheet' href='../css/skeleton.css' type='text/css'>" +
                "<link rel='stylesheet' href='../css/custom.css' type='text/css'>" +
                "<script type='text/javascript' src='../js/jquery.min.js'></script>" + // async defer
                "<script type='text/javascript' src='../js/jquery.cookie-min.js'></script>" +
                "<script type='text/javascript' src='../js/functions.js'></script>";
        String INDENT = "<br><br><br>";

    }

}
