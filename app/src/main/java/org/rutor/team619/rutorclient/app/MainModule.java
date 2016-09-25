package org.rutor.team619.rutorclient.app;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;

import org.rutor.team619.rutorclient.model.settings.Settings;
import org.rutor.team619.rutorclient.resource.RuTorRepository;
import org.rutor.team619.rutorclient.service.CommandsExecutor;
import org.rutor.team619.rutorclient.service.DeviceInformationService;
import org.rutor.team619.rutorclient.service.FolderService;
import org.rutor.team619.rutorclient.service.HttpServer;
import org.rutor.team619.rutorclient.service.ImageDownloader;
import org.rutor.team619.rutorclient.service.Interceptor.CacheInterceptor;
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
import org.rutor.team619.rutorclient.service.storage.Storage;
import org.rutor.team619.rutorclient.service.storage.WeakStorage;
import org.rutor.team619.rutorclient.util.AppUtil;
import org.rutor.team619.rutorclient.view.fragment.DetailPageFragment;
import org.rutor.team619.rutorclient.view.fragment.DeviceIdentificationFragment;
import org.rutor.team619.rutorclient.view.fragment.MainPageGroupedFragment;
import org.rutor.team619.rutorclient.view.fragment.MainPagePlainFragment;

import java.io.File;
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
import retrofit.converter.Converter;

/**
 * Created by BORIS on 14.08.2015.
 */
@Module(
        injects = {
                DeviceIdentificationFragment.class,
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
    public Settings provideProjectSettings() {
        return new Settings();
    }

    @Provides
    @Singleton
    public Endpoint provideEndpoint(Settings project) {
        return Endpoints.newFixedEndpoint(project.getUrl());
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(Settings project, Cache cache) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(project.getHttpConnectTimeout(), TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(project.getHttpReadTimeout(), TimeUnit.MILLISECONDS);
        okHttpClient.setCache(cache);
        okHttpClient.networkInterceptors().add(new CacheInterceptor(project));
        okHttpClient.interceptors().add(new CacheInterceptor(project));

        return okHttpClient;
    }

    @Provides
    @Singleton
    public Cache provideCache(FolderService folderService) {
        int mb = 20;
        return new Cache(new File(folderService.cacheDir()), 1024 * 1024 * mb);
    }

    @Provides
    @Singleton
    public RestAdapter provideRestAdapter(Endpoint endpoint, OkHttpClient client, Settings project, Converter converter, /* Don't remove this dep */HelperRegister helperRegister) {
        return new RestAdapter.Builder()
                .setConverter(converter)
                .setClient(new OkClient(client))
                .setEndpoint(endpoint)
                .setLogLevel(project.getLogLevel())
                .build();
    }

    @Provides
    @Singleton
    public Converter provideConverter(Settings project, Context context, FolderService folderService, Storage<Byte, String> storage) {
        return new RutorConverter(
                new MainPagePlainConverter(),
                new MainPageGroupedConverter(),
                new DetailPageConverter(project, context, folderService, storage,
                        new CardConverter(context, storage),
                        new CommentConverter(context, storage)));
    }

    @Provides
    @Singleton
    public RuTorRepository provideRuTorRepository(RestAdapter restAdapter) {
        return restAdapter.create(RuTorRepository.class);
    }

    @Provides
    @Singleton
    public HelperRegister provideHelperRegister(Context context, Settings project, FolderService folderService) {
        try {
            return new HelperRegister(Arrays.asList(
                    new FolderHelper(project, folderService),
                    new AssetsHelper(context.getAssets(), folderService)
            ))
                    .onWork();
        } catch (IOException e) {
            Log.e(TAG, "Failed to help the project", e);
        }

        return null;
    }

    @Provides
    @Singleton
    public WebServer provideServer(Settings project, FolderService folderService) {
        return new WebServer(project, folderService);
    }

    @Provides
    @Singleton
    public FolderService provideFolderService(Settings project) {
        return new FolderService(project);
    }

    @Provides
    public Storage<Byte, String> provideStorage() {
        return new WeakStorage();
    }

    @Provides
    @Singleton
    public CommandsExecutor provideCommandsExecutor() {
        return new CommandsExecutor();
    }

    @Provides
    @Singleton
    public DeviceInformationService provideDeviceInformationService(Context context) {
        return new DeviceInformationService(context);
    }

    @Provides
    public ImageDownloader provideImageDownloader(Settings project, Context context, CommandsExecutor commandsExecutor, DeviceInformationService deviceInformationService) {
        return new ImageDownloader(project.getUrl(), context.getResources(), commandsExecutor, deviceInformationService);
    }

    @Provides
    public Picasso providePicasso(Context context) {
        return Picasso.with(context);
    }

    @Provides
    public AppUtil provideAppUtil(Context context, Settings project) {
        return new AppUtil(context, project);
    }

}
