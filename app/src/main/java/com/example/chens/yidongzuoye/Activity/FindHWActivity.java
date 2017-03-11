package com.example.chens.yidongzuoye.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chens.yidongzuoye.NormalRecyclerViewAdapter;
import com.example.chens.yidongzuoye.R;
import com.example.chens.yidongzuoye.data.PracticeBean;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by chens on 2017/2/23.
 */
public class FindHWActivity extends Activity {
    private final int FAILED = 0,SUCCESS = 1;
    private RecyclerView recyclerView;
    private ArrayList<PracticeBean> practiceBeanList = new ArrayList<>();
    private String practice_type = "4";

    private NormalRecyclerViewAdapter adapter;
    private ProgressBar progressBar;
    private TextView textView;
    private RelativeLayout rl_findHW;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case FAILED:
                    progressBar.setVisibility(View.GONE);
                    textView.setText("加载失败了");
                    break;
                case SUCCESS:
                    //刷新adapter
                    adapter.setPracticeBeanList(practiceBeanList);
                    adapter.setOnItemClickListener(new NormalRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View view, String data) {
                            Log.d("info", "onClick--> Tag = " +data);
                            Intent intent = new Intent();
                            intent.putExtra("practice_id",practiceBeanList.get(Integer.parseInt(data)).getPractice_id());
                            intent.setClass(FindHWActivity.this,QuestionActivity.class);
                            startActivity(intent);
                        }
                    });
                    adapter.notifyDataSetChanged();
                    rl_findHW.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_hw);

        rl_findHW = (RelativeLayout) findViewById(R.id.rl_find_hw);
        textView = (TextView) findViewById(R.id.tv_waiting_find);
        progressBar = (ProgressBar) findViewById(R.id.pb_find_hw);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_find_hw);
        toolbar.setNavigationIcon(R.drawable.close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder().add("practice_type",practice_type).build();
        Request request = new Request.Builder().url(PopupActivity.QUERYPRA_URL).post(formBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = new Message();
                msg.what = FAILED;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("info","result是："+result);
                try {
                    JSONArray PracticeJSONs = new JSONArray(result);
                    for (int i=0;i<PracticeJSONs.length();i++){
                        JSONObject PracticeJSON = PracticeJSONs.getJSONObject(i);
                        Gson gson = new Gson();
                        PracticeBean practiceBean = gson.fromJson(PracticeJSON.toString(),PracticeBean.class);
                        practiceBeanList.add(practiceBean);
                        Log.i("info","接收到practice主表信息是："+ practiceBean.toString());
                    }
                    Message msg = new Message();
                    msg.what = SUCCESS;
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_find);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NormalRecyclerViewAdapter(this);
        adapter.setOnItemClickListener(new NormalRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                Log.d("info", "onClick--> Tag = " +data);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
