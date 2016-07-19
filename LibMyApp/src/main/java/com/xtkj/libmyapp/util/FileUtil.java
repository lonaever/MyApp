package com.xtkj.libmyapp.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.ContactsContract;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.finalteam.toolsfinal.ApkUtils;
import cn.finalteam.toolsfinal.DateUtils;
import cn.finalteam.toolsfinal.DeviceUtils;
import cn.finalteam.toolsfinal.StringUtils;

/**
 * Created by minyu on 16/6/30.
 * 文件操作工具类
 * 主要的功能包含:
 * 1、沙盒中的文件处理
 * 2、外部存储的文件处理
 * <p>
 * android6.0以上,是需要手动请求外部存储权限的,这个需要注意。
 */
public class FileUtil {

    final static String FILES_FOLDER = "files";
    final static String CACHE_FOLDER = "cache";

    private static Context appContext;
    private static String rootFolder = "myapp";

    //通过Context生成下列变量
    private static String rootPath;
    private static String filesPath;
    private static String cachePath;

    public static void init(Context context) {
        appContext = context;
        String packageName = context.getPackageName();
        LogUtils.d("packname="+packageName);
        String n[] = packageName.split("\\.");
        LogUtils.d("n length="+n.length);
        if (n.length > 0) {
            rootFolder = n[n.length - 1];
        }
        LogUtils.i("rootfolder="+rootFolder);
    }

    //--私有存储目录,肯定是可用的,但是不宜放太多东西--

    public static String getAppFilesPath() {
        return appContext.getFilesDir().getAbsolutePath();
    }

    public static String getAppCachePath() {
        return appContext.getCacheDir().getAbsolutePath();
    }

    public static String getAppFilesPathFile(String filename) {
        return getAppFilesPath() + "/" + filename;
    }

    public static String getAppCachePathFile(String filename) {
        return getAppCachePath() + "/" + filename;
    }

    //--应用外部存储目录,如果SD卡可用,这个目录才会存在--

    public static boolean isAppExtOk() {
        if (isCanUseSD()) {
            return true;
        }
        return false;
    }

    public static String getAppExtFilesPath() {
        String path = appContext.getExternalFilesDir(null).getAbsolutePath();
        if (path != null) {
            //确保该路径是存在的
            //6.0以上是肯定会存在的。之前可能该路径未创建,需要手动创建
            makeDirs(path);
        }
        return path;
    }

    public static String getAppExtCachePath() {
        String path = appContext.getExternalCacheDir().getAbsolutePath();
        if (path != null) {
            makeDirs(path);
        }
        return path;
    }

    public static String getAppExtFilesPathFile(String filename) {
        return getAppExtFilesPath() + "/" + filename;
    }

    public static String getAppExtCachePathFile(String filename) {
        return getAppExtCachePath() + "/" + filename;
    }

    //--sd卡自定义存储路径,sd必须存在,android6.0以上需要有权限--

    public static String getExtRootPath() {
        if (rootPath==null) {
            rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + rootFolder;
            makeDirs(rootPath);
        }
        return rootPath;
    }

    public static String getExtFilePath() {
        if (filesPath==null) {
            filesPath = getExtRootPath() + "/" + FILES_FOLDER;
            makeDirs(filesPath);
        }
        return filesPath;
    }

    public static String getExtCachePath() {
        if (cachePath==null) {
            cachePath = getExtRootPath() + "/" + CACHE_FOLDER;
            makeDirs(cachePath);
        }
        return cachePath;
    }

    public static String getExtRootPathFile(String filename) {
        return getExtRootPath() + "/" + filename;
    }

    public static String getExtFilePathFile(String filename) {
        return getExtFilePath() + "/" + filename;
    }

    public static String getExtCachePathFile(String filename) {
        return getExtCachePath() + "/" + filename;
    }

    //--缓存方法--

    /**
     * 应用缓存大小
     * 包含三个部分:沙盒+appext+ext中的cache部分
     * @return M
     */
    public static String appCacheSize() {
        long size=pathSize(getAppCachePath())+pathSize(getAppExtCachePath()+pathSize(getExtCachePath()));
        return String.format("%.2fM",(float)(size/1024.0/1024.0));
    }

    /**
     * 清理应用程序缓存
     * 包含三个部分:沙盒+appext+ext中的cache部分
     */
    public static void clearCache() {
        deleteFile(getAppCachePath());
        deleteFile(getAppExtCachePath());
        deleteFile(getExtCachePath());
    }

    //--常用工具方法--

    /**
     * 创建目录
     * @param path
     */
    public static void makeDirs(String path) {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /**
     * 生成随机的jpg文件名
     *
     * @return
     */
    public static String genRandomPicName(String prefix) {
        String datestr= DateUtils.format(new Date(),"yyyyMMddHHmmss");
        int randomNum = (int) (Math.random() * 10000);
        String fourRandom = randomNum + "";
        int randLength = fourRandom.length();
        if (randLength < 4) {
            for (int i = 1; i <= 4 - randLength; i++)
                fourRandom = fourRandom + "0";
        }
        return prefix+datestr+fourRandom+".jpg";
    }

    /**
     * 描述：SD卡是否能用.
     *
     * @return true 可用,false不可用
     */
    public static boolean isCanUseSD() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断文件是否存在assets
     *
     * @param context
     * @param filename
     * @return
     */
    public static boolean isFileExistInAssets(Context context, String filename) {
        String files[] = new String[0];
        try {
            files = context.getAssets().list("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < files.length; i++) {
            if (filename.equals(files[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算sdcard上的剩余空间.
     *
     * @return the int
     */
    public static int freeSpaceOnSD() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat.getBlockSize()) / 1024 * 1024;
        return (int) sdFreeMB;
    }

    /**
     * 路径文件大小
     *
     * @return byte
     */
    public static long pathSize(String filepath) {
        if (filepath==null) {
            return 0;
        }
        File folder = new File(filepath);
        File[] listFiles = folder.listFiles();
        long size = 0;
        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isDirectory()) {
                size += pathSize(listFiles[i].getAbsolutePath());
            } else {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(listFiles[i]);
                    size += fis.available();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return size;
    }

    /**
     * 描述：从sd卡中的文件读取到byte[].
     *
     * @param path sd卡中文件路径
     * @return byte[]
     */
    public static byte[] getByteArrayFromSD(String path) {
        byte[] bytes = null;
        ByteArrayOutputStream out = null;
        try {
            File file = new File(path);
            //SD卡是否存在
            if (!isCanUseSD()) {
                return null;
            }
            //文件是否存在
            if (!file.exists()) {
                return null;
            }

            long fileSize = file.length();
            if (fileSize > Integer.MAX_VALUE) {
                return null;
            }

            FileInputStream in = new FileInputStream(path);
            out = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int size = 0;
            while ((size = in.read(buffer)) != -1) {
                out.write(buffer, 0, size);
            }
            in.close();
            bytes = out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
        }
        return bytes;
    }

    /**
     * 描述：将byte数组写入文件.
     *
     * @param path    the path
     * @param content the content
     */
    public static void writeByteArrayToSD(String path, byte[] content) {

        FileOutputStream fos = null;
        try {
            File file = new File(path);
            //SD卡是否存在
            if (!isCanUseSD()) {
                return;
            }
            //文件是否存在
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(path);
            fos.write(content);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 将bitmap写入文件.
     *
     * @param path
     * @param bitmap png
     */
    public static void writeBitmapToSD(String path, Bitmap bitmap, Bitmap.CompressFormat format) {

        FileOutputStream fos = null;
        try {
            File file = new File(path);
            //SD卡是否存在
            if (!isCanUseSD()) {
                return;
            }
            //文件是否存在
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(path);
            bitmap.compress(format, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
        }
    }


    /**
     * 拷贝Assets目录内容到sd卡目录
     *
     * @param context
     * @param assetDir "dir"
     * @param outDir   完整sd卡路径
     */
    public static void copyAssets2SD(Context context, String assetDir, String outDir) {
        String[] files;
        try {
            files = context.getAssets().list(assetDir);
            File outDirFile = new File(outDir);
            if (!outDirFile.exists()) {
                outDirFile.mkdirs();
            }

            for (int i = 0; i < files.length; i++) {
                String fileName = files[i];

                String[] filesChild = context.getAssets().list(fileName);
                if (filesChild != null && filesChild.length > 0) {
                    copyAssets2SD(context, fileName, outDir + "/" + fileName);
                } else {
                    InputStream in = null;
                    if (!StringUtils.isEmpty(assetDir)) {
                        in = context.getAssets().open(assetDir + "/" + fileName);
                    } else {
                        in = context.getAssets().open(fileName);
                    }
                    File outFile = new File(outDir + "/" + fileName);
                    if (outFile.exists()) {
                        outFile.delete();
                    }
                    outFile.createNewFile();
                    OutputStream out = new FileOutputStream(outFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    in.close();
                    out.close();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件.
     *
     * @return true, if successful
     */
    public static boolean deleteFile(String filepath) {
        try {
            if (filepath==null) {
                return false;
            }
            File file=new File(filepath);
            if (file.exists()) {
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFile(files[i].getAbsolutePath());
                    }
                } else {
                    file.delete();
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * 描述：读取Assets目录的文件内容.
     *
     * @param context  the context
     * @param name     the name
     * @param encoding the encoding
     * @return the string
     */
    public static String readAssetsByName(Context context, String name, String encoding) {
        String text = null;
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        try {
            inputReader = new InputStreamReader(context.getAssets().open(name));
            bufReader = new BufferedReader(inputReader);
            String line = null;
            StringBuffer buffer = new StringBuffer();
            while ((line = bufReader.readLine()) != null) {
                buffer.append(line);
            }
            text = new String(buffer.toString().getBytes(), encoding);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufReader != null) {
                    bufReader.close();
                }
                if (inputReader != null) {
                    inputReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return text;
    }

    /**
     * 描述：读取Raw目录的文件内容.
     *
     * @param context  the context
     * @param id       the id
     * @param encoding the encoding
     * @return the string
     */
    public static String readRawByName(Context context, int id, String encoding) {
        String text = null;
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        try {
            inputReader = new InputStreamReader(context.getResources().openRawResource(id));
            bufReader = new BufferedReader(inputReader);
            String line = null;
            StringBuffer buffer = new StringBuffer();
            while ((line = bufReader.readLine()) != null) {
                buffer.append(line);
            }
            text = new String(buffer.toString().getBytes(), encoding);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufReader != null) {
                    bufReader.close();
                }
                if (inputReader != null) {
                    inputReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return text;
    }

    /**
     * 读取文档
     *
     * @param filepath
     * @return
     */
    public static String readTextFile(String filepath) {
        StringBuffer buffer = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(filepath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = br.readLine();
            while (line != null) {
                buffer.append(line);
                buffer.append('\n');
                line = br.readLine();
            }
            br.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

}
