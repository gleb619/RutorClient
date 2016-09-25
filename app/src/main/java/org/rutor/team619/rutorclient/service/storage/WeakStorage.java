package org.rutor.team619.rutorclient.service.storage;

import com.annimon.stream.function.Supplier;

import org.rutor.team619.rutorclient.util.Objects;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by BORIS on 24.09.2016.
 */
public class WeakStorage implements Storage<Byte, String> {

    private final Map<Byte, String> data = new WeakHashMap<>();

    @Override
    public String get(Code id, Supplier<String> text) {
        String value = getValue(id.val());
        if (Objects.isNull(value)) {
            value = set(id, text);
        }

        return value;
    }

    @Override
    public String set(Code id, Supplier<String> text) {
        setValue(id.val(), text.get());
        return getValue(id.val());
    }

    /* =================== */

    private String getValue(Byte id) {
        return data.get(id);
    }

    private void setValue(Byte id, String text) {
        data.put(id, text);
    }

}
