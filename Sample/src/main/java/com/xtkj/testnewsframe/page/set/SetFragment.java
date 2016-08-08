package com.xtkj.testnewsframe.page.set;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.xtkj.libmyapp.control.VersionControl;
import com.xtkj.libmyapp.util.FileUtil;
import com.xtkj.libmyapp.util.InstallApkUtil;
import com.xtkj.libmyapp.util.LogUtils;
import com.xtkj.libmyapp.util.ToolsUtil;
import com.xtkj.testnewsframe.LApplication;
import com.xtkj.testnewsframe.R;
import com.xtkj.testnewsframe.UpdateService;
import com.xtkj.testnewsframe.control.PublicDataControl;

import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetFragment extends PreferenceFragment {


    //data
    PublicDataControl pdc = LApplication.myApp().pdc;

    Preference.OnPreferenceClickListener preferenceClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference pre) {
            if (pre.getKey().equals("set_update")) {
                checkUpdate();
            }
            if (pre.getKey().equals("set_clearcache")) {
                FileUtil.clearCache();
                pre.setSummary("缓存:0M");
                Toast.makeText(getActivity(), "清除成功", Toast.LENGTH_LONG).show();
            }

            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //从xml文件加载选项
        addPreferencesFromResource(R.xml.setting);

        // 设置关于
        Preference about = findPreference("set_about");
        String ver = ToolsUtil.currentVersion(getActivity());
        if (ver != null) {
            about.setSummary(ver);
        }

        //检查更新
        Preference setUpdate = findPreference("set_update");
        setUpdate.setOnPreferenceClickListener(preferenceClickListener);

        // 清除
        Preference setClear = findPreference("set_clearcache");
        setClear.setOnPreferenceClickListener(preferenceClickListener);
        setClear.setSummary("缓存:" + FileUtil.appCacheSize());

    }

    private void checkUpdate() {
        final ProgressDialog dlg = ProgressDialog.show(getActivity(), "检查更新", "请稍后...", true);
        final VersionControl vc = LApplication.myApp().vc;
        if (vc.isXtkjUpdateAvailable()) {
            vc.requestXtkjVersion(vc.new XtkjVersionCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    dlg.dismiss();
                    LogUtils.e("request xtkj update server fail", e);
                }

                @Override
                public void onResponse(Object response, int id) {
                    dlg.dismiss();
                    if (vc.checkVersion()>0) {
                        installUpdate();
                    }
                }
            });
        }
    }

    private void installUpdate() {
        final VersionControl vc = LApplication.myApp().vc;
        InstallApkUtil i = new InstallApkUtil(getActivity(), getActivity().getPackageName(),
                "app.apk", vc.apkUrl, vc.must == 1);//
        if (vc.must == 1) {
            i.installFromService("版本更新", "检测到新版本:" + vc.versionName + "\n" + vc.updateInfo
                    + "\n需要立即更新", UpdateService.class);
        } else {
            i.installFromService("版本更新", "检测到新版本:" + vc.versionName + "\n" + vc.updateInfo
                    + "\n是否立即更新", UpdateService.class);
        }
    }
}
