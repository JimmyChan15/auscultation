package com.example.chens.yidongzuoye.Utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.example.chens.yidongzuoye.data.QuestionBean;
import com.example.chens.yidongzuoye.data.UpdateBean;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by chens on 2017/2/16.
 */
public class Update {
    private static final String UPDATEURL = "";

    OkHttpClient client = new OkHttpClient();
    public UpdateBean checkUpdate(final Context context){
        final UpdateBean[] updateBean = {new UpdateBean()};
        Request request = new Request.Builder().get().url(UPDATEURL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(context,"网络连接错误",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                updateBean[0] = gson.fromJson(response.body().string(),UpdateBean.class);
            }
        });
        return updateBean[0];
    }

}
