package com.xtkj.libmyapp.http.callback;


import com.alibaba.fastjson.JSON;
import com.xtkj.libmyapp.util.LogUtils;

import java.lang.reflect.Type;

/**
 * Created by minyu on 16/6/28.
 */
public class JsonGenericsSerializator implements IGenericsSerializator {
    @Override
    public <T> T transform(String response, Type type) {
        return JSON.parseObject(response, type);
    }
}
