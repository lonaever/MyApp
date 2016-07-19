package com.xtkj.libmyapp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by minyu on 16/7/15.
 * 调整html中的一些标签工具
 * 主要用于处理图片标签
 */
public class HtmlUtil {
    /**
     * 遍历所有图片标签
     *
     * @param html
     */
    public static List<String> findImg(String html) {
        //init
        List<String> imgUrlList=new ArrayList<>();
        //find
        Pattern p = Pattern.compile("http://.*?jpg");
        Matcher m = p.matcher(html);
        while (m.find()) {
            String imgstr = m.group();
            imgUrlList.add(imgstr);
        }
        return imgUrlList;
    }

    /**
     * 注入图片点击事件
     *
     * @param content
     * @return
     */
    public static String addClickImg(String content) {
        return content.replace("<img", "<img onclick=\"alert(src)\"");
    }

    /**
     * 清除正文中所有img标签的样式
     *
     * @param html
     */
    public static String ajustImgStyle(String html) {
        Pattern p = Pattern.compile("<img.*?/>");
        Matcher m = p.matcher(html);
        while (m.find()) {
            String imghtml = m.group();
            String newimg = clearImgStyle(imghtml);
            html = html.replace(imghtml, newimg);
        }
        return html;
    }

    /**
     * 清除img标记中，所有样式，包含width，height，style
     *
     * @param imghtml
     * @return
     */
    private static String clearImgStyle(String imghtml) {
        Pattern p = Pattern.compile("width=\"\\d*?\"|height=\"\\d*?\"|style=\".*?\"");
        Matcher m = p.matcher(imghtml);
        String out = m.replaceAll("");
        return out;
    }
}
