package com.llflib.cm.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.llflib.cm.R;
import com.llflib.cm.util.ILog;

public class BrowserAct extends ToolbarActivity {

    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cm_act_brower);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.cm_ic_left);
        setupViews();
        loadWeb(getIntent());
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onNewIntent(Intent intent) {
        loadWeb(intent);
    }

    protected void setupViews() {
        WebView webView = new WebView(getApplication());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, 0, 1);
        ((ViewGroup) findViewById(R.id.layout_root)).addView(webView, lp);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mWebView = webView;

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptCanOpenWindowsAutomatically(false);
        ws.setDatabaseEnabled(true);
        //        ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        webView.setWebViewClient(mWebClient);
        webView.setWebChromeClient(mWebChrome);
    }

    private void syncCookieIfNeed(Uri uri){
        ILog.i("schema:" + uri.getScheme() + ",author:" + uri.getAuthority() + ",Path:" + uri.getPath() +
                ",PathSegment:" + uri.getPathSegments());
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(uri.toString(), "abcdefg");
        cookieSyncManager.sync();
    }

    private void loadWeb(Intent intent) {
        //        "http://www.baidu.com"
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setIndeterminate(true);
        loadWebFromIntent(intent);
    }

    protected void loadWebFromIntent(Intent intent){
        Uri uri = intent.getData();
        syncCookieIfNeed(uri);
        ILog.i("web load Uri " + uri.toString());
        mWebView.loadUrl(uri.toString());
    }

    protected void loadWeb(String url){
        mWebView.loadUrl(url);
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    WebViewClient mWebClient = new WebViewClient() {
        @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
            loadWeb(url);
            return true;
        }

        @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgressBar.setIndeterminate(false);
            mProgressBar.setProgress(0);
            if(favicon != null){
                getSupportActionBar().setLogo(new BitmapDrawable(getResources(),favicon));
            }
        }

        @Override public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgressBar.postDelayed(new Runnable() {
                @Override public void run() {
                    mProgressBar.setVisibility(View.GONE);
                }
            }, 1000);
        }
    };

    WebChromeClient mWebChrome = new WebChromeClient() {
        @Override public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mProgressBar.setProgress(newProgress);
        }

        @Override public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            setTitle(title);
        }
    };


    @Override protected void onDestroy() {
        super.onDestroy();
        mWebView.removeAllViews();
        mWebView = null;
    }
}
