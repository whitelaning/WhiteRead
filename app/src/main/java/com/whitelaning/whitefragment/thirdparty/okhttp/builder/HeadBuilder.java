package com.whitelaning.whitefragment.thirdparty.okhttp.builder;

import com.whitelaning.whitefragment.thirdparty.okhttp.OkHttpUtils;
import com.whitelaning.whitefragment.thirdparty.okhttp.request.OtherRequest;
import com.whitelaning.whitefragment.thirdparty.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
