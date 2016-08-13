package com.whitelaning.whitefragment.workspace.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whitelaning.whitefragment.R;
import com.whitelaning.whitefragment.factory.activity.BaseActivity;
import com.whitelaning.whitefragment.factory.view.MMWebView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends BaseActivity {

    @BindView(R.id.mBack)
    TextView mBack;
    @BindView(R.id.mTitle)
    TextView mTitle;
    @BindView(R.id.mMenu)
    TextView mMenu;
    @BindView(R.id.mActionBarBackground)
    RelativeLayout mActionBarBackground;
    @BindView(R.id.mWebView)
    MMWebView mWebView;

    private String indexUrl;
    private String indexContent;
    private int type;

    public static void toStartActivityForResult(Activity mActivity, int requestCode, String url) {
        toStartActivityForResult(mActivity, requestCode, url, null);
    }

    public static void toStartActivityForResult(Activity mActivity, int requestCode, String url, String content) {
        toStartActivityForResult(mActivity, requestCode, url, content, 0);
    }

    public static void toStartActivityForResult(Activity mActivity, int requestCode, String url, String content, int type) {
        Intent intent = new Intent(mActivity, WebViewActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("url", url);
        intent.putExtra("content", content);
        mActivity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        init();

        switch (type) {
            case 0:
                mWebView.loadUrl(indexUrl);
                break;
            case 1:
                mWebView.loadDataWithBaseURL(null, indexContent, "text/html", "UTF-8", null);
                break;
        }
    }

    private void init() {
        initIntent();
        initViewStatus();
        initWebView();
    }

    private void initWebView() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.equals(indexUrl)) {
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    private void initViewStatus() {
        mBack.setVisibility(View.VISIBLE);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mMenu.setVisibility(View.VISIBLE);
        mMenu.setText("OutUrl");
        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(indexUrl);
                intent.setData(content_url);
                startActivity(intent);
            }
        });

        mTitle.setText(indexUrl);
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        type = intent.getIntExtra("type", 0);
        indexContent = intent.getStringExtra("content");
        indexUrl = intent.getStringExtra("url");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
