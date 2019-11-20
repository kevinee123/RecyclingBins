package com.asto.recyclingbins.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.asto.recyclingbins.model.ApiModel;
import com.asto.recyclingbins.util.InstallUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;

import java.io.File;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class UpdateService extends Service {

    private ApiModel service;
    private String url;
    private InstallUtil downloadUtil;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStart(intent, startId);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://app.golden-navi.com/")
                //定义client类型
                .client(okHttpClient)
                //创建
                .build();
        service = retrofit.create(ApiModel.class);
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                url = bundle.getString("url");
            }
        }
        downloadAll_2(url);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public void downloadAll_2(String url) {

        GetRequest<File> request = OkGo.get(url)
//        .headers("key,""value")
//        .params("key","value")
                ;
        DownloadTask task = OkDownload.request(url, request)
                .folder(getFilesDir().toString())//下载的文件夹
//                .fileName(fileName)
                .register(new DownloadListener(url) {
                    @Override
                    public void onStart(Progress progress) {
//                        Logger.e("开始下载：" + url);
                    }

                    @Override
                    public void onProgress(Progress progress) {
                    }

                    @Override
                    public void onError(Progress progress) {
//                        Logger.e("下载失败：" + url);
                    }

                    @Override
                    public void onFinish(File file, Progress progress) {
//                        Logger.e("下载完成：" + url);

                        new InstallUtil(UpdateService.this.getApplication()).install(file.getAbsolutePath());

                    }

                    @Override
                    public void onRemove(Progress progress) {

                    }
                });
        task.save();
        OkDownload.getInstance().getThreadPool().setCorePoolSize(3);
        task.start();
    }
}
