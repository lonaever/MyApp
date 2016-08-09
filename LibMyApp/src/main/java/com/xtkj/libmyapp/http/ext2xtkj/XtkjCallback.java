package com.xtkj.libmyapp.http.ext2xtkj;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xtkj.libmyapp.http.callback.Callback;

import okhttp3.Response;

/**
 * Created by minyu on 16/8/8.
 */
public abstract class XtkjCallback<T> extends Callback<T> {

    @Override
    public String validateReponse(Response response, int id) throws Exception {
        JSONObject json = JSON.parseObject(response.body().string());
        if (json == null) {
            return ("Response is not json data!");
        } else {
            String data = json.getString("DATA");
            int ecode = json.getIntValue("ECODE");//该值的约定可变
            String msg = json.getString("MSG");
            if (ecode == 0) {
                validateData = data;
                return null;
            } else {
                return "出错啦!code=" + ecode + "," + msg;
            }
        }
    }
}
