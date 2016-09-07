package org.rutor.team619.rutorclient.app;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;

import org.rutor.team619.rutorclient.model.settings.ProjectSettings;
import org.rutor.team619.rutorclient.resource.RuTorRepository;
import org.rutor.team619.rutorclient.service.HttpServer;
import org.rutor.team619.rutorclient.service.ImageDownloader;
import org.rutor.team619.rutorclient.service.WebServer;
import org.rutor.team619.rutorclient.service.converter.CardConverter;
import org.rutor.team619.rutorclient.service.converter.CommentConverter;
import org.rutor.team619.rutorclient.service.converter.DetailPageConverter;
import org.rutor.team619.rutorclient.service.converter.MainPageGroupedConverter;
import org.rutor.team619.rutorclient.service.converter.MainPagePlainConverter;
import org.rutor.team619.rutorclient.service.converter.core.RutorConverter;
import org.rutor.team619.rutorclient.service.helper.AssetsHelper;
import org.rutor.team619.rutorclient.service.helper.FolderHelper;
import org.rutor.team619.rutorclient.service.helper.HelperRegister;
import org.rutor.team619.rutorclient.util.AppUtil;
import org.rutor.team619.rutorclient.view.fragment.DetailPageFragment;
import org.rutor.team619.rutorclient.view.fragment.MainPageGroupedFragment;
import org.rutor.team619.rutorclient.view.fragment.MainPagePlainFragment;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by BORIS on 14.08.2015.
 */
@Module(
        injects = {
                MainPageGroupedFragment.class,
                MainPagePlainFragment.class,
                DetailPageFragment.class,
                HttpServer.class,
        },
        complete = false,
        library = true
)
public class MainModule implements Serializable {

    private static final String TAG = AssetsHelper.class.getName() + ":";

    @Provides
    @Singleton
    public ProjectSettings provideProject() {
        return new ProjectSettings();
    }

    @Provides
    @Singleton
    public Endpoint provideEndpoint(ProjectSettings project) {
        return Endpoints.newFixedEndpoint(project.getUrl());
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(ProjectSettings project) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(project.getHttpConnectTimeout(), TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(project.getHttpReadTimeout(), TimeUnit.MILLISECONDS);

        return okHttpClient;
    }

    @Provides
    @Singleton
    public RestAdapter provideRestAdapter(Endpoint endpoint, OkHttpClient client, ProjectSettings project, Context context, HelperRegister helperRegister) {
        return new RestAdapter.Builder()
                .setConverter(new RutorConverter(
                        new MainPagePlainConverter(),
                        new MainPageGroupedConverter(),
                        new DetailPageConverter(project, context, new CardConverter(context), new CommentConverter(context))))
                .setClient(new OkClient(client))
                .setEndpoint(endpoint)
                .setLogLevel(project.getLogLevel())
                .build();
    }

    @Provides
    @Singleton
    public RuTorRepository provideRuTorRepository(RestAdapter restAdapter) {
        return restAdapter.create(RuTorRepository.class);
    }

    @Provides
    @Singleton
    public HelperRegister provideHelperRegister(Context context, ProjectSettings project) {
        try {
            return new HelperRegister(Arrays.asList(
                    new FolderHelper(project),
                    new AssetsHelper(context.getAssets(), project)
            ))
                    .onWork();
        } catch (IOException e) {
            Log.e(TAG, "Failed to help the project", e);
        }

        return null;
    }

    @Provides
    @Singleton
    public WebServer provideServer(ProjectSettings project) {
        return new WebServer(project);
    }

    @Provides
    public ImageDownloader provideImageDownloder(ProjectSettings project, Context context) {
        return new ImageDownloader(project.getUrl(), context.getResources());
    }

    @Provides
    public Picasso providePicasso(Context context) {
        return Picasso.with(context);
    }

    @Provides
    public AppUtil provideAppUtil(Context context, ProjectSettings project) {
        return new AppUtil(context, project);
    }

}
