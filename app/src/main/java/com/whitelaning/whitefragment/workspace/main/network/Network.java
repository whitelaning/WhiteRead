package com.whitelaning.whitefragment.workspace.main.network;

import com.whitelaning.whitefragment.thirdparty.okhttp.OkHttpUtils;
import com.whitelaning.whitefragment.workspace.main.network.service.RequestServiceGankio;
import com.whitelaning.whitefragment.workspace.main.network.service.RequestServiceMzitu;
import com.whitelaning.whitefragment.workspace.main.network.service.RequestServiceTngou;
import com.whitelaning.whitefragment.workspace.main.network.service.RequestServiceU148;
import com.whitelaning.whitefragment.workspace.main.network.service.RequestServiceZhihu;

import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Zack White on 2016/7/29.
 */
public class Network {

    private static RequestServiceGankio serviceGankio; // Gank.io
    private static RequestServiceU148 serviceU148; // u148.net
    private static RequestServiceTngou serviceTngou;// tngou.net
    private static RequestServiceMzitu serviceMzitu;// mzitu.com
    private static RequestServiceZhihu serviceZhihu;// zhihu.com

    private static Converter.Factory scalarsConverterFactory = ScalarsConverterFactory.create();
    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();

    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();

    private volatile static Network INSTANCE;

    //构造方法私有
    private Network() {

    }

    public RequestServiceZhihu getServiceZhihuAPI() {
        return getServiceZhihuAPI(false);
    }

    /**
     * @param hasChangeOkHttpClientConfig 当改变了OkHttpClient的配置后，调用改方法重新实例化RequestService
     * @return
     */
    public RequestServiceZhihu getServiceZhihuAPI(boolean hasChangeOkHttpClientConfig) {
        if (serviceZhihu == null || hasChangeOkHttpClientConfig) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(OkHttpUtils.getInstance().getOkHttpClient())
                    .baseUrl(UrlPath.BaseUrlRequestServiceZhihu)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            serviceZhihu = retrofit.create(RequestServiceZhihu.class);
        }
        return serviceZhihu;
    }


    public RequestServiceMzitu getServiceMzitu() {
        return getServiceMzitu(false);
    }

    /**
     * @param hasChangeOkHttpClientConfig 当改变了OkHttpClient的配置后，调用改方法重新实例化RequestService
     * @return
     */
    public RequestServiceMzitu getServiceMzitu(boolean hasChangeOkHttpClientConfig) {
        if (serviceMzitu == null || hasChangeOkHttpClientConfig) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(UrlPath.BaseUrlRequestServiceMzitu)
                    .client(OkHttpUtils.getInstance().getOkHttpClient())
                    .addConverterFactory(scalarsConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            serviceMzitu = retrofit.create(RequestServiceMzitu.class);
        }
        return serviceMzitu;
    }

    public RequestServiceTngou getServiceTngouAPI() {
        return getServiceTngouAPI(false);
    }

    /**
     * @param hasChangeOkHttpClientConfig 当改变了OkHttpClient的配置后，调用改方法重新实例化RequestService
     * @return
     */
    public RequestServiceTngou getServiceTngouAPI(boolean hasChangeOkHttpClientConfig) {
        if (serviceTngou == null || hasChangeOkHttpClientConfig) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(OkHttpUtils.getInstance().getOkHttpClient())
                    .baseUrl(UrlPath.BaseUrlRequestServiceTngou)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            serviceTngou = retrofit.create(RequestServiceTngou.class);
        }
        return serviceTngou;
    }

    public RequestServiceGankio getServiceGankioAPI() {
        return getServiceGankioAPI(false);
    }

    /**
     * @param hasChangeOkHttpClientConfig 当改变了OkHttpClient的配置后，调用改方法重新实例化RequestService
     * @return
     */
    public RequestServiceGankio getServiceGankioAPI(boolean hasChangeOkHttpClientConfig) {
        if (serviceGankio == null || hasChangeOkHttpClientConfig) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(OkHttpUtils.getInstance().getOkHttpClient())
                    .baseUrl(UrlPath.BaseUrlRequestServiceGankio)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            serviceGankio = retrofit.create(RequestServiceGankio.class);
        }
        return serviceGankio;
    }

    public RequestServiceU148 getServiceU148API() {
        return getServiceU148API(false);
    }

    /**
     * @param hasChangeOkHttpClientConfig 当改变了OkHttpClient的配置后，调用改方法重新实例化RequestService
     * @return
     */
    public RequestServiceU148 getServiceU148API(boolean hasChangeOkHttpClientConfig) {
        if (serviceU148 == null || hasChangeOkHttpClientConfig) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(OkHttpUtils.getInstance().getOkHttpClient())
                    .baseUrl(UrlPath.BaseUrlRequestServiceU148)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            serviceU148 = retrofit.create(RequestServiceU148.class);
        }
        return serviceU148;
    }

    //获取单例
    public static Network getInstance() {
        if (INSTANCE == null) {
            synchronized (Network.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Network();
                }
            }
        }
        return INSTANCE;
    }
}
