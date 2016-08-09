package com.xtkj.libmyapp.activity.web;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.xtkj.libmyapp.activity.BaseActivity;
import com.xtkj.libmyapp.model.WebContent;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.toolsfinal.StringUtils;

public abstract class BaseWebActivity extends BaseActivity {

    //参数
    public String webTitle;//用于显示在导航栏的标题
    //正文分三种情况,直接url,html正文,接口形式
    public String webUrl;
    public String webContent;
    public WebContent webContentObj;

    //记录html中包含的所有图片url
    public List<String> imgUrlList = new ArrayList<>();

    //ui
    public WebView webview;//子类中需要绑定web
    public ProgressBar progressBar;//如果xml中存在进度提示,可以使用

    public class ProgressChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                if (progressBar.getVisibility() == View.INVISIBLE)
                    progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //args
        if (getIntent().hasExtra("webTitle")) {
            webTitle = getIntent().getStringExtra("webTitle");
        }
        if (getIntent().hasExtra("webUrl")) {
            webUrl = getIntent().getStringExtra("webUrl");
        }
        if (getIntent().hasExtra("webContent")) {
            webContent = getIntent().getStringExtra("webContent");
        }
        if (getIntent().hasExtra("webContentObj")) {
            webContentObj = (WebContent) getIntent().getSerializableExtra("webContentObj");
        }
    }

    public WebChromeClient genWebChromeClient() {
        return new WebChromeClient();
    }

    public WebViewClient genWebViewClient() {
        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        };
    }

    public void setWebView() {
        WebSettings ws = webview.getSettings();
        //set webview
        ws.setUseWideViewPort(true);//自动调整页面适配屏幕
        ws.setLoadWithOverviewMode(true);
        ws.setJavaScriptEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setAppCacheEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);//设置缓存模式
        //支持输入
        webview.requestFocus();
        //client设置
        webview.setWebChromeClient(genWebChromeClient());
        webview.setWebViewClient(genWebViewClient());
    }

    public void refreshWebview() {
        if (!StringUtils.isEmpty(webTitle)) {
            setTitle(webTitle);
        }
        if (!StringUtils.isEmpty(webUrl)) {
            webview.loadUrl(webUrl);
        }
        if (!StringUtils.isEmpty(webContent)) {
            webview.loadData(webContent, "text/html", "UTF-8");
        }
        if (webContentObj != null) {
            if (webContentObj.genHtml(this)!=null) {
                webview.loadDataWithBaseURL("file:///android_asset/", webContentObj.genHtml(this), "text/html", "UTF-8", "");
            }
        }
    }

}
