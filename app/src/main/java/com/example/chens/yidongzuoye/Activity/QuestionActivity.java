package com.example.chens.yidongzuoye.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chens.yidongzuoye.QuestionAdapter;
import com.example.chens.yidongzuoye.QuestionFragment;
import com.example.chens.yidongzuoye.R;
import com.example.chens.yidongzuoye.Utils.UploadUtil;
import com.example.chens.yidongzuoye.data.Answer_post;
import com.example.chens.yidongzuoye.data.QuestionBean;
import com.google.gson.Gson;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by chens on 2017/2/18.
 */
public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String QUERY_QUESTION_URL = "http://116.57.86.220/ci/index.php/QueryQuestion";
    private static final String ANS_URL = "http://116.57.86.220/ci/index.php/UploadAns";
    private static final int POST_SUCCESS=1, POST_FAILED=0,onUploadDone = 2,WRITE_FILL = 3;
    private ArrayList<QuestionBean> questionBeans = new ArrayList<>();  //此list存接收的问题
    private static ArrayList<Answer_post> answer_posts = new ArrayList<>();    //此list存上传的答案
    public static ArrayList<String> photo_Url = new ArrayList<>();
    public boolean[] isWrite;         //是否已经写了答案的数组
    private RelativeLayout rl_question;
    private ProgressBar progressBar;
    private TextView textView;
    private ViewPager viewPager;
    private Button update_answer;
    public static int practice_id;      //第几章的id号
    private QuestionAdapter adapter;
    private QuestionFragment currentFragment;
    private int prePosition = 0;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        if(MainActivity.student_id.equals("00000")){
            Toast.makeText(this,"您尚未登录，请到\"我的\"页面登录",Toast.LENGTH_SHORT).show();
            finish();
        }
        Intent intent = getIntent();
        practice_id = intent.getIntExtra("practice_id",0);
        if (practice_id == 0){
            Toast.makeText(this,"抱歉，应用发生错误",Toast.LENGTH_SHORT).show();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_question);
        Button btn_pre = (Button) findViewById(R.id.btn_pre);
        Button btn_next = (Button) findViewById(R.id.btn_next);
        Button show_all = (Button) findViewById(R.id.show_all);
        update_answer = (Button) findViewById(R.id.update_answer);
        rl_question = (RelativeLayout) findViewById(R.id.rl_question);
        progressBar = (ProgressBar) findViewById(R.id.pb_question);
        textView = (TextView) findViewById(R.id.tv_showWaiting_question);
        viewPager = (ViewPager) findViewById(R.id.viewpager_question);
//        camera = (ImageButton) findViewById(R.id.imgBtn_camera);
        btn_pre.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        show_all.setOnClickListener(this);
//        camera.setOnClickListener(this);
        update_answer.setOnClickListener(this);

        toolbar.setNavigationIcon(R.drawable.arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (questionBeans.size() == 0){
            //获取问题数据
            RequestBody formBody = new FormBody.Builder().add("practice_id",String.valueOf(practice_id)).build();
            Request request = new Request.Builder().url(QUERY_QUESTION_URL).post(formBody).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("info","获取question主表失败");
                    //这里是另外一种在UI线程运行的方法
                    QuestionActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            textView.setText("加载失败了");
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    Log.i("info","获取的response是："+result);
                    try {
                        JSONArray QuestionJSONs = new JSONArray(result);
                        for (int i=0;i<QuestionJSONs.length();i++){
                            JSONObject QuestionJSON = QuestionJSONs.getJSONObject(i);
                            Gson gson = new Gson();
                            QuestionBean questionBean = gson.fromJson(QuestionJSON.toString(),QuestionBean.class);
                            questionBeans.add(questionBean);
                            Log.i("info","接收到题目信息："+ questionBean.toString()+" ||"+QuestionJSONs.length()+"  ||"+questionBeans.size());
                        }
                        Message msg = new Message();
                        msg.what = POST_SUCCESS;
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        adapter = new QuestionAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.i("info","Scrolled: "+position);
            }

            @Override
            public void onPageSelected(int position) {      //注意，最后一题还没有写入答案！
                Log.i("info","Selected: "+position);
                if ((position+1) == questionBeans.size()){
                    update_answer.setVisibility(View.VISIBLE);
                }else{
                    update_answer.setVisibility(View.GONE);
                }

                if (prePosition != position){
                    //通过判断题目类型写入答案
                    if (questionBeans.get(prePosition).getQuestion_type()==1){
                        currentFragment = adapter.currentFragment;
                        writeAnswer(currentFragment.getAnswer_choose());      //获取前一个fragment的数据
                        Log.i("info","the xuanze answer is:"+currentFragment.getAnswer_choose());
                    }else if (questionBeans.get(prePosition).getQuestion_type() == 2){
                        currentFragment = adapter.currentFragment;
                        /**
                         * 实在搞不懂为什么不能在这里写！！！
                         * (弄明白了，先currentfragment赋值再取，所以估计这个赋值是上一个的赋值，但是为什么换到
                         * 其他地方然后把顺序换一下也可以？？？）
                         * */
                        Message msg = new Message();
                        msg.what = WRITE_FILL;
                        handler.sendMessage(msg);
                    }else{
                        writeAnswer("大题已上传");
                    }
//                    Log.i("info","is 2");
//                    currentFragment = adapter.currentFragment;           //保存当前fragment对象
//                    Log.i("info","is 3");

                    prePosition = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Log.i("info","Scroll State Changed: "+state);
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case POST_SUCCESS:
                    adapter.setQuestionBeanArrayList(questionBeans);
                    adapter.notifyDataSetChanged();                             //这里刷新adapter
                    currentFragment = adapter.currentFragment;

                    rl_question.setVisibility(View.GONE);
                    isWrite = new boolean[questionBeans.size()];
                    for(int i=0;i<questionBeans.size();i++){isWrite[i] = false;}
                    break;
                case onUploadDone:
                    if (msg.arg1 == 1) {
                        Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getApplicationContext(),"上传失败",Toast.LENGTH_SHORT).show();
                    break;
                case WRITE_FILL:
                    Log.i("info","is 1");
                    writeAnswer(currentFragment.getAnswer_fill());      //获取前一个fragment的数据
                    Log.i("info","the tiankong answer is:"+answer_posts.get(prePosition).getAns_student());
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_pre:
                int currentItem1 = viewPager.getCurrentItem();
                currentItem1 = currentItem1 - 1;
                if (currentItem1 < 0){
                    currentItem1 = 0;
                    Log.i("info","current1 < 0"+"  current1 is:"+currentItem1);
                }
                viewPager.setCurrentItem(currentItem1,true);
                break;
            case R.id.btn_next:
                int currentItem2 = viewPager.getCurrentItem();
                currentItem2 = currentItem2 + 1;
                if (currentItem2 > questionBeans.size()-1){
                    currentItem2 = questionBeans.size()-1;
                    Log.i("info","current2 > "+"current2 is:"+currentItem2);
                }

                //通过判断题目类型写入答案
//                if (questionBeans.get(prePosition).getQuestion_type()==1){
////                    currentFragment = adapter.currentFragment;
//                    writeAnswer(currentFragment.getAnswer_choose());
//                    Log.i("info","the xuanze answer is:"+currentFragment.getAnswer_choose());
//                }else if (questionBeans.get(prePosition).getQuestion_type() == 2){
////                    currentFragment = adapter.currentFragment;
//                    writeAnswer(currentFragment.getAnswer_fill());      //获取前一个fragment的数据
//                    Log.i("info","the tiankong answer is:"+answer_posts.get(prePosition).getAns_student());
//                }else{
//                    writeAnswer("大题已上传");
//                }

                viewPager.setCurrentItem(currentItem2,true);
                break;

            case R.id.show_all:
                break;

            case R.id.update_answer:
                if (questionBeans.get(prePosition).getQuestion_type()==1){
                    writeAnswer(currentFragment.getAnswer_choose());
                    Log.i("info","the xuanze answer is:"+currentFragment.getAnswer_choose());
                }else if (questionBeans.get(prePosition).getQuestion_type() == 2){
                    currentFragment = adapter.currentFragment;
                    writeAnswer(currentFragment.getAnswer_fill());      //获取前一个fragment的数据
                    Log.i("info","the tiankong answer is:"+answer_posts.get(prePosition).getAns_student());
                }else{
                    writeAnswer("大题已上传");
                }
                Gson gson = new Gson();
                String ans_json = gson.toJson(answer_posts);
                RequestBody formbody = new FormBody.Builder().add("ans",ans_json).build();
                Request request = new Request.Builder().url(ANS_URL).post(formbody).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Message msg = new Message();
                        msg.what = onUploadDone;
                        msg.arg1 = 0;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message msg = new Message();
                        msg.what = onUploadDone;
                        msg.arg1 = 1;
                        handler.sendMessage(msg);
                    }
                });
                Log.i("info","answers is:"+answer_posts.toString());
                Log.i("info","photo url is:"+photo_Url.toString());
                break;
        }
    }

    private void showNumDialog(){
        DialogPlus dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.dialog_show_num))
                .setMargin(10,10,10,10)
                .setPadding(10,10,10,10)
                .setOverlayBackgroundResource(android.R.color.transparent)
                .create();
        dialogPlus.show();
        View view = dialogPlus.getHolderView();
    }

    private void writeAnswer(String answer){
//        int i = viewPager.getCurrentItem();
        int i = prePosition;
        if (!isWrite[i]){
            Answer_post answer_post = new Answer_post();
            answer_post.setStudent_id(MainActivity.student_id);
            answer_post.setPractice_id(practice_id);
            answer_post.setQuestion_id(questionBeans.get(i).getQuestion_id());
            answer_post.setKnowledge_point_code(questionBeans.get(i).getKnowledge_point_code());
            answer_post.setAns_student(answer);
            answer_posts.add(answer_post);
            isWrite[i] = true;
        }else{
            answer_posts.get(i).setAns_student(answer);
        }
    }
}
