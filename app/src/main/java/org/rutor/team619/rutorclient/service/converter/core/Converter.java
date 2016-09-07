package org.rutor.team619.rutorclient.service.converter.core;

import java.io.Serializable;

/**
 * Created by BORIS on 07.08.2016.
 * <P> - supported type
 */
public interface Converter<PAGE, INPUT> extends Serializable {

    PAGE convert(INPUT input);

    Class<PAGE> support();

}
