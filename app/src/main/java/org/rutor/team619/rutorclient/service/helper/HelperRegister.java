package org.rutor.team619.rutorclient.service.helper;

import org.rutor.team619.rutorclient.service.helper.core.Helper;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BORIS on 14.08.2016.
 */
public class HelperRegister implements Serializable {

    private final List<Helper> helpers = new ArrayList<>();

    public HelperRegister(List<Helper> helpers) {
        for (Helper helper : helpers) {
            this.helpers.add(helper);
        }
    }

    public HelperRegister onWork() throws IOException {
        for (Helper helper : helpers) {
            helper.onHelp();
        }

        return this;
    }

}
