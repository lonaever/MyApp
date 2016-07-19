package com.xtkj.libmyapp.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * Created by minyu on 16/7/4.
 */
public abstract class JSONArrayCallback extends Callback<JSONArray> {
    @Override
    public JSONArray parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        JSONArray jsonArray = JSON.parseArray(string);
        if (jsonArray == null) {
            throw new RuntimeException("Response is not json array data!");
        }
        return jsonArray;
    }
}
