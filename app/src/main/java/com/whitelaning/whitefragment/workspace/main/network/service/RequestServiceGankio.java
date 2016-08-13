package com.whitelaning.whitefragment.workspace.main.network.service;

import com.whitelaning.whitefragment.workspace.main.network.entity.GankioHistoryModel;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Zack White on 2016/7/31.
 */
public interface RequestServiceGankio {
    @GET("api/data/Android/{number}/{page}")
    Observable<GankioHistoryModel> getHistoryContent(@Path("number") int number, @Path("page") int page);

    @GET("api/search/query/listview/category/休息视频/count/{number}/page/{page}")
    Observable<GankioHistoryModel> getVideoList(@Path("number") int number, @Path("page") int page);
}
