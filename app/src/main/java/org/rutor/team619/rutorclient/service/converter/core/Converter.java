package org.rutor.team619.rutorclient.service.converter.core;

import org.rutor.team619.rutorclient.service.storage.Storage;

import java.io.Serializable;

/**
 * Created by BORIS on 07.08.2016.
 * <P> - supported type
 */
public interface Converter<PAGE, INPUT> extends Serializable {

    PAGE convert(INPUT input);

    Storage<Byte, String> storage();

    String getText();

    Class<PAGE> support();

}
