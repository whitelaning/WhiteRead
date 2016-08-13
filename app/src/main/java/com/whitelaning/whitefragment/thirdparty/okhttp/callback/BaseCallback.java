package com.whitelaning.whitefragment.thirdparty.okhttp.callback;

import com.google.gson.Gson;

import okhttp3.Response;

/**
 * Created by Whitelaning on 2016/7/30.
 * Email：whitelaning@qq.com
 */
public abstract class BaseCallback<T> extends Callback<T> {

    private Class<T> mClassOfT;

    public BaseCallback(Class<T> mClassOfT) {
        this.mClassOfT = mClassOfT;
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        return new Gson().fromJson(string, mClassOfT);
    }
}
