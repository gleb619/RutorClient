package org.rutor.team619.rutorclient.service.converter;

import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.rutor.team619.rutorclient.model.Caption;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by BORIS on 07.08.2016.
 */
public class DefaultConverter implements Serializable {

    private static final String TAG = DefaultConverter.class.getName() + ":";

    protected static String readFromFile(String fileName, Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(fileName, Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }

        return returnString.toString();
    }

    public static Boolean escapeHtmlCheck(String input) {
        return escapeHtmlNonEmpty(input).length() > 0;
    }

    public static String escapeHtmlNonEmpty(String input) {
        return escapeHtml(input).replaceAll(Patterns.ALL_SPACES, "");
    }

    public static Boolean isNull(Object input) {
        return input == null;
    }

    public static Boolean isNotNull(Object input) {
        return input != null;
    }

    public static Boolean isNotNull(String input) {
        return (input != null && input.replaceAll(Patterns.ALL_SPACES, "").length() > 0);
    }

    public static Boolean isNotNull(Collection<?> input) {
        return (input != null && input.size() > 0);
    }

    public static Boolean isNotNullAndEquals(Collection<?> input, int size) {
        return (isNotNull(input) && input.size() == size);
    }

    public static Boolean isNotNullAndNotLess(Collection<?> input, int size) {
        return (isNotNull(input) && input.size() >= size);
    }

    public static Boolean isNotNullAndNotMore(Collection<?> input, int size) {
        return (isNotNull(input) && input.size() <= size);
    }

    public static String escapeHtml(String input) {
        if (input == null) {
            input = "";
        }
        input = Jsoup.parse(input).text();

        return input;
    }

    public static String parseString(String input) {
        if (input == null) {
            input = "";
        }
        input = input.replace(Patterns.HTML_SPACE, "");

        return input;
    }

    public static String parseStringClear(String input) {
        if (input == null) {
            input = "";
        }
        input = input.replace(Patterns.HTML_SPACE, "")
                .replaceAll("^" + Patterns.ALL_SPACES, "")
                .replaceAll(Patterns.ALL_SPACES + "$", "");

        return input;
    }

    public static Integer parseInteger(String input) {
        Integer output = 0;
        if (input == null) {
            input = "";
        }
        input = input.replaceAll(Patterns.ONLY_NUMBERS, "");
        try {
            output = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            Log.e(TAG, "ERROR: " + e.getMessage());
        }

        return output;
    }

    public static double parseDouble(String input) {
        double output = 0;
        if (input == null) {
            input = "";
        }
        input = input.replaceAll(Patterns.ONLY_NUMBERS, "");
        try {
            output = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            Log.e(TAG, "ERROR: " + e.getMessage());
        }

        return output;
    }

    public static Caption parseCaption(String input) {
        Caption output;
        if (input == null) {
            input = "";
        }

        try {
            String pattern = Patterns.CAPTION;
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(input);
            int index = 1;
            if (m.find()) {
                output = new Caption(
                        parseStringClear(m.group(index++))
                        , parseStringClear(m.group(index++))
                        , parseStringClear(m.group(index++))
                );
            } else {
                output = new Caption(parseStringClear(input));
            }
        } catch (Exception e) {
            Log.e(TAG, "ERROR: ", e);
            output = new Caption(input);
        }

        return output;
    }

    public static String parseTorrentName(String input) {
        String output = "";
        if (input == null) {
            input = "";
        }

        try {
            String pattern = Patterns.TORRENT_NAME;
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(input);
            if (m.find()) {
                output = m.group(3);
            } else {
                output = parseStringClear(input);
            }
        } catch (Exception e) {
            Log.e(TAG, "ERROR: ", e);
            output = input;
        }

        return output;
    }

    public static int parseId(String input) {
        int output = 0;
        if (input == null) {
            input = "";
        }

        Pattern pattern = Pattern.compile(Patterns.ID);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String idInString = matcher.group(1);
            if (idInString != null) {
                try {
                    output = new Integer(idInString.replaceAll(Patterns.ONLY_NUMBERS, ""));
                } catch (NumberFormatException e) {
                    output = -1;
                }
            }
        } else {
            throw new IllegalArgumentException(new StringBuilder("Id couldn't be parsed from input: ")
                    .append(input)
                    .toString());
        }

        return Math.abs(output);
    }

    public static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public static String compare(final String text) {
        return text.replaceAll(Patterns.ALL_SPACES, "").toLowerCase();
    }

    private interface Patterns extends Serializable {

        String ID = "[http|https]+:..d.+rutor\\.\\w+.download.(.*)";
        String CAPTION = "(.*)\\((.*)\\)(.*)";
        String TORRENT_NAME = "\\/(.*)\\/(.*)\\/(.*)";
        String ONLY_NUMBERS = "[^0-9.]";
        String HTML_SPACE = "&nbsp;";
        String ALL_SPACES = "\\s+";

    }

}
