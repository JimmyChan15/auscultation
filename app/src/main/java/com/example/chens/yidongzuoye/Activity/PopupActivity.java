package com.example.chens.yidongzuoye.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.chens.yidongzuoye.R;
import com.example.chens.yidongzuoye.data.PracticeBean;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by chens on 2017/2/14.
 */
public class PopupActivity extends Activity {
    public static final String QUERYPRA_URL = "http://116.57.86.220/ci/index.php/QueryPractice";
    public static ArrayList<PracticeBean> practiceBeans = new ArrayList<>();
    private final int POST_FAILED = 0,POST_SUCCESS = 1;
    private int btn_number;
    private String practice_type = "2";    //1:自由测试；2：单元测试；3综合测试；4.课后作业
    RelativeLayout rl_jiaZai;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case POST_FAILED:
                    Toast.makeText(getApplication(),"加载章节数据失败",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case POST_SUCCESS:
                    gainButton(btn_number);
                    rl_jiaZai.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        rl_jiaZai = (RelativeLayout) findViewById(R.id.rl_jiaZai);

        RequestBody formBody = new FormBody.Builder().add("practice_type",practice_type).build();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(QUERYPRA_URL).post(formBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("info","获取practice主表失败");
                Message msg = new Message();
                msg.what = POST_FAILED;
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
                        practiceBeans.add(practiceBean);
                        Log.i("info","接收到practice主表信息是："+ practiceBean.toString());
                    }
                    btn_number = PracticeJSONs.length();
                    Message msg = new Message();
                    msg.what = POST_SUCCESS;
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void gainButton(int btn_number){
        //获取屏幕大小
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.rl_popup);
        Button btn[] = new Button[btn_number];
        int j = -1;
        for(int i=0;i<btn_number;i++){
            btn[i] = new Button(this);
            btn[i].setId(2000 + i);
            btn[i].setText(String.valueOf(i+1));  //设置button的文字
            btn[i].setBackgroundResource(R.drawable.circle);
            //设置button宽度和高度
            RelativeLayout.LayoutParams btn_params = new RelativeLayout.LayoutParams((width - 50)/5,(width - 50)/5);
            if(i%5 == 0)
                j++;
            btn_params.leftMargin = 10 + ((width - 50)/5 + 10)*(i%4);
            btn_params.topMargin = 20 + 55*j;
            layout.addView(btn[i],btn_params);
        }

        for (int k=0;k<=btn_number-1;k++){
            btn[k].setTag(k);           //设置标志确认哪个按钮。由于创建时确定id，所以不需要findid
            btn[k].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = (Integer) v.getTag();    //由于内部类的限制
                    Log.i("info","点击了第"+String.valueOf(i+1)+"个按钮");
                    Intent intent = new Intent();
                    intent.putExtra("practice_id", practiceBeans.get(i).getPractice_id());
                    intent.setClass(getApplicationContext(),QuestionActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
