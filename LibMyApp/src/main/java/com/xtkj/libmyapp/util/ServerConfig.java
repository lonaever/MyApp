package com.xtkj.libmyapp.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.io.IOException;
import java.util.Properties;

import cn.finalteam.toolsfinal.StringUtils;

/**
 * 如果需要指定server_config的文件名，需要在meta里面配置如：
 * <meta-data android:name="config_name" android:value="xxx.properties"/>
 */
public class ServerConfig {
	private static Properties props = new Properties();
	
	public static void initServerConfig(Context context) {
		try {
            String configname="";
            try {
                ApplicationInfo appInfo = context.getPackageManager()
                        .getApplicationInfo(context.getPackageName(),
                                PackageManager.GET_META_DATA);
                configname=appInfo.metaData.getString("config_name");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
			if (StringUtils.isEmpty(configname)) {
                configname="server_config.properties";
            }
			props.load(context.getResources().getAssets().open(configname));
		} catch (IOException e) {
			LogUtils.e("init server_config error:" + e.getLocalizedMessage());
		}
	}

	public static String getValue(String key) {
		return props.getProperty(key);
	}

	
}
