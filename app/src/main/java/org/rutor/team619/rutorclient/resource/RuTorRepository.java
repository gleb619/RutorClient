package org.rutor.team619.rutorclient.resource;


import org.rutor.team619.rutorclient.model.DetailPage;
import org.rutor.team619.rutorclient.model.MainGroupedPage;
import org.rutor.team619.rutorclient.model.MainPlainPage;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by BORIS on 31.10.2015.
 */
public interface RuTorRepository {

    @GET("/")
    Observable<MainPlainPage> mainPagePlain();

    @GET("/new")
    Observable<MainGroupedPage> mainPageGrouped();

    @GET("/torrent/{id}/{name}")
    Observable<DetailPage> detailPage(@Path("id") String id, @Path("name") String name);

}
