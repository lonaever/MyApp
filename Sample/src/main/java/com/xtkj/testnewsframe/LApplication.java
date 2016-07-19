package com.xtkj.testnewsframe;

import com.xtkj.libmyapp.application.MyApplication;
import com.xtkj.testnewsframe.control.PublicDataControl;

/**
 * Created by minyu on 16/7/7.
 */
public class LApplication extends MyApplication {
    public static LApplication app;
    public PublicDataControl pdc;

    @Override
    public void onCreate() {
        super.onCreate();
        //init
        app = this;
        pdc = new PublicDataControl(getApplicationContext());

    }
}
