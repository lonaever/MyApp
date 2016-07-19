package com.xtkj.testnewsframe.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xtkj.libmyapp.control.DataControl;
import com.xtkj.libmyapp.global.Constant;
import com.xtkj.libmyapp.http.ext2xtkj.XtGenericsCallback;
import com.xtkj.testnewsframe.model.NewsInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.util.List;

import cn.finalteam.toolsfinal.StorageUtils;
import okhttp3.Response;

/**
 * Created by minyu on 16/7/7.
 */
public class PublicDataControl extends DataControl {
    public PublicDataControl(Context context) {
        super(context);
        this.initSet();
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
    public abstract class JuheNewsCallback extends Callback<List<NewsInfo>> {
        @Override
        public List<NewsInfo> parseNetworkResponse(Response response, int id) throws Exception {
            JSONObject json = JSON.parseObject(response.body().string());
            if (json != null) {
                int error_code = json.getIntValue("error_code");
                String msg = json.getString("reason");
                if (error_code == 0) {
                    JSONArray jsonArray = json.getJSONObject("result").getJSONArray("data");
                    return JSON.parseArray(jsonArray.toJSONString(), NewsInfo.class);
                } else {
                    throw new RuntimeException(msg);
                }
            }
            return null;
        }
    }

    /**
     * 聚合,新闻头条数据请求
     *
     * @param tag 需要用tag来控制,请求取消的情况
     * @param callback
     */
    public void requestJuheNewsList(Object tag, JuheNewsCallback callback) {
        OkHttpUtils.get().url("http://v.juhe.cn/toutiao/index").addParams("type", "top").addParams("key", "2a24e9e1f4b77a843fcf7a264c419480").tag(tag).build().execute(callback);
    }

    /**
     * 翔天的一个接口,返回一组人的列表
     * 这样使用就非常简单了
     * @param tag
     * @param callback
     */
    public void requestPeopleList(Object tag, XtGenericsCallback callback) {
        OkHttpUtils.get().url("http://mobapi.ldck.org/data/cadresWikiIndexPic").tag(tag).build().execute(callback);
    }
}