package com.example.chens.yidongzuoye;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by chens on 2017/2/22.
 */
public class DownloadService extends Service {
    private String downloadUrl;
    private NotificationManager notificationManager;
    private Notification notification;
    private String appName;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("info","开启服务");
        if (intent == null){
            notifyMessage("温馨提醒","安装包下载失败",0);
            stopSelf();
        }
        downloadUrl = intent.getStringExtra("apkUrl");
        appName = intent.getStringExtra("appName");
        downloadFile(downloadUrl);
        Log.i("info","url是："+downloadUrl+"  app名字是："+appName);
        return super.onStartCommand(intent, flags, startId);
    }

    private void notifyMessage(String title,String contentText,int progress){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(title);
        if (progress>0 && progress<100){
            builder.setProgress(100,progress,false);
        }else{
            builder.setProgress(0,0,false);
        }
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentText(contentText);
        if (progress >= 100){
            builder.setContentIntent(getInstallIntent());
        }
        notification = builder.build();
        notificationManager.notify(0,notification);
    }
    /**
     * 安装apk文件
     */
    private PendingIntent getInstallIntent(){
        File file = new File(Environment.DIRECTORY_DOWNLOADS+appName);   //看起来像默认下载地址？
        Intent intent = new Intent(Intent.ACTION_VIEW);  //这个Intent的用法不是很清楚
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://"+file.getAbsolutePath()),"application/vnd.android.package-archive");
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    /**
     * 下载apk文件
     * */
    private void downloadFile(String url){
        Log.i("info","下载文件");
        final File file = new File(Environment.DIRECTORY_DOWNLOADS+appName);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info","下载失败");
                notifyMessage("温馨提醒","文件下载失败",0);
                stopSelf();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("info","下载成功");
                InputStream inputStream = null;
                byte[] buf = new byte[1024];
                int len = 0;
                FileOutputStream fileOutputStream = null;
                long total = response.body().contentLength();
                Log.i("info","total:"+total);
                long current = 0;
                inputStream = response.body().byteStream();
                Log.i("info","2");
                fileOutputStream = new FileOutputStream(file);
                Log.i("info","3");
                int i = 1;
                while((len = inputStream.read(buf)) != -1){
                    Log.i("info","埋点："+ i++);
                    current += len;
                    fileOutputStream.write(buf,0,len);
                    //每10%刷新一次进度
                    if ((int)(current/total*100) % 10 == 0){
                        notifyMessage("温馨提醒","文件正在下载",(int) (current/total*100));
                    }
                }
                Log.i("info","4");
                fileOutputStream.flush();
                notifyMessage("温馨提醒","文件下载完成",100);
                stopSelf();
                Log.i("info","5");
            }
        });
    }
}
