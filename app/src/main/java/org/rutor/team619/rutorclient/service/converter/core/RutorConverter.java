package org.rutor.team619.rutorclient.service.converter.core;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by BORIS on 31.10.2015.
 */
public class RutorConverter implements Converter {

    private static final String TAG = RutorConverter.class.getName() + ":";
    private final Map<Class<?>, org.rutor.team619.rutorclient.service.converter.core.Converter> converters = new HashMap<>();

    public RutorConverter() {

    }

    public RutorConverter(org.rutor.team619.rutorclient.service.converter.core.Converter... converterList) {
        this();
        for (org.rutor.team619.rutorclient.service.converter.core.Converter converter : converterList) {
            converters.put(converter.support(), converter);
        }
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        final Object[] result = new Object[1];

        Thread thread = new Thread(() -> {
            try {
                result[0] = work(body, type);
            } catch (ConversionException e) {
                Log.e(TAG, "ERROR:", e);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, "ERROR:", e);
        }

        return result[0];
    }

    private Object work(TypedInput body, Type type) throws ConversionException {
        Document document;
        try {
            document = Jsoup.parse(inputStreamTOString(body.in()));
        } catch (IOException e) {
            throw new ConversionException(e);
        }

        if (converters.containsKey(type)) {
            return converters.get(type).convert(document);
        }

        return document;
    }

    @Override
    public TypedOutput toBody(Object object) {
        try {
            return new TypedByteArray("text/html; charset=UTF-8", object.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    private String inputStreamTOString(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "ERROR:", e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return total.toString();
    }

}
