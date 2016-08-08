package com.xtkj.testnewsframe;

import android.app.Application;

import com.xtkj.libmyapp.application.MyApplication;
import com.xtkj.testnewsframe.control.PublicDataControl;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by minyu on 16/7/7.
 */
public class LApplication extends MyApplication {
    public PublicDataControl pdc;

    @Override
    public void onCreate() {
        super.onCreate();
        //字体定义
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setFontAttrId(R.attr.fontPath)
                        .build());
        //init
        pdc = new PublicDataControl(getApplicationContext());

    }

    public static LApplication myApp() {
        return (LApplication) myapp;
    }
}
