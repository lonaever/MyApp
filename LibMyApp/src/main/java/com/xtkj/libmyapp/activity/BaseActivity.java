package com.xtkj.libmyapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.xtkj.libmyapp.R;
import com.zhy.http.okhttp.OkHttpUtils;

/**
 * Created by minyu on 15/7/17.
 */
public class BaseActivity extends AppCompatActivity {

    protected final String HTTP_TASK_TAG = "HttpTaskKey_" + hashCode();

    public BaseActivity context = this;
    public boolean isActive;

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(HTTP_TASK_TAG);
    }

    public void close(boolean ani) {
        finish();
        if (ani) {
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        }
    }

    public void openIntent(Class<?> cls, boolean ani) {
        Intent t = new Intent(this, cls);
        startActivity(t);
        if (ani) {
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
    }

    public void runAni(boolean isopen) {
        if (isopen) {
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        } else {
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        }
    }

    public int convertDipToPix(int dip) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return (int) (dip * dm.density);
    }

    public int getScreenHeight() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public int getScreenWidth() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public void showSuccTip(String tip) {
        if (isActive) {
            SuperActivityToast superActivityToast = new SuperActivityToast(this);
            superActivityToast.setText(tip);
            superActivityToast.setDuration(SuperToast.Duration.SHORT);
            superActivityToast.setBackground(SuperToast.Background.BLACK);
            superActivityToast.setIcon(R.drawable.tip_success, SuperToast.IconPosition.TOP);
            superActivityToast.setTextColor(Color.WHITE);
            superActivityToast.setTouchToDismiss(true);
            superActivityToast.show();
        }
    }

    public void showErrorTip(String tip) {
        if (isActive) {
            SuperActivityToast superActivityToast = new SuperActivityToast(this);
            superActivityToast.setText(tip);
            superActivityToast.setDuration(SuperToast.Duration.SHORT);
            superActivityToast.setBackground(SuperToast.Background.BLACK);
            superActivityToast.setIcon(R.drawable.tip_error, SuperToast.IconPosition.TOP);
            superActivityToast.setTextColor(Color.WHITE);
            superActivityToast.setTouchToDismiss(true);
            superActivityToast.show();
        }
    }

    public SuperActivityToast showProgressTip(String tip) {
        SuperActivityToast superActivityToast = new SuperActivityToast(this, SuperToast.Type.PROGRESS);
        superActivityToast.setText(tip);
        superActivityToast.setIndeterminate(true);
        superActivityToast.setProgressIndeterminate(true);
        superActivityToast.show();
        return superActivityToast;
    }
}
