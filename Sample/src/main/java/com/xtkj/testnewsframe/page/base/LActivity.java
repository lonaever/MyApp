package com.xtkj.testnewsframe.page.base;

import android.content.Intent;
import android.os.Bundle;

import com.xtkj.libmyapp.activity.BaseActivity;
import com.xtkj.libmyapp.control.VersionControl;
import com.xtkj.libmyapp.util.LogUtils;
import com.xtkj.testnewsframe.LApplication;
import com.xtkj.testnewsframe.control.PublicDataControl;
import com.xtkj.testnewsframe.page.home.HomeActivity;

/**
 * Created by minyu on 16/7/7.
 */
public class LActivity extends BaseActivity {
    public PublicDataControl pdc = LApplication.myApp().pdc;
    public VersionControl vc = LApplication.myApp().vc;

    //--控制重启----------------------------------------------------------------

    public boolean checkLoadingFlag() {
        return true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (checkLoadingFlag()) {
            if (pdc.loadingFlag != 1) {
                LogUtils.d("----- app restorestate,now need to goto loading -----");
                restart();
            }
        }
    }

    public void restart() {
        Intent t = new Intent(this, HomeActivity.class);
        t.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        t.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(t);
    }
}
