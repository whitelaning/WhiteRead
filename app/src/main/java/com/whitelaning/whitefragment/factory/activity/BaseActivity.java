package com.whitelaning.whitefragment.factory.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.whitelaning.whitefragment.R;
import com.whitelaning.whitefragment.factory.console.ActivityCollector;

import rx.Subscription;

/**
 * Created by Zack White on 2016/5/17.
 */
public class BaseActivity extends AppCompatActivity {

    protected Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ReplaceSystemControls();


        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);// Activity初始化时，加入AcitivityCollector的List中
    }

    public void ReplaceSystemControls() {
        LayoutInflaterCompat.setFactory(LayoutInflater.from(this), new LayoutInflaterFactory() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
//                /**
//                 * 可以在这里将系统类替换为自定义View,不要在这里做处理，到相应的类中复写该方法
//                 */
//                if (name.equals("ImageView")) {
//                    return new Button(context, attrs);
//                }
                return getDelegate().createView(parent, name, context, attrs);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unsubscribe();
        ActivityCollector.removeActivity(this);// Activity被销毁时，在AcitivityCollector的List中移除
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void unsubscribe() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}
