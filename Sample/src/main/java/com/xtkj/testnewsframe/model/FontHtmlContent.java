package com.xtkj.testnewsframe.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.xtkj.libmyapp.global.Constant;
import com.xtkj.libmyapp.model.WebContent;
import com.xtkj.libmyapp.util.FileUtil;

import java.io.Serializable;

/**
 * Created by minyu on 16/8/4.
 */
public class FontHtmlContent implements WebContent, Serializable {

    public String content;

    public FontHtmlContent(){}

    public FontHtmlContent(String content) {
        this.content = content;
    }

    @Override
    public String genHtml(Context context) {
        String html = FileUtil.readAssetsByName(context, "fontcontent.html", "UTF-8");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String fontSize = sp.getString(Constant.SET_HTML_FONTSIZE, "3");
        return html.replace("{fontSize}",fontSize).replace("{content}",content);
    }
}
