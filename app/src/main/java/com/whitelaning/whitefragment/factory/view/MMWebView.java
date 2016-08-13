package com.whitelaning.whitefragment.factory.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.ZoomButtonsController;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Whitelaning on 2016/5/19.
 */
public class MMWebView extends WebView {

    protected WeakReference<Activity> mActivity;
    protected WeakReference<Fragment> mFragment;

    protected WebViewClientListener mWebViewClientListener;

    protected WebViewClient mCustomWebViewClient;
    protected WebChromeClient mCustomWebChromeClient;

    protected static final Map<String, String> mHttpHeaders = new HashMap<String, String>();

    private ProgressBar mProgressbar;
    private int mProgressColor = Color.BLUE;

    public MMWebView(Context context) {
        super(context);
        init(context);
    }

    public MMWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MMWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void onResume() {
        if (Build.VERSION.SDK_INT >= 11) {
            super.onResume();
        }
        resumeTimers();
    }

    public void onPause() {
        pauseTimers();
        if (Build.VERSION.SDK_INT >= 11) {
            super.onPause();
        }
    }

    public void onDestroy() {
        try {
            ((ViewGroup) getParent()).removeView(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mHttpHeaders.clear();

        destroy();
    }

    protected void init(Context context) {

        if (context instanceof Activity) {
            mActivity = new WeakReference<Activity>((Activity) context);
        }

        initProgress(context);
        initWebSettings();
        initWebViewClient();
        initWebChromeClient();

        setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (mWebViewClientListener != null) {
                    mWebViewClientListener.onDownloadRequested(url, userAgent, contentDisposition, mimetype, contentLength);
                }
            }
        });
    }


    private void initProgress(Context context) {
        if (mProgressbar == null) {
            mProgressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
            mProgressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 12, 0, 0));
            mProgressbar.setMax(100);
            mProgressbar.setProgress(0);
            mProgressbar.setBackgroundColor(0xffcccccc);
            ClipDrawable d = new ClipDrawable(new ColorDrawable(mProgressColor), Gravity.START, ClipDrawable.HORIZONTAL);
            mProgressbar.setProgressDrawable(d);
        }

        if (indexOfChild(mProgressbar) == -1) {
            addView(mProgressbar);
        }
    }

    private void initWebChromeClient() {
        super.setWebChromeClient(new WebChromeClient() {

            // file upload callback (Android 2.2 (API level 8) -- Android 2.3 (API level 10)) (hidden method)
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, null);
            }

            // file upload callback (Android 3.0 (API level 11) -- Android 4.0 (API level 15)) (hidden method)
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooser(uploadMsg, acceptType, null);
            }

            // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (hidden method)
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileInput(uploadMsg, null);
            }

            // file upload callback (Android 5.0 (API level 21) -- current) (public method)
            @SuppressWarnings("all")
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                openFileInput(null, filePathCallback);
                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress == 100) {
                    mProgressbar.setVisibility(GONE);
                } else {

                    if (mProgressbar.getVisibility() == GONE) {
                        mProgressbar.setVisibility(VISIBLE);
                    }

                    mProgressbar.setProgress(newProgress);
                }

                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onProgressChanged(view, newProgress);
                } else {
                    super.onProgressChanged(view, newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onReceivedTitle(view, title);
                } else {
                    super.onReceivedTitle(view, title);
                }
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onReceivedIcon(view, icon);
                } else {
                    super.onReceivedIcon(view, icon);
                }
            }

            @Override
            public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onReceivedTouchIconUrl(view, url, precomposed);
                } else {
                    super.onReceivedTouchIconUrl(view, url, precomposed);
                }
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onShowCustomView(view, callback);
                } else {
                    super.onShowCustomView(view, callback);
                }
            }

            public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
                if (Build.VERSION.SDK_INT >= 14) {
                    if (mCustomWebChromeClient != null) {
                        mCustomWebChromeClient.onShowCustomView(view, requestedOrientation, callback);
                    } else {
                        super.onShowCustomView(view, requestedOrientation, callback);
                    }
                }
            }

            @Override
            public void onHideCustomView() {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onHideCustomView();
                } else {
                    super.onHideCustomView();
                }
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                if (mCustomWebChromeClient != null) {
                    return mCustomWebChromeClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
                } else {
                    return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
                }
            }

            @Override
            public void onRequestFocus(WebView view) {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onRequestFocus(view);
                } else {
                    super.onRequestFocus(view);
                }
            }

            @Override
            public void onCloseWindow(WebView window) {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onCloseWindow(window);
                } else {
                    super.onCloseWindow(window);
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                if (mCustomWebChromeClient != null) {
                    return mCustomWebChromeClient.onJsAlert(view, url, message, result);
                } else {
                    return super.onJsAlert(view, url, message, result);
                }
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                if (mCustomWebChromeClient != null) {
                    return mCustomWebChromeClient.onJsConfirm(view, url, message, result);
                } else {
                    return super.onJsConfirm(view, url, message, result);
                }
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                if (mCustomWebChromeClient != null) {
                    return mCustomWebChromeClient.onJsPrompt(view, url, message, defaultValue, result);
                } else {
                    return super.onJsPrompt(view, url, message, defaultValue, result);
                }
            }

            @Override
            public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
                if (mCustomWebChromeClient != null) {
                    return mCustomWebChromeClient.onJsBeforeUnload(view, url, message, result);
                } else {
                    return super.onJsBeforeUnload(view, url, message, result);
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                if (mGeolocationEnabled) {
                    callback.invoke(origin, true, false);
                } else {
                    if (mCustomWebChromeClient != null) {
                        mCustomWebChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
                    } else {
                        super.onGeolocationPermissionsShowPrompt(origin, callback);
                    }
                }
            }

            @Override
            public void onGeolocationPermissionsHidePrompt() {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onGeolocationPermissionsHidePrompt();
                } else {
                    super.onGeolocationPermissionsHidePrompt();
                }
            }

            public void onPermissionRequest(PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= 21) {
                    if (mCustomWebChromeClient != null) {
                        mCustomWebChromeClient.onPermissionRequest(request);
                    } else {
                        super.onPermissionRequest(request);
                    }
                }
            }

            public void onPermissionRequestCanceled(PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= 21) {
                    if (mCustomWebChromeClient != null) {
                        mCustomWebChromeClient.onPermissionRequestCanceled(request);
                    } else {
                        super.onPermissionRequestCanceled(request);
                    }
                }
            }

            @Override
            public boolean onJsTimeout() {
                if (mCustomWebChromeClient != null) {
                    return mCustomWebChromeClient.onJsTimeout();
                } else {
                    return super.onJsTimeout();
                }
            }

            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onConsoleMessage(message, lineNumber, sourceID);
                } else {
                    super.onConsoleMessage(message, lineNumber, sourceID);
                }
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if (mCustomWebChromeClient != null) {
                    return mCustomWebChromeClient.onConsoleMessage(consoleMessage);
                } else {
                    return super.onConsoleMessage(consoleMessage);
                }
            }

            @Override
            public Bitmap getDefaultVideoPoster() {
                if (mCustomWebChromeClient != null) {
                    return mCustomWebChromeClient.getDefaultVideoPoster();
                } else {
                    return super.getDefaultVideoPoster();
                }
            }

            @Override
            public View getVideoLoadingProgressView() {
                if (mCustomWebChromeClient != null) {
                    return mCustomWebChromeClient.getVideoLoadingProgressView();
                } else {
                    return super.getVideoLoadingProgressView();
                }
            }

            @Override
            public void getVisitedHistory(ValueCallback<String[]> callback) {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.getVisitedHistory(callback);
                } else {
                    super.getVisitedHistory(callback);
                }
            }

            @Override
            public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
                } else {
                    super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
                }
            }

            @Override
            public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
                if (mCustomWebChromeClient != null) {
                    mCustomWebChromeClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
                } else {
                    super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
                }
            }
        });
    }

    private void initWebViewClient() {
        super.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (mWebViewClientListener != null) {
                    mWebViewClientListener.onPageStarted(url, favicon);
                }

                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onPageStarted(view, url, favicon);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mWebViewClientListener != null) {
                    mWebViewClientListener.onPageFinished(url);
                }

                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onPageFinished(view, url);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                if (mWebViewClientListener != null) {
                    mWebViewClientListener.onPageError(errorCode, description, failingUrl);
                }

                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onReceivedError(view, errorCode, description, failingUrl);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                if (!isHostNameAllowed(url)) {
                    if (mWebViewClientListener != null) {
                        mWebViewClientListener.onExternalPageRequest(url);
                    }
                    return true;
                }

                if (mCustomWebViewClient != null) {
                    if (mCustomWebViewClient.shouldOverrideUrlLoading(view, url)) {
                        return true;
                    }
                }

                return super.shouldOverrideUrlLoading(view, url);
            }

            /**
             * 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
             * @param view
             * @param url
             */
            @Override
            public void onLoadResource(WebView view, String url) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onLoadResource(view, url);
                } else {
                    super.onLoadResource(view, url);
                }
            }

            /**
             * 主程序返回的数据为null，WebView会自行请求网络加载资源,否则使用主程序提供的数据。
             * 注意这个回调发生在非UI线程中。
             * 11引入 shouldInterceptRequest(WebView view, String url)
             * 21弃用并引入 shouldInterceptRequest(WebView view, WebResourceRequest request)
             * @param view
             * @param url
             * @return
             */
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 21) {
                    if (mCustomWebViewClient != null) {
                        return mCustomWebViewClient.shouldInterceptRequest(view, url);
                    } else {
                        return super.shouldInterceptRequest(view, url);
                    }
                } else {
                    return null;
                }
            }

            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= 21) {
                    if (mCustomWebViewClient != null) {
                        return mCustomWebViewClient.shouldInterceptRequest(view, request);
                    } else {
                        return super.shouldInterceptRequest(view, request);
                    }
                } else {
                    return null;
                }
            }

            /**
             * 应用程序重新请求网页数据
             * @param view
             * @param dontResend
             * @param resend
             */
            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onFormResubmission(view, dontResend, resend);
                } else {
                    super.onFormResubmission(view, dontResend, resend);
                }
            }

            /**
             * 更新历史记录。
             * 跳转后在这里clearHistory才能把跳转之前的url历史给清掉
             * @param view
             * @param url
             * @param isReload
             */
            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.doUpdateVisitedHistory(view, url, isReload);
                } else {
                    super.doUpdateVisitedHistory(view, url, isReload);
                }
            }

            /**
             * 访问有证书的网址。
             * handler.proceed()忽略证书
             * @param view
             * @param handler
             * @param error
             */
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onReceivedSslError(view, handler, error);
                } else {
                    handler.proceed();
                }
            }

            /**
             * 加载证书
             * @param view
             * @param request
             */
            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                if (Build.VERSION.SDK_INT >= 21) {
                    if (mCustomWebViewClient != null) {
                        mCustomWebViewClient.onReceivedClientCertRequest(view, request);
                    } else {
                        super.onReceivedClientCertRequest(view, request);
                    }
                }
            }

            /**
             * 返回信息授权请求
             * @param view
             * @param handler
             * @param host
             * @param realm
             */
            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
                } else {
                    super.onReceivedHttpAuthRequest(view, handler, host, realm);
                }
            }

            /**
             * 重写此方法才能够处理在浏览器中的按键事件。
             * @param view
             * @param event
             * @return
             */
            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                if (mCustomWebViewClient != null) {
                    return mCustomWebViewClient.shouldOverrideKeyEvent(view, event);
                } else {
                    return super.shouldOverrideKeyEvent(view, event);
                }
            }

            @Override
            public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onUnhandledKeyEvent(view, event);
                } else {
                    super.onUnhandledKeyEvent(view, event);
                }
            }

//            public void onUnhandledInputEvent(WebView view, InputEvent event) {
//                if (Build.VERSION.SDK_INT >= 21) {
//                    if (mCustomWebViewClient != null) {
//                        mCustomWebViewClient.onUnhandledInputEvent(view, event);
//                    } else {
//                        super.onUnhandledInputEvent(view, event);
//                    }
//                }
//            }

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onScaleChanged(view, oldScale, newScale);
                } else {
                    super.onScaleChanged(view, oldScale, newScale);
                }
            }

            public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
                if (Build.VERSION.SDK_INT >= 12) {
                    if (mCustomWebViewClient != null) {
                        mCustomWebViewClient.onReceivedLoginRequest(view, realm, account, args);
                    } else {
                        super.onReceivedLoginRequest(view, realm, account, args);
                    }
                }
            }
        });
    }

    private void initWebSettings() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setSaveEnabled(true);

        WebSettings webSettings = getSettings();
        webSettings.setAllowFileAccess(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        setAllowAccessFromFileUrls(webSettings, false);
        setMixedContentAllowed(webSettings, true);
        setThirdPartyCookiesEnabled(true);
        setZoomController(false);

        if (Build.VERSION.SDK_INT < 18) {
            /**
             * 提高渲染的优先级
             */
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        }
    }

    protected static void setAllowAccessFromFileUrls(final WebSettings webSettings, final boolean allowed) {

        if (Build.VERSION.SDK_INT >= 16) {
            /**
             * WebSettings.setAllowFileAccessFromFileURLs()
             * 通过此API可以设置是否允许通过file url(file:///etc/hosts)加载的Javascript读取其他的本地文件
             * 这个设置在JELLY_BEAN以前的版本默认是允许，在JELLY_BEAN及以后的版本中默认是禁止的。
             */
            webSettings.setAllowFileAccessFromFileURLs(allowed);

            /**
             * 通过此API可以设置是否允许通过file url加载的Javascript可以访问其他的源
             * 包括其他的文件和http,https等其他的源
             * 这个设置在JELLY_BEAN以前的版本默认是允许，在JELLY_BEAN及以后的版本中默认是禁止的。
             * 如果此设置是允许，则setAllowFileAccessFromFileURLs不起做用
             */
            webSettings.setAllowUniversalAccessFromFileURLs(allowed);
        }
    }

    public void setMixedContentAllowed(final boolean allowed) {
        setMixedContentAllowed(getSettings(), allowed);
    }

    /**
     * http和https的混合请求
     * 在API>=21的版本上面默认是关闭的
     * 在21以下就是默认开启的
     * 可能会导致了在高版本上面http请求不能正确跳转。
     *
     * @param webSettings
     * @param allowed
     */
    protected void setMixedContentAllowed(final WebSettings webSettings, final boolean allowed) {
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(allowed ? WebSettings.MIXED_CONTENT_ALWAYS_ALLOW : WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        }
    }

    /**
     * 在21之前的版本默认允许设置第三方的Cookie，在21之后的版本默认不允许设置。
     *
     * @param enabled
     */
    public void setThirdPartyCookiesEnabled(final boolean enabled) {
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, enabled);
        }
    }


    /**
     * 防拦截跳转
     * 判断当前url地址是否是允许访问的域名地址。
     *
     * @param url
     */
    private static final List<String> mHostNameList = new LinkedList<String>();

    protected boolean isHostNameAllowed(String url) {
        if (mHostNameList.size() == 0) {
            return true;
        }

        url = url.replace("http://", "");
        url = url.replace("https://", "");

        for (String hostname : mHostNameList) {
            if (url.startsWith(hostname)) {
                return true;
            }
        }

        return false;
    }

    public void addHostNameList(String hostname) {
        mHostNameList.add(hostname);
    }

    public void addHostNameList(Collection<? extends String> collection) {
        mHostNameList.addAll(collection);
    }

    public List<String> getHostNameList() {
        return mHostNameList;
    }

    public void removeHostName(String hostname) {
        mHostNameList.remove(hostname);
    }

    public void clearHostNameList() {
        mHostNameList.clear();
    }


    protected ValueCallback<Uri> mFileUploadCallbackFirst; //5.0之前使用
    protected ValueCallback<Uri[]> mFileUploadCallbackSecond;//5.0+使用
    protected String mUploadFileTypes = "*/*";
    protected static final int REQUEST_CODE_FILE_PICKER = 51426;
    protected int mRequestCodeFilePicker = REQUEST_CODE_FILE_PICKER;

    protected void openFileInput(final ValueCallback<Uri> fileUploadCallbackFirst, final ValueCallback<Uri[]> fileUploadCallbackSecond) {
        if (mFileUploadCallbackFirst != null) {
            mFileUploadCallbackFirst.onReceiveValue(null);
        }
        mFileUploadCallbackFirst = fileUploadCallbackFirst;

        if (mFileUploadCallbackSecond != null) {
            mFileUploadCallbackSecond.onReceiveValue(null);
        }
        mFileUploadCallbackSecond = fileUploadCallbackSecond;

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType(mUploadFileTypes);

        if (mFragment != null && mFragment.get() != null && Build.VERSION.SDK_INT >= 11) {
            mFragment.get().startActivityForResult(Intent.createChooser(i, "Choose a file"), mRequestCodeFilePicker);
        } else if (mActivity != null && mActivity.get() != null) {
            mActivity.get().startActivityForResult(Intent.createChooser(i, "Choose a file"), mRequestCodeFilePicker);
        }
    }

    public void setUploadFileTypes(final String mimeType) {
        mUploadFileTypes = mimeType;
    }

    protected boolean mGeolocationEnabled;

    public void setGeolocationEnabled(final boolean enabled) {
        if (enabled) {
            getSettings().setJavaScriptEnabled(true);
            getSettings().setGeolocationEnabled(true);
            setGeolocationDatabasePath();
        }

        mGeolocationEnabled = enabled;
    }

    protected void setGeolocationDatabasePath() {
        final Activity activity;

        if (mFragment != null && mFragment.get() != null && Build.VERSION.SDK_INT >= 11 && mFragment.get().getActivity() != null) {
            activity = mFragment.get().getActivity();
        } else if (mActivity != null && mActivity.get() != null) {
            activity = mActivity.get();
        } else {
            return;
        }

        getSettings().setGeolocationDatabasePath(activity.getFilesDir().getPath());
    }

    public void setWebViewClientListener(final Activity activity, final WebViewClientListener listener) {
        setWebViewClientListener(activity, listener, REQUEST_CODE_FILE_PICKER);
    }

    public void setWebViewClientListener(final Activity activity, final WebViewClientListener listener, final int requestCodeFilePicker) {
        if (activity != null) {
            mActivity = new WeakReference<Activity>(activity);
        } else {
            mActivity = null;
        }

        setWebViewClientListener(listener, requestCodeFilePicker);
    }

    public void setWebViewClientListener(final Fragment fragment, final WebViewClientListener listener) {
        setWebViewClientListener(fragment, listener, REQUEST_CODE_FILE_PICKER);
    }

    public void setWebViewClientListener(final Fragment fragment, final WebViewClientListener listener, final int requestCodeFilePicker) {
        if (fragment != null) {
            mFragment = new WeakReference<Fragment>(fragment);
        } else {
            mFragment = null;
        }

        setWebViewClientListener(listener, requestCodeFilePicker);
    }

    protected void setWebViewClientListener(final WebViewClientListener listener, final int requestCodeFilePicker) {
        mWebViewClientListener = listener;
        mRequestCodeFilePicker = requestCodeFilePicker;
    }

    @Override
    public void setWebViewClient(final WebViewClient client) {
        mCustomWebViewClient = client;
    }

    @Override
    public void setWebChromeClient(final WebChromeClient client) {
        mCustomWebChromeClient = client;
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == mRequestCodeFilePicker) {
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    if (mFileUploadCallbackFirst != null) {
                        mFileUploadCallbackFirst.onReceiveValue(intent.getData());
                        mFileUploadCallbackFirst = null;
                    } else if (mFileUploadCallbackSecond != null) {
                        Uri[] dataUris;
                        try {
                            dataUris = new Uri[]{Uri.parse(intent.getDataString())};
                        } catch (Exception e) {
                            dataUris = null;
                        }

                        mFileUploadCallbackSecond.onReceiveValue(dataUris);
                        mFileUploadCallbackSecond = null;
                    }
                }
            } else {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(null);
                    mFileUploadCallbackFirst = null;
                } else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond.onReceiveValue(null);
                    mFileUploadCallbackSecond = null;
                }
            }
        }
    }

    @Override
    public void loadUrl(final String url, Map<String, String> additionalHttpHeaders) {
        if (additionalHttpHeaders == null) {
            additionalHttpHeaders = mHttpHeaders;
        } else if (mHttpHeaders.size() > 0) {
            additionalHttpHeaders.putAll(mHttpHeaders);
        }

        super.loadUrl(url, additionalHttpHeaders);
    }

    @Override
    public void loadUrl(final String url) {
        if (mHttpHeaders.size() > 0) {
            super.loadUrl(url, mHttpHeaders);
        } else {
            super.loadUrl(url);
        }
    }

    public void loadUrl(String url, final boolean preventCaching) {
        if (preventCaching) {
            url = makeUrlUnique(url);
        }

        loadUrl(url);
    }

    public void loadUrl(String url, final boolean preventCaching, final Map<String, String> additionalHttpHeaders) {
        if (preventCaching) {
            url = makeUrlUnique(url);
        }

        loadUrl(url, additionalHttpHeaders);
    }

    protected static String makeUrlUnique(final String url) {
        StringBuilder unique = new StringBuilder();
        unique.append(url);

        if (url.contains("?")) {
            unique.append('&');
        } else {
            if (url.lastIndexOf('/') <= 7) {
                unique.append('/');
            }
            unique.append('?');
        }

        unique.append(System.currentTimeMillis());
        unique.append('=');
        unique.append(1);

        return unique.toString();
    }

    public void addHttpHeader(final String name, final String value) {
        mHttpHeaders.put(name, value);
    }

    public void removeHttpHeader(final String name) {
        mHttpHeaders.remove(name);
    }

    private void setZoomController(boolean tag) {
        // API version 11+ SDK提供了屏蔽缩放按钮的方法
        if (Build.VERSION.SDK_INT >= 11) {
            this.getSettings().setBuiltInZoomControls(true);
            this.getSettings().setDisplayZoomControls(tag);
        } else {
            // 如果是11- 的版本使用JAVA中的映射的办法
            getController(tag);
        }
    }

    private void getController(boolean tag) {
        try {
            Class webView = Class.forName("android.webkit.WebView");
            Method method = webView.getMethod("getZoomButtonsController");
            ZoomButtonsController zoomController = (ZoomButtonsController) method.invoke(this, new Object[]{});
            if (zoomController != null) {
                zoomController.setVisible(tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setmProgressColor(int mProgressColor) {
        this.mProgressColor = mProgressColor;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldL, int oldT) {
        LayoutParams lp = (LayoutParams) mProgressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        mProgressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldL, oldT);
    }


    public interface WebViewClientListener {
        void onPageStarted(String url, Bitmap favicon);

        void onPageFinished(String url);

        void onPageError(int errorCode, String description, String failingUrl);

        void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength);

        /**
         * 不是允许的访问地址
         *
         * @param url
         */
        void onExternalPageRequest(String url);
    }
}
