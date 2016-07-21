package com.xtkj.libmyapp.control;

import com.xtkj.libmyapp.util.ServerConfig;

/**
 * Created by minyu on 16/7/21.
 */
public class UrlControl {
    public static String baseUrl;

    public static String url(String key) {
        if (baseUrl == null) {
            baseUrl = ServerConfig.getValue("baseUrl");
        }
        String url = ServerConfig.getValue(key);
        if (url != null) {
            if (url.startsWith("http")) {
                return url;
            } else {
                return baseUrl + url;
            }
        }else {
            return baseUrl+key;
        }
    }
}
