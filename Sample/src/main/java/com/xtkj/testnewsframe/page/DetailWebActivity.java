package com.xtkj.testnewsframe.page;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebChromeClient;

import com.xtkj.libmyapp.activity.web.BaseWebActivity;
import com.xtkj.testnewsframe.R;

import butterknife.ButterKnife;

public class DetailWebActivity extends BaseWebActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_detail_web);
        webview = ButterKnife.findById(this, R.id.webview);
        progressBar = ButterKnife.findById(this, R.id.progress_bar);
        //init
        setWebView();
        //load
        refreshWebview();
    }

    @Override
    public WebChromeClient genWebChromeClient() {
        return new ProgressChromeClient();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                close(true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
