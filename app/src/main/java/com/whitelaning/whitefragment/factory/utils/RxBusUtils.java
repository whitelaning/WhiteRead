package com.whitelaning.whitefragment.factory.utils;

import android.os.Bundle;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by Whitelaning on 2016/6/22.
 * Email: whitelaning@qq.com
 */
public class RxBusUtils {

    private final Subject<Object, Object>
            rxBus = new SerializedSubject<>(PublishSubject.create());

    private RxBusUtils() {

    }

    public static RxBusUtils getInstance() {
        return RxbusHolder.instance;
    }

    public static class RxbusHolder {
        private static final RxBusUtils instance = new RxBusUtils();
    }

    public void send(Bundle o) {
        rxBus.onNext(o);
    }

    public Observable<Object> toObserverable() {
        return rxBus;
    }
}