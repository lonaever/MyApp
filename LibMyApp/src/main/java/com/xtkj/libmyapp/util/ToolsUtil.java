package com.xtkj.libmyapp.util;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.finalteam.toolsfinal.DateUtils;
import cn.finalteam.toolsfinal.StringUtils;

public class ToolsUtil {
	/**
	 * 显示对话框
	 *
	 * @param context
	 * @param msg
	 */
	public static void msgbox(Context context, String msg) {
		new AlertDialog.Builder((context)).setTitle("提示").setMessage(msg)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
					}
				}).show();
	}

	/**
	 * 无按钮的提示对话框，点击外部消失
	 *
	 * @param context
	 * @param msg
	 */
	public static void msgtip(Context context, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(msg);
		builder.setCancelable(true);
		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	/**
	 * 获取当前系统的版本号
	 *
	 * @param context
	 * @return 0：失败 其他：成功的版本号
	 */
	public static int currentVersionCode(Context context) {
		int ver = 0;
		try {
			ver = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return ver;
	}

	public static String currentVersion(Context context) {
		String ver = null;
		try {
			ver = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return ver;
	}

	/**
	 * 发送短信
	 *
	 * @param context
	 * @param to
	 * @param content
	 */
	public static void sendSms(Context context, String to, String content) {
		Uri uri = Uri.parse("smsto:" + to);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		it.putExtra("sms_body", content);
		context.startActivity(it);
	}

	/**
	 * 打电话
	 * @param context
	 * @param phone
	 */
	public static void tel(Context context, String phone) {
		Uri uri = Uri.parse("tel:" + phone);
		Intent it = new Intent(Intent.ACTION_CALL, uri);
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
			return;
		}
		context.startActivity(it);
	}

	/**
	 * 描述：手机号格式验证.
	 *
	 * @param str 指定的手机号码字符串
	 * @return 是否为手机号码格式:是为true，否则false
	 */
	public static Boolean isMobileNo(String str) {
		Boolean isMobileNo = false;
		try {
			Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Matcher m = p.matcher(str);
			isMobileNo = m.matches();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isMobileNo;
	}

	/**
	 * 描述：是否只是字母和数字.
	 *
	 * @param str 指定的字符串
	 * @return 是否只是字母和数字:是为true，否则false
	 */
	public static Boolean isNumberLetter(String str) {
		Boolean isNoLetter = false;
		String expr = "^[A-Za-z0-9]+$";
		if (str.matches(expr)) {
			isNoLetter = true;
		}
		return isNoLetter;
	}

	/**
	 * 描述：是否只是数字.
	 *
	 * @param str 指定的字符串
	 * @return 是否只是数字:是为true，否则false
	 */
	public static Boolean isNumber(String str) {
		Boolean isNumber = false;
		String expr = "^[0-9]+$";
		if (str.matches(expr)) {
			isNumber = true;
		}
		return isNumber;
	}

	/**
	 * 描述：是否是邮箱.
	 *
	 * @param str 指定的字符串
	 * @return 是否是邮箱:是为true，否则false
	 */
	public static Boolean isEmail(String str) {
		Boolean isEmail = false;
		String expr = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		if (str.matches(expr)) {
			isEmail = true;
		}
		return isEmail;
	}

	/**
	 * 描述：是否是中文.
	 *
	 * @param str 指定的字符串
	 * @return 是否是中文:是为true，否则false
	 */
	public static Boolean isChinese(String str) {
		Boolean isChinese = true;
		String chinese = "[\u0391-\uFFE5]";
		if (!StringUtils.isEmpty(str)) {
			//获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
			for (int i = 0; i < str.length(); i++) {
				//获取一个字符
				String temp = str.substring(i, i + 1);
				//判断是否为中文字符
				if (temp.matches(chinese)) {
				} else {
					isChinese = false;
				}
			}
		}
		return isChinese;
	}

	/**
	 * 描述：是否包含中文.
	 *
	 * @param str 指定的字符串
	 * @return 是否包含中文:是为true，否则false
	 */
	public static Boolean isContainChinese(String str) {
		Boolean isChinese = false;
		String chinese = "[\u0391-\uFFE5]";
		if (!StringUtils.isEmpty(str)) {
			//获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
			for (int i = 0; i < str.length(); i++) {
				//获取一个字符
				String temp = str.substring(i, i + 1);
				//判断是否为中文字符
				if (temp.matches(chinese)) {
					isChinese = true;
				} else {

				}
			}
		}
		return isChinese;
	}

    /**
     * 描述：ip地址转换为10进制数.
     *
     * @param ip the ip
     * @return the long
     */
    public static long ip2int(String ip) {
        ip = ip.replace(".", ",");
        String[] items = ip.split(",");
        return Long.valueOf(items[0]) << 24 | Long.valueOf(items[1]) << 16 | Long.valueOf(items[2]) << 8 | Long.valueOf(items[3]);
    }

    /**
     * 将int ip转字符串
     * @param a
     * @return
     */
    public static String int2ip(int a) {
        StringBuffer sb = new StringBuffer();
        int b = (a >> 0) & 0xff;
        sb.append(b + ".");
        b = (a >> 8) & 0xff;
        sb.append(b + ".");
        b = (a >> 16) & 0xff;
        sb.append(b + ".");
        b = (a >> 24) & 0xff;
        sb.append(b);
        return sb.toString();
    }
}
