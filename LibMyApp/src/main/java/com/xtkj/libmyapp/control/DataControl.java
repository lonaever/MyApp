package com.xtkj.libmyapp.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.xtkj.libmyapp.util.ToolsUtil;

public abstract class DataControl {

	public Context appContext;// 应用上下文
    public int loadingFlag = 0;//基础数据加载完成设置为1

	public DataControl(Context context) {
		appContext = context;
	}

    /**
     * 基础数据，loading加载完成的标识
     */
    public void loadingFinish() {
        loadingFlag = 1;
    }

	/**
	 * 判断是否第一次启动本版本
	 * 
	 * @return
	 */
	public boolean checkFirstTimeRun() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(appContext);
		String key = "firstTimeRun_" + ToolsUtil.currentVersion(appContext);
		boolean ret = sp.getBoolean(key, true);
		// 同时将这个状态更改
		Editor e = sp.edit();
		e.putBoolean(key, false);
		e.commit();
		return ret;
	}

}
