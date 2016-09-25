package org.rutor.team619.rutorclient.service.storage;

import com.annimon.stream.function.Supplier;

/**
 * Created by BORIS on 24.09.2016.
 * <K> - key of storage
 * <V> - value of storage
 */
public interface Storage<K, V> {

    V get(Code id, Supplier<String> text);

    String set(Code id, Supplier<String> text);

    enum Code {

        MAIN(1),
        CARD(2),
        COMMENT_CARD(3);

        private final byte id;

        Code(int id) {
            this.id = (byte) id;
        }

        public byte val() {
            return id;
        }

    }

}
