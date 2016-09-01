package com.xtkj.testnewsframe.page.http;

import android.Manifest;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.xtkj.libmyapp.http.OkHttpUtils;
import com.xtkj.libmyapp.http.callback.FileCallBack;
import com.xtkj.libmyapp.util.FileUtil;
import com.xtkj.libmyapp.util.LogUtils;
import com.xtkj.libmyapp.util.OpenFiles;
import com.xtkj.libmyapp.util.ToolsUtil;
import com.xtkj.testnewsframe.R;
import com.xtkj.testnewsframe.page.base.LActivity;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 测试三种情况
 * 1.请求http接口数据,展示新闻列表。
 * 2.下载一首mp3到应用扩展存储中,并播放。
 * 3.下载MP3到sd卡,并播放,需要提前申请权限。
 */
public class HttpTestActivity extends LActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_http_test);
        ButterKnife.bind(this);
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

    @OnClick(R.id.btn_news)
    public void onBtnNews(View view) {
        openIntent(ZrcRefreshNewsActivity.class, true);
    }

    @OnClick(R.id.btn_people)
    public void onBtnPeople(View view) {
        openIntent(UPTRRefreshPeopleActivity.class, true);
    }

    @OnClick(R.id.btn_recycle)
    public void onBtnRecycle(View view) {
        openIntent(RecycleViewActivity.class, true);
    }

    private void downloadToAppExt() {
        //生成App扩展存储路径
        if (!FileUtil.isAppExtOk()) {
            ToolsUtil.msgbox(context, "SD卡不可用");
            return;
        }
        //显示进度
        final SuperActivityToast superActivityToast = new SuperActivityToast(context, SuperToast.Type.PROGRESS_HORIZONTAL);
        superActivityToast.setText("下载");
        superActivityToast.setIndeterminate(true);
        superActivityToast.setMaxProgress(100);
        superActivityToast.setProgress(0);
        superActivityToast.show();
        LogUtils.d("filepath=" + FileUtil.getAppExtFilesPathFile("1.mp3"));
        //开始下载
        OkHttpUtils.get().url("http://cdn.y.baidu.com/11176a501471bc3596958834550a84db.mp3").build().execute(new FileCallBack(FileUtil.getAppExtFilesPath(), "1.mp3") {
            @Override
            public void onError(Call call, Exception e, int id) {
                superActivityToast.dismiss();
                showErrorTip("下载失败:" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(File response, int id) {
                superActivityToast.dismiss();
                OpenFiles.openFile(context, response);
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                superActivityToast.setProgress((int) (100 * progress));
            }
        });
    }

    private void downloadToSDExt() {
        //显示进度
        final SuperActivityToast superActivityToast = new SuperActivityToast(context, SuperToast.Type.PROGRESS_HORIZONTAL);
        superActivityToast.setText("下载");
        superActivityToast.setIndeterminate(true);
        superActivityToast.setMaxProgress(100);
        superActivityToast.setProgress(0);
        superActivityToast.show();
        LogUtils.d("filepath=" + FileUtil.getExtFilePathFile("1.mp3"));
        //开始下载
        OkHttpUtils.get().url("http://cdn.y.baidu.com/11176a501471bc3596958834550a84db.mp3").build().execute(new FileCallBack(FileUtil.getExtFilePath(), "1.mp3") {
            @Override
            public void onError(Call call, Exception e, int id) {
                superActivityToast.dismiss();
                showErrorTip("下载失败:" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(File response, int id) {
                superActivityToast.dismiss();
                OpenFiles.openFile(context, response);
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                superActivityToast.setProgress((int) (100 * progress));
            }
        });
    }

    @OnClick(R.id.btn_download)
    public void onBtnDownload(View view) {
        downloadToAppExt();
    }

    @OnClick(R.id.btn_download_sd)
    public void onBtnDownloadSd(View view) {
        if (!FileUtil.isCanUseSD()) {
            ToolsUtil.msgbox(context, "SD卡不可用");
            return;
        }
        MPermissions.requestPermissions(context, 501, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(501)
    public void requestSdcardSuccess() {
        downloadToSDExt();
    }

    @PermissionDenied(501)
    public void requestSdcardFailed() {
        ToolsUtil.msgbox(context, "没有读写扩展卡权限,请设置");
    }
}
