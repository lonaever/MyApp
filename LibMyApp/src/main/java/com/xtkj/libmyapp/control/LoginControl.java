package com.xtkj.libmyapp.control;

import android.content.Context;

import com.xtkj.libmyapp.util.KV;

import java.security.GeneralSecurityException;

import cn.finalteam.toolsfinal.StringUtils;
import cn.finalteam.toolsfinal.coder.AESCoder;


public class LoginControl {

    public static String readLoginName(Context context) {
        KV kv = new KV(context, "loginInfo", 0);
        return kv.getString("loginName", "");
    }

    public static void saveLoginName(Context context, String loginName) {
        KV kv = new KV(context, "loginInfo", 0);
        kv.commit("loginName", loginName);
    }

    public static void clearLoginName(Context context,String loginName) {
        KV kv = new KV(context, "loginInfo", 0);
        kv.remove("loginName");
        kv.remove(loginName);
        kv.commit();
    }

    public static void savePasswordForName(Context context, String name, String pwd) {
        KV kv = new KV(context, "loginInfo", 0);
        try {
            kv.commit(name, AESCoder.encrypt("xtkj62", pwd));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public static String readPasswordForName(Context context, String name) {
        KV kv = new KV(context, "loginInfo", 0);
        String codepwd = kv.getString(name, null);
        if (!StringUtils.isEmpty(codepwd)) {
            try {
                return AESCoder.decrypt("xtkj62", codepwd);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
