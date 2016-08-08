package com.xtkj.testnewsframe.page.loading;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.xtkj.libmyapp.activity.loading.MyLoadingActivity;
import com.xtkj.testnewsframe.LApplication;
import com.xtkj.testnewsframe.R;

public class LoadingActivity extends MyLoadingActivity {


    @Override
    public int loadingResId() {
        return 0;
    }

    @Override
    public ImageView.ScaleType scaleType() {
        return null;
    }

    @Override
    public int bgColorId() {
        return R.color.white;
    }

    @Override
    public View logoView() {
        ImageView iv=new ImageView(this);
        iv.setImageResource(R.mipmap.loading_logo);
        return iv;
    }

    @Override
    public int logoMarginTop() {
        return 0;
    }

    @Override
    public int logoMarginBottom() {
        return 20;
    }

    @Override
    public Intent nextIntent() {
        return null;
    }

    @Override
    public int taskCount() {
        return 0;
    }

    @Override
    public void doLoading() {

    }

    @Override
    public int delayTime() {
        return 2000;
    }

    @Override
    public void done() {
        LApplication.myApp().pdc.loadingFinish();
    }

    @Override
    public boolean needCheckNetwork() {
        return false;
    }
}
