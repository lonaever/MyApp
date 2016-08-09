package com.xtkj.libmyapp.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xtkj.libmyapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class InstallApkUtil {

	private Context theContext;
	private String thePackageName;

	// 安装的两种方式
	private String downUrl;
	private String apkName;
	private String savePath;
	private boolean isMust;

	// 用于下载
	private Dialog downDlg;
	private ProgressBar pb;
	private TextView tvTotalSize;
	private TextView tvDownSize;
	private int totalSize;// kb
	private int downSize;// kb
	private int downProgress;
	private boolean interceptFlag;

	// 安装控制
	private InstallHandler theHandler;

	/**
	 * 
	 * @param context
	 * @param packageName
	 * @param apkName
	 * @param url
	 * @param isMust
	 *            必须执行，不能取消
	 */
	public InstallApkUtil(Context context, String packageName, String apkName,
			String url, boolean isMust) {
		this.theContext = context;
		this.thePackageName = packageName;
		this.downUrl = url;
		this.apkName = apkName;
		this.isMust = isMust;
		this.savePath = FileUtil.getAppExtCachePathFile(apkName);
		this.theHandler = new InstallHandler();
	}

	public InstallApkUtil(Context context, String packageName, String apkName,
			String url) {
		this.theContext = context;
		this.thePackageName = packageName;
		this.downUrl = url;
		this.apkName = apkName;
		this.isMust = false;
		this.savePath = FileUtil.getAppExtCachePathFile(apkName);
		this.theHandler = new InstallHandler();
	}

	/**
	 * 判断apk是否已经安装
	 * 
	 * @return
	 */
	public boolean checkInstalled() {
		ArrayList<String> packageNameList = new ArrayList<String>();
		PackageManager pm = theContext.getPackageManager();
		List<PackageInfo> pkList = pm.getInstalledPackages(0);
		for (int i = 0; i < pkList.size(); i++) {
			PackageInfo pI = pkList.get(i);
				packageNameList.add(pI.packageName);
		}
		return packageNameList.contains(thePackageName);
	}

	/**
	 * 通过serice下载apk进行安装
	 * 
	 * @param msgTitle
	 *            代表对话框的标题
	 * @param msgContent
	 *            代表对话框的内容
	 * @param cls
	 *            代表service的class
	 */
	public void installFromService(final String msgTitle, String msgContent,
			final Class<?> cls) {
		Builder builder = new Builder(theContext);
		builder.setCancelable(!isMust);
		builder.setTitle(msgTitle);
		builder.setMessage(msgContent);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				startApkDownloadSerive(cls);
			}
		});
		if (this.isMust == false) {
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
		}
		builder.show();
	}

	/**
	 * 安装apk通过url地址下载
	 * 
	 * @param msgTitle
	 * @param msgContent
	 */
	public void installFromUrl(String msgTitle, String msgContent) {
		Builder builder = new Builder(theContext);
		builder.setCancelable(!isMust);
		builder.setTitle(msgTitle);
		builder.setMessage(msgContent);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				downloadApk();
			}
		});
		if (this.isMust == false) {
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
		}
		builder.show();
	}

	/**
	 * 安装apk通过asset文件夹中的
	 * 
	 * @param msgTitle
	 * @param msgContent
	 */
	public void installFromAsset(String msgTitle, String msgContent) {
		Builder builder = new Builder(theContext);
		builder.setCancelable(!isMust);
		builder.setTitle(msgTitle);
		builder.setMessage(msgContent);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				copyApkFromAsset();
				installApk(savePath);
			}
		});
		if (this.isMust == false) {
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
		}
		builder.show();
	}

	private void startApkDownloadSerive(Class<?> cls) {
		Intent t = new Intent(theContext, cls);
		t.putExtra("apkUrl", downUrl);
		t.putExtra("apkDir", FileUtil.getAppExtCachePath());
		t.putExtra("apkName",apkName);
		theContext.startService(t);
	}

	private void downloadApk() {
		Builder builder = new Builder(theContext);
		builder.setTitle("下载");
		View v = LayoutInflater.from(theContext).inflate(R.layout.dlg_download,
				null);
		pb = (ProgressBar) v.findViewById(R.id.progress);
		tvTotalSize = (TextView) v.findViewById(R.id.tv_totalsize);
		tvDownSize = (TextView) v.findViewById(R.id.tv_downsize);
		builder.setView(v);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		// builder.show();
		downDlg = builder.create();
		downDlg.setCanceledOnTouchOutside(false);
		downDlg.show();
		DownloadThread thread = new DownloadThread();
		thread.start();
	}

	private void copyApkFromAsset() {
		try {
			InputStream is = theContext.getAssets().open(apkName);
			byte[] buffer = new byte[1024];
			int count = 0;
			FileOutputStream fos = null;
			fos = new FileOutputStream(new File(savePath));
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 安装apk 要安装一个apk，必须将安装包保存在sd卡上
	 * 
	 * @param filepath
	 *            apk安装包的本地路径
	 */
	public void installApk(String filepath) {

		File apkfile = new File(filepath);
		if (apkfile.exists()) {
			Intent t=OpenFiles.getApkFileIntent(apkfile);
			theContext.startActivity(t);
		}
	}

	// 远程下载apk处理

	@SuppressLint("HandlerLeak")
	private final class InstallHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:// 下载更新进度
				pb.setProgress(downProgress);
				tvTotalSize.setText(totalSize + "K");
				tvDownSize.setText(downSize + "K");
				break;
			case 2:// 下载完成
				pb.setProgress(downProgress);
				downDlg.dismiss();
				installApk(savePath);
				break;
			}
		}

	}

	private class DownloadThread extends Thread {

		@Override
		public void run() {
			try {
				URL url = new URL(downUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File ApkFile = new File(savePath);
				FileOutputStream fos = new FileOutputStream(ApkFile);
				int count = 0;
				byte buf[] = new byte[1024];
				do {
					int numread = is.read(buf);
					count += numread;
					downProgress = (int) (((float) count / length) * 100);
					downSize = count / 1024;
					totalSize = length / 1024;
					// 更新进度
					theHandler.sendEmptyMessage(1);
					if (numread <= 0) {
						// 下载完成通知安装
						theHandler.sendEmptyMessage(2);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载.
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
