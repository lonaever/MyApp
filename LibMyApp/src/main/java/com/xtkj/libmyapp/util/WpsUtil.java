package com.xtkj.libmyapp.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.xtkj.libmyapp.http.FileCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;

import okhttp3.Call;

/**
 * Created by minyu on 15/7/17.
 */
public class WpsUtil {

    // 获取wps的intent
    public static Intent getWpsIntent(File file) {
        Intent i = new Intent();
        i.setClassName("cn.wps.moffice_eng", "cn.wps.moffice.documentmanager.PreStartActivity2");
        Uri uri = Uri.fromFile(file);
        i.setData(uri);
        Bundle bundle = new Bundle();
        bundle.putString("OpenMode", "ReadMode");
        bundle.putBoolean("ClearBuffer", true);
        bundle.putBoolean("ClearTrace", true);
        i.putExtras(bundle);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(Intent.ACTION_VIEW);
        return i;
    }

    /**
     * 通过wps打开文档
     * @param context
     * @param docUrl
     * @param saveFileName
     */
    public static void openDocInWps(final Context context, String docUrl, String saveFileName) {
        String downUrl=ServerConfig.getValue("wpsUrl");
        InstallApkUtil i=new InstallApkUtil(context,"cn.wps.moffice_eng","moffice_2052_wpscn.apk",downUrl);
        if(!i.checkInstalled()) {
            i.installFromUrl("安装阅读插件", "将联机下载安装阅读所需的插件");
            return;
        }
        File file=new File(FileUtil.getAppFilesPathFile(saveFileName));
        if (file.exists()) {
            Intent t = getWpsIntent(file);
            context.startActivity(t);
            return;
        }
        // 下载doc
        final ProgressDialog dlg = new ProgressDialog(context);
        dlg.setTitle("载入文档");
        dlg.setIndeterminate(false);
        dlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dlg.setMax(100);
        dlg.setProgress(0);
        dlg.show();

        OkHttpUtils.get().url(docUrl).build().execute(new FileCallback(FileUtil.getAppFilesPath(),saveFileName) {
            @Override
            public void onError(Call call, Exception e, int id) {
                dlg.dismiss();
                Toast.makeText(context, "文档下载出错!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(File response, int id) {
                dlg.dismiss();
                Intent t = getWpsIntent(response);
                context.startActivity(t);
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                dlg.setProgress((int) (progress * 100));
            }
        });

    }
}
