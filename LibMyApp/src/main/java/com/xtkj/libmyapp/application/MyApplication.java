package com.xtkj.libmyapp.application;

import android.app.Application;

import com.xtkj.libmyapp.control.VersionControl;
import com.xtkj.libmyapp.util.FileUtil;
import com.xtkj.libmyapp.util.GalleryHelper;
import com.xtkj.libmyapp.util.MyLog;
import com.xtkj.libmyapp.util.ServerConfig;


public class MyApplication extends Application {

    public static Application myapp;
    public VersionControl vc;

    @Override
    public void onCreate() {
        super.onCreate();

        if (myapp == null) {
            myapp = this;
        }
        //初始化配置文件模块
        ServerConfig.initServerConfig(getApplicationContext());
        //初始化文件类
        FileUtil.init(getApplicationContext());
        //相册初始化
        GalleryHelper.init(getApplicationContext());
        //初始化日志类
        MyLog.initFileDir(FileUtil.getAppExtFilesPath());

        //初始化版本控制
        vc = new VersionControl(getApplicationContext());
    }

}