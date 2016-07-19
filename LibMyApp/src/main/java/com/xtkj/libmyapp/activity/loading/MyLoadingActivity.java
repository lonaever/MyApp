package com.xtkj.libmyapp.activity.loading;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xtkj.libmyapp.activity.BaseActivity;
import com.xtkj.libmyapp.global.Constant;
import com.xtkj.libmyapp.util.AbImageUtil;

import java.io.File;
import java.util.List;

import cn.finalteam.toolsfinal.DeviceUtils;


public abstract class MyLoadingActivity extends BaseActivity {

    //data
    public boolean isActive;

    //ui
    RelativeLayout root;

    //loadcontrol
    private int taskCompleteCount;
    private int taskFailCount;

    Handler h = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            //如果有广告的处理
            if (msg.what == 88) {
                close();
                return false;
            } else if (msg.what == 3) {
                showAdv(msg.arg1);
                return false;
            }
            //加载过程的处理
            taskCompleteCount++;
            if (msg.what == 2) {
                taskFailCount++;
            }
            if (taskCompleteCount == taskCount() + 1) {
                if (isActive) {
                    if (taskFailCount > 0) {
                        showDlgError("初始化数据出错", "请检查网络设置后重试");
                    } else {
                        if (needShowAdv()) {
                            showAdv(0);
                        } else {
                            close();
                        }
                    }
                }
            }
            return false;
        }
    });

    private void close() {
        Intent t = nextIntent();
        if (t != null) {
            startActivity(t);
        }
        done();
        setResult(result());
        finish();
    }

    private void showAdv(int index) {
        //获取源
        List<String> picpaths = advPicPaths();
        if (index < picpaths.size() && index >= 0) {
            //把前面的广告图，现移除掉，再添加新的
            View lastiv = root.findViewWithTag(Integer.valueOf(index - 1));
            if (lastiv != null) {
                root.removeView(lastiv);
            }
            //add adv
            ImageView iv_adv = new ImageView(this);
            iv_adv.setTag(Integer.valueOf(index));
            String path = picpaths.get(index);
            Bitmap bm_adv = AbImageUtil.getScaleByOneSideBitmap(new File(path), getViewWidth(), 0);
            if (bm_adv != null) {
                iv_adv.setImageBitmap(bm_adv);
            }
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            root.addView(iv_adv, p);

            Message m = new Message();
            m.what = 3;
            m.arg1 = index + 1;
            h.sendMessageDelayed(m, 2000);
        } else {
            //1s后关闭
            h.sendEmptyMessage(88);
        }
    }

    private void showDlgError(String title, String msg) {
        AlertDialog dlg = new AlertDialog.Builder(this).setTitle(title).setMessage(msg).setPositiveButton("重试", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                retry();
            }
        }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                setResult(Constant.ResultExit);
                finish();
            }
        }).setCancelable(false).create();
        dlg.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.createUI();
        if (needCheckNetwork()) {
            if (!DeviceUtils.isOnline(this)) {
                showDlgError("无网络连接", "请检查网络设置后重试");
                return;
            }
        }
        this.doLoading();
        this.delay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
        if (taskCompleteCount == taskCount() + 1) {
            if (isActive) {
                if (taskFailCount > 0) {
                    showDlgError("初始化数据出错", "请检查网络设置后重试");
                } else {
                    if (needShowAdv()) {
                        showAdv(0);
                    } else {
                        close();
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(Constant.ResultExit);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void retry() {
        taskFailCount = 0;
        taskCompleteCount = 0;
        this.doLoading();
        this.delay();
    }

    public void asyncTaskComplete() {
        h.sendEmptyMessage(1);
    }

    public void asyncTaskFail() {
        h.sendEmptyMessage(2);
    }

    private void createUI() {
        root = new RelativeLayout(this);
        //set bg color
        if (bgColorId() > 0) {
            root.setBackgroundColor(ContextCompat.getColor(context,bgColorId()));
        }
        //add iv bg
        int bgid = loadingResId();
        if (bgid != 0) {
            ImageView iv = new ImageView(this);
            iv.setScaleType(scaleType());
            iv.setImageResource(loadingResId());
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            p.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            root.addView(iv, p);
        }

        //add logo
        View view_logo = logoView();
        if (view_logo != null) {
            RelativeLayout.LayoutParams p2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (logoMarginTop() > 0) {
                p2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                p2.topMargin = logoMarginTop();
            }
            if (logoMarginBottom() > 0) {
                p2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                p2.bottomMargin = logoMarginBottom();
            }
            if (logoMarginBottom() == 0 && logoMarginTop() == 0) {
                p2.addRule(RelativeLayout.CENTER_VERTICAL);
            }
            p2.addRule(RelativeLayout.CENTER_HORIZONTAL);
            root.addView(view_logo, p2);
        }

        setContentView(root);
    }

    private void delay() {
        h.sendEmptyMessageDelayed(1, delayTime());
    }

    public abstract int loadingResId();

    public abstract ScaleType scaleType();

    public abstract int bgColorId();

    public abstract Intent nextIntent();

    public abstract int taskCount();

    public abstract void doLoading();

    public abstract int delayTime();

    public abstract void done();

    public View logoView() {
        return null;
    }

    public int logoMarginTop() {
        return 40;
    }

    public int logoMarginBottom() {
        return 40;
    }

    public int result() {
        return Activity.RESULT_OK;
    }

    public boolean needCheckNetwork() {
        return true;
    }

    public boolean needShowAdv() {
        return false;
    }

    public List<String> advPicPaths() {
        return null;
    }
}
