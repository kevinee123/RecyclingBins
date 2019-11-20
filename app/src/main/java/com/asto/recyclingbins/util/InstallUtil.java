package com.asto.recyclingbins.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * apk安装工具类
 */
public class InstallUtil {

    Context context;

    public InstallUtil(Context context) {
        super();
        this.context = context;
    }

    /**
     * 下载到本地
     *
     * @param body 内容
     * @return 成功或者失败
     */
    public String writeResponseBodyToDisk(ResponseBody body) {
        try {
            //判断文件夹是否存在
            File files = new File(context.getFilesDir() + "/asto/");//跟目录一个文件夹
            if (!files.exists()) {
                //不存在就创建出来
                files.mkdirs();
            }
            //创建一个文件
            String fileName = context.getFilesDir() + "/asto/" + new Date().getTime() + ".apk";
            File futureStudioIconFile = new File(fileName);
            //初始化输入流
            InputStream inputStream = null;
            //初始化输出流
            OutputStream outputStream = null;

            try {
                //设置每次读写的字节
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                //请求返回的字节流
                inputStream = body.byteStream();
                //创建输出流
                outputStream = new FileOutputStream(futureStudioIconFile);
                //进行读取操作
                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }
                    //进行写入操作
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;

                }
                //刷新
                outputStream.flush();
                return fileName;
            } catch (IOException e) {
                return null;
            } finally {
                if (inputStream != null) {
                    //关闭输入流
                    inputStream.close();
                }

                if (outputStream != null) {
                    //关闭输出流
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * apk安装
     *
     * @param filePath
     */
    public void install(String filePath) {
        File file = new File(filePath);
        if (file.getName().endsWith(".apk")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 适配Android 7系统版本
                    File files = new File(context.getFilesDir() + "/asto/");
                    if (!files.exists()) {
                        files.mkdirs();
                    }
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                    uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);//通过FileProvider创建一个content类型的Uri
                } else {
                    uri = Uri.fromFile(file);
                }
                //重新给下载的文件设置权限
                //file为下载的文件
                String[] command = {"chmod", "777", file.getPath()};
                ProcessBuilder builder = new ProcessBuilder(command);
                try {
                    builder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                intent.setDataAndType(uri, "application/vnd.android.package-archive"); // 对应apk类型
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * apk安装
     *
     * @param file
     */
    public void install(File file) {
        if (file.getName().endsWith(".apk")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 适配Android 7系统版本
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                    uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);//通过FileProvider创建一个content类型的Uri
                } else {
                    uri = Uri.fromFile(file);
                }
                intent.setDataAndType(uri, "application/vnd.android.package-archive"); // 对应apk类型
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //重新给下载的文件设置权限
                //file为下载的文件
                String[] command = {"chmod", "777", file.getPath()};
                ProcessBuilder builder = new ProcessBuilder(command);
                try {
                    builder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * apk安装
     *
     */
    public void installAll(List<String> fileList) {
        for (String filePath : fileList) {
            File file = new File(filePath);
            if (file.getName().endsWith(".apk")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 适配Android 7系统版本
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                        uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);//通过FileProvider创建一个content类型的Uri
                    } else {
                        uri = Uri.fromFile(file);
                    }
                    //重新给下载的文件设置权限
                    //file为下载的文件
                    String[] command = {"chmod", "777", file.getPath()};
                    ProcessBuilder builder = new ProcessBuilder(command);
                    try {
                        builder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    intent.setDataAndType(uri, "application/vnd.android.package-archive"); // 对应apk类型
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
