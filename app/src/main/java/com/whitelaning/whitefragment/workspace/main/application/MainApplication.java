package com.whitelaning.whitefragment.workspace.main.application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.whitelaning.whitefragment.factory.application.BaseApplication;
import com.whitelaning.whitefragment.factory.other.fresco.FrescoConfigFactory;
import com.whitelaning.whitefragment.factory.utils.UtilsFolder;
import com.whitelaning.whitefragment.factory.utils.UtilsNetWork;
import com.whitelaning.whitefragment.thirdparty.okhttp.OkHttpUtils;
import com.whitelaning.whitefragment.thirdparty.okhttp.cookie.CookieJarImpl;
import com.whitelaning.whitefragment.thirdparty.okhttp.cookie.store.PersistentCookieStore;
import com.whitelaning.whitefragment.thirdparty.okhttp.https.HttpsUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Whitelaning on 2016/7/30.
 * Email：whitelaning@qq.com
 */
public class MainApplication extends BaseApplication {
    @Override
    protected void initData() {
        super.initData();
        initOkHttps();
        initFresco();
        Fresco.initialize(this);
    }

    private void initFresco() {
        Fresco.initialize(this, FrescoConfigFactory.getImagePipelineConfig(this));
    }


    private void initOkHttps() {

        File httpCacheDirectory = new File(UtilsFolder.getCacheDir(MainApplication.getContext()), "responses");
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null /*证书的inputStream*/, null, null);

        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!UtilsNetWork.isOnline(MainApplication.getContext())) {
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }

                Response response = chain.proceed(request);

                if (UtilsNetWork.isOnline(MainApplication.getContext())) {
                    int maxAge = 0 * 60; // 有网络时设置缓存超时时间0个小时
                    response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                            .build();
                } else {
                    int maxStale = 60 * 60 * 24 * 28; // 无网络时，设置超时为4周
                    response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .removeHeader("Pragma")
                            .build();
                }

                return response;
            }
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .cookieJar(cookieJar)
                .addInterceptor(interceptor)
                .cache(cache)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }
}
