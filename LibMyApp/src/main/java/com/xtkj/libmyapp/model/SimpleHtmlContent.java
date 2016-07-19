package com.xtkj.libmyapp.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.phrase.Phrase;
import com.xtkj.libmyapp.global.Constant;
import com.xtkj.libmyapp.util.FileUtil;

import java.io.Serializable;

/**
 * Created by minyu on 16/7/15.
 */
public class SimpleHtmlContent implements WebContent, Serializable {
    public String content;

    public SimpleHtmlContent(){}

    public SimpleHtmlContent(String content) {
        this.content = content;
    }

    @Override
    public String genHtml(Context context) {
        String html = FileUtil.readAssetsByName(context, "simplecontent.html", "UTF-8");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String fontSize = sp.getString(Constant.SET_HTML_FONTSIZE, "3");
        return Phrase.from(html).put("fontSize", fontSize).put("content", content).toString();
    }
}
