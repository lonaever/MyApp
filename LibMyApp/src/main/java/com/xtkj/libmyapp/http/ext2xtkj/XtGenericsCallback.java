package com.xtkj.libmyapp.http.ext2xtkj;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xtkj.libmyapp.http.GenericsCallback;
import com.xtkj.libmyapp.http.JsonGenericsSerializator;
import com.xtkj.libmyapp.util.LogUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by minyu on 16/6/29.
 * <p/>
 * 适合xtkj目前使用的网络通讯约定
 * request使用p=json的方式
 * response使用{"ECODE":0,"DATA":json}的方式
 */
public abstract class XtGenericsCallback<T> extends GenericsCallback<T> {

    /**
     * 默认使用Json序列化数据
     */
    public XtGenericsCallback() {
        super(new JsonGenericsSerializator());
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException {
        String string = response.body().string();
        JSONObject json = JSON.parseObject(string);
        if (json == null) {
            throw new RuntimeException("Response is not json data!");
        } else {
            String data = json.getString("DATA");
            int ecode = json.getIntValue("ECODE");//该值的约定可变
            String msg = json.getString("MSG");
            if (ecode == 0) {
                //数据正确
                Type superclass = getClass().getGenericSuperclass();
                if (superclass instanceof Class) {
                    //表示未设置泛型T
                    return null;
                }
                ParameterizedType parameterized = (ParameterizedType) superclass;
                Type mType = parameterized.getActualTypeArguments()[0];
                if (mType == String.class) {
                    return (T) data;
                } else {
                    T bean = mGenericsSerializator.transform(data, mType);
                    return bean;
                }
            } else {
                //服务器错误
                throw new RuntimeException(msg + ",错误代码:" + ecode);
            }
        }
    }
}
