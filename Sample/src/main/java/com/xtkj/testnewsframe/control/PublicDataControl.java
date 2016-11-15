package com.xtkj.testnewsframe.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xtkj.libmyapp.control.DataControl;
import com.xtkj.libmyapp.global.Constant;
import com.xtkj.libmyapp.http.OkHttpUtils;
import com.xtkj.libmyapp.http.callback.GenericsCallback;
import com.xtkj.libmyapp.http.callback.IGenericsSerializator;
import com.xtkj.libmyapp.http.callback.JsonGenericsSerializator;
import com.xtkj.libmyapp.http.ext2xtkj.XtkjGenericsCallback;
import com.xtkj.libmyapp.util.LogUtils;
import com.xtkj.libmyapp.util.ServerConfig;
import com.xtkj.testnewsframe.BuildConfig;
import com.xtkj.testnewsframe.model.NewsInfo;

import java.util.List;

import okhttp3.Response;

/**
 * Created by minyu on 16/7/7.
 */
public class PublicDataControl extends DataControl {
    public PublicDataControl(Context context) {
        super(context);
        this.initSet();

        //测试从meta中读取
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            String a=appInfo.metaData.getString("config_name");
            LogUtils.d("a="+a);
            LogUtils.d("read testmeta:"+ ServerConfig.getValue("testMeta"));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化默认设置
     */
    public void initSet() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(appContext);
        boolean haveinit = sp.getBoolean("initset", false);
        if (haveinit == false) {
            SharedPreferences.Editor et = sp.edit();
            et.putBoolean("initset", true);
            et.putBoolean(Constant.SET_AUTO_UPDATE, true);
            et.putBoolean(Constant.SET_AUTO_LOGIN, true);
            et.putBoolean(Constant.SET_PWD_REMEMBER, true);
            et.putString(Constant.SET_HTML_FONTSIZE, "3");
            et.commit();
        }
    }

    /**
     * 聚合,头条新闻数据解析
     */

    public abstract class JuheNewsCallback<T> extends GenericsCallback<T> {

        public JuheNewsCallback() {
            super(new JsonGenericsSerializator());
        }

        public JuheNewsCallback(IGenericsSerializator serializator) {
            super(serializator);
        }

        @Override
        public String validateReponse(Response response, int id) throws Exception {
            JSONObject json = JSON.parseObject(response.body().string());
            if (json == null) {
                return ("Response is not json data!");
            } else {
                int error_code = json.getIntValue("error_code");
                String msg = json.getString("reason");
                if (error_code == 0) {
                    JSONArray jsonArray = json.getJSONObject("result").getJSONArray("data");
                    validateData = jsonArray.toJSONString();
                    return null;
                } else {
                    return "error_code:" + error_code + " " + msg;
                }
            }
        }
    }

    /**
     * 聚合,新闻头条数据请求
     *
     * @param tag      需要用tag来控制,请求取消的情况
     * @param callback
     */
    public void requestJuheNewsList(Object tag, JuheNewsCallback callback) {
        OkHttpUtils.get().url("http://v.juhe.cn/toutiao/index").addParams("type", "top").addParams("key", "2a24e9e1f4b77a843fcf7a264c419480").tag(tag).build().execute(callback);
    }

    /**
     * 翔天的一个接口,返回一组人的列表
     * 这样使用就非常简单了
     *
     * @param tag
     * @param callback
     */
    public void requestPeopleList(Object tag, XtkjGenericsCallback callback) {
        OkHttpUtils.get().url("http://mobapi.ldck.org/data/cadresWikiIndexPic").tag(tag).build().execute(callback);
    }
}
