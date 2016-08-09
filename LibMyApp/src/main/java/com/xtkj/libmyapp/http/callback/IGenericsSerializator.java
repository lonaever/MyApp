package com.xtkj.libmyapp.http.callback;

import java.lang.reflect.Type;

/**
 * Created by JimGong on 2016/6/23.
 */
public interface IGenericsSerializator {
    <T> T transform(String response, Type type);
}
