package com.whitelaning.whitefragment.workspace.main.network.service;

import com.whitelaning.whitefragment.workspace.main.network.entity.BaseResultEntity;
import com.whitelaning.whitefragment.workspace.main.network.entity.U148HomeModel;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Whitelaning on 2016/8/3.
 * Email：whitelaning@qq.com
 */
public interface RequestServiceU148 {
    @GET("json/0/{page}")
    Observable<BaseResultEntity<U148HomeModel>> getHomeData(@Path("page") int page);
    @GET("json/article/{id}")
    Observable<BaseResultEntity<U148HomeModel>> getArticleData(@Path("id") int id);
}
