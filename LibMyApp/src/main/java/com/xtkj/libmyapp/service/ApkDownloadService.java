package com.xtkj.libmyapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import com.xtkj.libmyapp.R;
import com.xtkj.libmyapp.http.FileCallback;
import com.xtkj.libmyapp.util.LogUtils;
import com.xtkj.libmyapp.util.OpenFiles;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;


public abstract class ApkDownloadService extends Service {

    //文件存储
    private String apkUrl;
    private String apkName;
    private String apkDir;

    //自动安装
    private boolean autoInstall;

    //通知栏
    private NotificationManager updateNotificationManager = null;
    private Builder updateBuilder;

    //需要实现的方法
    public abstract int iconResId();
    public abstract String titleText();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 获取传值
        apkUrl = intent.getStringExtra("apkUrl");
        apkDir = intent.getStringExtra("apkDir");
        apkName = intent.getStringExtra("apkName");
        autoInstall = intent.getBooleanExtra("autoInstall", false);

        this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        this.updateBuilder = new NotificationCompat.Builder(this);
        updateBuilder.setContentTitle(titleText()).setSmallIcon(iconResId());

        // 发出通知
        updateProgress(0);
        startDownload();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void startDownload() {
        LogUtils.d("startDownload " + apkUrl);
        OkHttpUtils.get().url(apkUrl).build().execute(new FileCallback(apkDir, apkName) {
            @Override
            public void onError(Call call, Exception e, int id) {
                updateBuilder.setContentText(getString(R.string.download_fail)).setProgress(0, 0, false);
                //设置点击通知栏回到应用
                PendingIntent pendingIntent = PendingIntent.getActivity(ApkDownloadService.this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
                updateBuilder.setContentIntent(pendingIntent);
                Notification noti = updateBuilder.build();
                noti.flags = android.app.Notification.FLAG_AUTO_CANCEL;
                updateNotificationManager.notify(0, noti);

                // 停止服务
                stopSelf();
            }

            @Override
            public void onResponse(File response, int id) {
                // 当下载完毕，更新通知栏
                updateBuilder.setContentText(getString(R.string.download_success)).setProgress(0, 0, false);

                //安装apk
                Intent installIntent = OpenFiles.getApkFileIntent(response);
                if (autoInstall) {
                    //自动安装apk
                    startActivity(installIntent);
                    updateNotificationManager.cancel(0);
                } else {
                    //点击安装apk
                    //好像是用来改文件权限的
                    String[] command = {"chmod", "777", response.toString()};
                    ProcessBuilder builder = new ProcessBuilder(command);
                    try {
                        builder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //设置点击通知栏也进行安装
                    PendingIntent pendingIntent = PendingIntent.getActivity(ApkDownloadService.this, 0, installIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    updateBuilder.setContentIntent(pendingIntent);
                    Notification noti = updateBuilder.build();
                    noti.flags = android.app.Notification.FLAG_AUTO_CANCEL;
                    updateNotificationManager.notify(0, noti);
                }
                //停止服务
                stopSelf();
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                //自定义显示进度
                updateProgress((int) (progress * 100));
            }
        });

    }

    private void updateProgress(int progress) {
        updateBuilder.setContentText(this.getString(R.string.download_progress, progress)).setProgress(100, progress, false);
        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
        updateBuilder.setContentIntent(pendingintent);
        updateNotificationManager.notify(0, updateBuilder.build());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("UpdateService Create");
    }

    @Override
    public void onDestroy() {
        LogUtils.d("UpdateService Destroy");
        super.onDestroy();
    }


}
