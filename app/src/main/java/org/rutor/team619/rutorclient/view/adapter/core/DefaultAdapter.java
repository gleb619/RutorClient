package org.rutor.team619.rutorclient.view.adapter.core;

import java.io.Serializable;

/**
 * Created by BORIS on 31.10.2015.
 */
public interface DefaultAdapter<T> extends Serializable {

    T getData();

    void setData(T data);

}
