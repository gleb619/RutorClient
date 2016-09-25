package org.rutor.team619.rutorclient.service.converter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.annimon.stream.Optional;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.rutor.team619.rutorclient.model.BlankElement;
import org.rutor.team619.rutorclient.model.BlankElements;
import org.rutor.team619.rutorclient.model.DetailPage;
import org.rutor.team619.rutorclient.model.settings.Settings;
import org.rutor.team619.rutorclient.service.FolderService;
import org.rutor.team619.rutorclient.service.converter.core.Converter;
import org.rutor.team619.rutorclient.service.storage.Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import static org.rutor.team619.rutorclient.model.settings.Settings.Code.TRIM_MODE;
import static org.rutor.team619.rutorclient.service.storage.Storage.Code.MAIN;

/**
 * Created by BORIS on 07.08.2016.
 */
public class DetailPageConverter extends DefaultConverter implements Converter<DetailPage, Document> {

    private static final String TAG = DetailPageConverter.class.getName() + ":";

    private final Settings project;
    private final Context context;
    private final CardConverter cardConverter;
    private final CommentConverter commentConverter;
    private final FolderService folderService;
    private final Storage<Byte, String> storage;


    public DetailPageConverter(Settings project, Context context, FolderService folderService, Storage<Byte, String> storage, CardConverter cardConverter, CommentConverter commentConverter) {
        this.project = project;
        this.context = context;
        this.storage = storage;
        this.folderService = folderService;
        this.cardConverter = cardConverter;
        this.commentConverter = commentConverter;
        this.storage.set(MAIN, this::getText);
    }

    private static String parseIdentifier(Document document) {
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

    private static Element parseElement(Element element) {
        return Optional.ofNullable(element).orElse(BlankElement.newOne());
    }

    private static Elements parseElement(Elements element) {
        return Optional.ofNullable(element).orElse(BlankElements.newList());
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* =================== */

    private static String parseFileName(String location) {
        return new StringBuilder(location.replaceAll("[^A-z0-9]+", ""))
                .append(Selectors.EXTENSION)
                .toString();
    }

    private static void writeText(String text, File file) throws IOException {
        if (Boolean.FALSE.equals(isExternalStorageWritable())) {
            return;
        }
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

    @Override
    public DetailPage convert(Document input) {
        String fileName = parseIdentifier(input);

//        if(!containsInCache(fileName)){
        createNewOne(input, fileName);
//        }

        return new DetailPage(createLink(fileName));
    }

    @Override
    public Class<DetailPage> support() {
        return DetailPage.class;
    }

    @Override
    public String getText() {
        return readFromFile("template.html", context);
    }

    @Override
    public Storage<Byte, String> storage() {
        return storage;
    }

    private String createLink(String fileName) {
        return new StringBuilder(Selectors.PROTOCOL_HTTP)
                .append(project.getAddress())
                .append(Selectors.COLON)
                .append(project.getHttpPort())
                .append(Selectors.BACKSLASH)
                .append(fileName)
                .toString();
    }

    private boolean containsInCache(String fileName) {
        return new File(new StringBuilder(folderService.pagesDir())
                .append(fileName)
                .toString()).exists();
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

            Element mainLink = parseElement(htmlBody.select(Selectors.TOPIC_ID).first());
            String id = Integer.toString(parseId(mainLink.attr("href").toString()));
            String selfLink = mainLink.attr("href");
            Element table = mainContent.select(Selectors.MAIN_TABLE).first();

            //999937765 = Залил
            if (table.getElementsByTag("tr").get(1).getElementsByTag("td").get(0).text().hashCode() != 999937765) {
                table.select("tr:nth-child(1)").after("" +
                        "<tr>" +
                        "<td class='header'>Залил</td>" +
                        "<td><b><a href='/browse/0/0/0/0' target='_blank'>Unknown</a></b></td>" +
                        "</tr>");
            }

            String topicSpecification = parseElement(table.select(Selectors.TOPIC_DETAILS).first()).toString();
            Element boundedContentElement = parseElement(table.select(Selectors.TOPIC_BOUNDED).first());
            Elements commentsContentElement = parseElement(mainContent.select(Selectors.TOPIC_COMMENTS));

            parseElement(table.select(Selectors.TOPIC_FILE).first()).remove();
            parseElement(table.select(Selectors.TOPIC_BOUNDED).first()).remove();
            parseElement(table.getElementsByTag(Selectors.TR).first()).remove();

            String distributionDetails = parseElement(table.getElementsByTag(Selectors.TR)).toString();
            String boundedContent = cardConverter.convert(boundedContentElement.select(Selectors.TOPIC_BOUNDED_DETAILS));
//                    Stream.of(boundedContentElement.select(Selectors.TOPIC_BOUNDED_DETAILS))
//                    .skip(1)
//                    .map(tr -> cardConverter.convert(tr))
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.joining());
            String commentsContent = commentConverter.convert(commentsContentElement);

            topicSpecification = topicSpecification.replace("hidehead", "hidehead plus");
            commentsContent = commentsContent.replace("hidehead", "hidehead plus");

            content = String.format(storage.get(MAIN, this::getText),
                    H1header,
                    selfLink,
                    topicSpecification,
                    distributionDetails,
                    boundedContent,
                    id,
                    commentsContent);

            if (Boolean.TRUE.toString().equals(project.value(TRIM_MODE))) {
                content = content.replaceAll(">\\s*<", "><");
            }

            writeText(content, getCacheFile(fileName));
        } catch (Exception e) {
            Log.e(TAG, "ERROR:", e);
        }
    }

    private File getCacheFile(String fileName) throws IOException {
        File storageDir = new File(folderService.pagesDir());
        if (Boolean.FALSE.equals(storageDir.exists())) {
            Log.e(TAG, "Directory not created for file: " + fileName);
        }

        File output = new File(storageDir.getAbsolutePath(), fileName);
        output.createNewFile();

        return output;
    }

    public interface Selectors extends Serializable {

        //        String PAGE_FOLDER = "/page";
//        String CSS_FOLDER = "/css";
//        String JS_FOLDER = "/js";
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

        //        String[] HEAD_JUNK = new String[]{
//                "head > script:nth-child(6)",
//                "head > script:nth-child(6)",
//                "head > script:nth-child(6)",
////                "head > link:nth-child(2)",
//        };
//        String[] BODY_JUNK = new String[]{
//                "#content > center:first-child",
//                "#details > tbody > tr:nth-child(1) > td:nth-child(1)",
//        };
        String COLON = ":";
        String BACKSLASH = File.separator;
        String PROTOCOL_FILE = "file://";
        String PROTOCOL_HTTP = "http://";
        String EXTENSION = ".html";
//        String EXTENSION_CSS = "" +
//                "<!--   /* ================= */ -->" +
//                "<meta name='viewport' content='width=device-width, initial-scale=1'>" +
////                "<link rel='stylesheet' href='http://fonts.googleapis.com/css?family=Raleway:400,300,600' type='text/css'>" +
//                "<link rel='stylesheet' href='../css/normalize.css' type='text/css'>" + //media='bogus'
//                "<link rel='stylesheet' href='../css/skeleton.css' type='text/css'>" +
//                "<link rel='stylesheet' href='../css/custom.css' type='text/css'>" +
//                "<script type='text/javascript' src='../js/jquery.min.js'></script>" + // async defer
//                "<script type='text/javascript' src='../js/jquery.cookie-min.js'></script>" +
//                "<script type='text/javascript' src='../js/functions.js'></script>";
//        String INDENT = "<br><br><br>";

    }

}
