package org.rutor.team619.rutorclient.service.Interceptor;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

import org.rutor.team619.rutorclient.model.settings.Settings;

import java.io.IOException;

/**
 * Created by BORIS on 24.09.2016.
 */
public class CacheInterceptor implements Interceptor {

    private final Settings projectSettings;

    public CacheInterceptor(Settings project) {
        this.projectSettings = project;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .header("Cache-Control", String.format("max-age=%d, only-if-cached, max-stale=%d", 1200, 0))
                .build();
    }

}
