package com.xtkj.libmyapp.util;

import android.content.Context;

import java.io.IOException;
import java.util.Properties;

public class ServerConfig {
	private static Properties props = new Properties();
	
	public static void initServerConfig(Context context) {
		try {
			props.load(context.getResources().getAssets().open("server_config.properties"));
		} catch (IOException e) {
			LogUtils.e("init server_config error:" + e.getLocalizedMessage());
		}
	}

	public static String getValue(String key) {
		return props.getProperty(key);
	}

	
}
