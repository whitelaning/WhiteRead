package com.whitelaning.whitefragment.workspace.main.network.entity;

import com.whitelaning.whitefragment.thirdparty.logger.Logger;

import rx.Subscriber;

/**
 * Created by Zack White on 2016/7/29.
 */

public class BaseSubscriber<T> extends Subscriber<T> {

    @Override
    public void onStart() {

    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        Logger.e(e.getMessage());
    }

    @Override
    public void onNext(T response) {

    }
}
