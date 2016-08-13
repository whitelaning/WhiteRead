package com.whitelaning.whitefragment.workspace.main.network.service;

import com.whitelaning.whitefragment.workspace.main.network.entity.ZhihuModel;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Whitelaning on 2016/8/9.
 * Email：whitelaning@qq.com
 */
public interface RequestServiceZhihu {
    @GET("latest")
    Observable<ZhihuModel> getLatestData();

    @GET("before/{data}")
    Observable<ZhihuModel> getBeforeData(@Path("data") String data);
}
