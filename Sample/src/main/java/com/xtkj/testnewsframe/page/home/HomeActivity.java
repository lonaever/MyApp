package com.xtkj.testnewsframe.page.home;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.xtkj.libmyapp.global.Constant;
import com.xtkj.libmyapp.util.ExitDoubleClick;
import com.xtkj.libmyapp.util.InstallApkUtil;
import com.xtkj.libmyapp.util.LogUtils;
import com.xtkj.testnewsframe.BuildConfig;
import com.xtkj.testnewsframe.R;
import com.xtkj.testnewsframe.UpdateService;
import com.xtkj.testnewsframe.page.base.LActivity;
import com.xtkj.testnewsframe.page.db.DBInsertActivity;
import com.xtkj.testnewsframe.page.drawer.DrawerActivity;
import com.xtkj.testnewsframe.page.font.FontDisplayActivity;
import com.xtkj.testnewsframe.page.gallery.TakePicActivity;
import com.xtkj.testnewsframe.page.http.HttpTestActivity;
import com.xtkj.testnewsframe.page.loading.LoadingActivity;
import com.xtkj.testnewsframe.page.pager.AutoPagerActivity;
import com.xtkj.testnewsframe.page.pager.ViewPagerActivity;
import com.xtkj.testnewsframe.page.set.SetActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.toolsfinal.StringUtils;
import okhttp3.Call;

/**
 * 应用,启动Activity
 * 主要包含几个功能:
 * 1、推送功能在这里实现
 * 2、自动检查更新在这里实现
 * <p/>
 * 我的启动顺序:
 * 无论如何都是先启动这个Acitivy,然后判断如果基础数据没有加载,则调用Loading界面进行加载。此后如果有登录需求也如此操作。
 */
public class HomeActivity extends LActivity {

    //ui
    @BindView(R.id.tv_title)
    TextView tv_title;

    //data
    boolean checkUpdateFlag;//update flag
    String notifyJsonStr;//收到的推送json

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        //设置推送栏自定义
        setPushStyleCustom();

        //检查是否包含推送的参数
        if (getIntent().hasExtra("notify")) {
            notifyJsonStr = getIntent().getStringExtra("notify");
        }

        //显示当前Flavor的ApplicationId
        tv_title.setText("测试:"+ BuildConfig.APPLICATION_ID+" "+BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set, menu);
        final MenuItem item = menu.findItem(R.id.search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        if (mSearchView==null) {
            LogUtils.d("oh,fuck");
        }else {
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_set:
                openIntent(SetActivity.class, false);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pdc.loadingFlag == 0) {
            Intent t = new Intent(this, LoadingActivity.class);
            startActivityForResult(t, Constant.LoadingRequestCode);
        } else {
            //如果有推送，先执行推送，否则检查更新
            if (StringUtils.isEmpty(notifyJsonStr)) {
                if (checkUpdateFlag == false) {
                    checkAutoUpdate();
                    checkUpdateFlag = true;
                }
            } else {
                openPushContent();
            }
            //刷新UI
            //code here...
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            setIntent(intent);
            //获取push args
            //检查是否包含推送的参数
            if (intent.hasExtra("notify")) {
                notifyJsonStr = getIntent().getStringExtra("notify");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.LoadingRequestCode) {
            if (resultCode == RESULT_OK) {
                //基础数据加载完成
                //在此可以执行一些更新操作
            } else if (resultCode == Constant.ResultExit) {
                finish();
            }
        } else if (requestCode == Constant.LoginRequestCode) {
            if (resultCode == RESULT_OK) {
                //当登录成功后
            } else if (resultCode == Constant.ResultExit) {
                finish();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ExitDoubleClick.getInstance(this).doDoubleClick(1500, "再按一次返回键退出");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 检查自动更新
     * 这里有两个函数可以使用:1.使用fir 2.使用翔天更新服务
     */
    private void checkAutoUpdate() {
        if (vc.checkAutoUpdate()) {
            //翔天服务
            if (vc.isXtkjUpdateAvailable()) {
                vc.requestXtkjVersion(vc.new XtkjVersionCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e("request xtkj update server fail", e);
                    }

                    @Override
                    public void onResponse(Object response, int id) {
                        if (vc.checkVersion() > 0) {
                            installUpdate();
                        }
                    }
                });
            }
            /*
            if (vc.isFirUpdateAvailable()) {
                vc.requestFirVersion(vc.new FirVersionCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtils.e("Request fir server fail", e);
                    }

                    @Override
                    public void onResponse(Object response, int id) {
                        if (isActive) {
                            if (vc.checkVersion() > 0) {
                                installUpdate();
                            }
                        }
                    }
                });
            }
            */
        }
    }

    /**
     * 下载并安装更新
     */
    private void installUpdate() {
        InstallApkUtil i = new InstallApkUtil(context, getPackageName(),
                "app.apk", vc.apkUrl, vc.must == 1);//
        if (vc.must == 1) {
            i.installFromService("版本更新", "检测到新版本:" + vc.versionName + "\n" + vc.updateInfo
                    + "\n需要立即更新", UpdateService.class);
        } else {
            i.installFromService("版本更新", "检测到新版本:" + vc.versionName + "\n" + vc.updateInfo
                    + "\n是否立即更新", UpdateService.class);
        }
    }

    @OnClick(R.id.btn_http)
    public void onBtnHttp(View view) {
        openIntent(HttpTestActivity.class, true);
    }

    @OnClick(R.id.btn_takepic)
    public void onBtnTakepic(View view) {
        openIntent(TakePicActivity.class, true);
    }

    @OnClick(R.id.btn_pager)
    public void onBtnPager(View view) {
        openIntent(ViewPagerActivity.class, true);
    }

    @OnClick(R.id.btn_font)
    public void onBtnFont(View view) {
        openIntent(FontDisplayActivity.class, true);
    }

    @OnClick(R.id.btn_db)
    public void onBtnDb(View view) {
        openIntent(DBInsertActivity.class, true);
    }

    @OnClick(R.id.btn_autopager)
    public void onBtnAutoPager(View view) {
        openIntent(AutoPagerActivity.class, true);
    }

    @OnClick(R.id.btn_notify)
    public void onBtnNotify(View view) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("oh,jiba").setContentText("why,what a fuck here! do u know?").setSmallIcon(R.mipmap.ic_test_nofity);

        //set pending
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, new Intent(this, AutoPagerActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);

        //get notfiy
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        manager.notify(1, notification);

    }

    @OnClick(R.id.btn_drawer)
    public void onBtnDrawer(View view) {
        openIntent(DrawerActivity.class, true);
    }

    /**
     * 执行推送打开的界面
     */
    private void openPushContent() {
//        if (AbStrUtil.isEmpty(notifyJsonStr)) {
//            return;
//        }
//        JSONObject customJson = JSON.parseObject(notifyJsonStr);
//        if (customJson.containsKey("u")) {
//            String linkurl = customJson.getString("u");
//            Intent t = new Intent(context, WebDetailActivity.class);
//            t.putExtra("webLinkUrl", linkurl);
//            startActivity(t);
//        }
//        notifyJsonStr = null;
    }


    /**
     * 设置通知栏样式 - 定义通知栏Layout
     */
    private void setPushStyleCustom() {
//        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(context, R.layout.customer_notitfication_layout, R.id.icon, R.id.title, R.id.text);
//        builder.layoutIconDrawable = R.drawable.ic_launcher;
//        JPushInterface.setPushNotificationBuilder(2, builder);
    }

}
