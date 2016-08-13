package com.whitelaning.whitefragment.workspace.main.network.service;

import com.whitelaning.whitefragment.workspace.main.network.UrlPath;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Zack White on 2016/8/7.
 */
public interface RequestServiceMzitu {

    @GET("{number}")
    Observable<String> getDetialData(@Path("number") String number, @Query("time") long time);

    @GET("page/{pageNumber}")
    Observable<String> getIndexMoreData(@Path("pageNumber") int pageNumber, @Query("time") long time);

    @GET(UrlPath.BaseUrlRequestServiceMzitu)
    Observable<String> getIndexData(@Query("time") long time);

    @GET("/tag/{path}")
    Observable<String> getTagData(@Path("path") String path, @Query("time") long time);

    @GET("/tag/{path}/page/{pageNumber}")
    Observable<String> getTagMoreData(@Path("path") String path, @Path("pageNumber") int pageNumber, @Query("time") long time);
}