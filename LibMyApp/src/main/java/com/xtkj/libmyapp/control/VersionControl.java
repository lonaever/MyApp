package com.xtkj.libmyapp.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xtkj.libmyapp.http.GenericsCallback;
import com.xtkj.libmyapp.http.JSONObjectCallback;
import com.xtkj.libmyapp.http.JsonGenericsSerializator;
import com.xtkj.libmyapp.util.LogUtils;
import com.xtkj.libmyapp.util.ServerConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import cn.finalteam.toolsfinal.StringUtils;
import okhttp3.Call;
import okhttp3.Response;

public class VersionControl {
    //参数
    private Context appContext;
    //翔天的升级路径
    private String updateUrl_xtkj;
    private String appId_xtkj;
    //Fir的版本路径
    private String appId_fir;
    private String appToken_fir;

    // 服务端通知的当前的最新版本
    public int versionCode;
    public String versionName;
    public String updateInfo;
    public String apkUrl;
    public int must;//0:可忽略 1:必须升级

    // 读取自己获取的版本
    public int currentVersionCode;
    public String currentVersionName;

    public VersionControl(Context context) {
        appContext = context;
        //翔天的配置参数
        updateUrl_xtkj=ServerConfig.getValue("updateUrl_xtkj");
        appId_xtkj=ServerConfig.getValue("appId_xtkj");

        //Fir的配置参数
        appId_fir = ServerConfig.getValue("appId_fir");
        appToken_fir=ServerConfig.getValue("appToken_fir");

        String appPackage = context.getPackageName();
        //读取自己
        try {
            currentVersionCode = context.getPackageManager().getPackageInfo(appPackage, 0).versionCode;
            currentVersionName = context.getPackageManager().getPackageInfo(appPackage, 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断版本是否有更新
     *
     * @return
     */
    public int checkVersion() {
        if (versionCode != 0) {
            return (versionCode - currentVersionCode);
        } else {
            return 0;
        }
    }

    /**
     * 检查应用是否可以自动更新
     *
     * @return
     */
    public boolean checkAutoUpdate() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(appContext);
        boolean auto = sp.getBoolean("set_autoupdate_enable", true);
        return auto;
    }

    /**
     * 判断是否需要自动更新了
     *
     * @return
     */
    public boolean needAutoUpdate() {
        if (checkAutoUpdate()) {
            if (checkVersion() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断翔天的升级版本服务是否配置好了
     * @return
     */
    public boolean isXtkjUpdateAvailable() {
        if (!StringUtils.isEmpty(updateUrl_xtkj)&&!StringUtils.isEmpty(appId_xtkj)) {
            return true;
        }
        return false;
    }

    /**
     * 判断Fir的服务是否配置好了
     * @return
     */
    public boolean isFirUpdateAvailable() {
        if (!StringUtils.isEmpty(appId_fir)&&!StringUtils.isEmpty(appToken_fir)) {
            return true;
        }
        return false;
    }

    /**
     * 用于翔天Apk更新服务接口,通用形式
     */
    public abstract class XtkjVersionCallback extends Callback {

        @Override
        public Object parseNetworkResponse(Response response, int id) throws Exception {
            JSONArray array = JSON.parseArray(response.body().string());
            if (array.size() > 0) {
                JSONObject json = array.getJSONObject(0);
                versionCode = json.getIntValue("VERSION_CODE");
                versionName = json.getString("VERSION_NAME");
                updateInfo = json.getString("UPDATE_DESCRIPTION");
                apkUrl = json.getString("FILE_PATH");
                must = json.getIntValue("MUST");
                LogUtils.d("xtkj server versioncode="+versionCode+",versionname="+versionName);
            }
            return null;
        }

    }

    public void requestXtkjVersion(XtkjVersionCallback callback) {
        JSONObject jp = new JSONObject();
        jp.put("UniquelyIdentifies", appId_xtkj);
        OkHttpUtils.get().url(updateUrl_xtkj).addParams("Type", "json").addParams("Parameters", jp.toString()).build().execute(callback);
    }

    /**
     * 用于Fir上的apk更新服务接口,通用形式
     */
    public abstract class FirVersionCallback extends Callback {

        @Override
        public Object parseNetworkResponse(Response response, int id) throws Exception {
            JSONObject json=JSON.parseObject(response.body().string());
            versionName=json.getString("versionShort");
            versionCode=json.getIntValue("build");
            updateInfo=json.getString("changelog");
            apkUrl=json.getString("direct_install_url");
            LogUtils.d("fir server versioncode="+versionCode+",versionname="+versionName);
            return null;
        }

    }

    public void requestFirVersion(final FirVersionCallback callback) {
        String url = "http://api.fir.im/apps/latest/" + appId_fir;
        OkHttpUtils.get().url(url).addParams("api_token", appToken_fir).build().execute(callback);
    }
}
