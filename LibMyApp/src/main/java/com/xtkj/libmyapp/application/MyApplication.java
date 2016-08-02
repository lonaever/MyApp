package com.xtkj.libmyapp.application;

import android.app.Application;

import com.xtkj.libmyapp.control.VersionControl;
import com.xtkj.libmyapp.util.FileUtil;
import com.xtkj.libmyapp.util.GalleryUtil;
import com.xtkj.libmyapp.util.MyLog;
import com.xtkj.libmyapp.util.ServerConfig;

import cn.finalteam.toolsfinal.logger.Logger;


public class MyApplication extends Application {

    public VersionControl vc;

	@Override
	public void onCreate() {
		super.onCreate();
		//初始化配置文件模块
		ServerConfig.initServerConfig(getApplicationContext());
		//初始化文件类
		FileUtil.init(getApplicationContext());
		//相册初始化
        GalleryUtil.init(getApplicationContext());
		//初始化日志类
		MyLog.initFileDir(FileUtil.getAppExtFilesPath());

        //初始化版本控制
        vc=new VersionControl(getApplicationContext());
	}
}