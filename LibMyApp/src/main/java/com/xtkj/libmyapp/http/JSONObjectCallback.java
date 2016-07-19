package com.xtkj.libmyapp.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * Created by minyu on 16/7/4.
 */
public abstract class JSONObjectCallback extends Callback<JSONObject> {
    @Override
    public JSONObject parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        JSONObject json = JSON.parseObject(string);
        if (json == null) {
            throw new RuntimeException("Response is not json object data!");
        }
        return json;
    }
}
