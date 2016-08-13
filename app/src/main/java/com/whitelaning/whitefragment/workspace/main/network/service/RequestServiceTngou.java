package com.whitelaning.whitefragment.workspace.main.network.service;

import com.whitelaning.whitefragment.workspace.main.network.entity.TngouModel;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Zack White on 2016/8/3.
 */
public interface RequestServiceTngou {
    String ImageHear= "http://tnfs.tngou.net/image";

    @GET("tnfs/api/list")
    Observable<TngouModel> getListData(@Query("id") int id, @Query("page") int page, @Query("rows") int rows);
    @GET("tnfs/api/show")
    Observable<TngouModel> getDataDetail(@Query("id") int id);
}
