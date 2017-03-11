package com.example.chens.yidongzuoye;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chens.yidongzuoye.Activity.MainActivity;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyFragment extends Fragment {
    private static final String loginURL = "http://116.57.86.220/ci/index.php/LogIn";
    private String studentNumber, password;
    //使用message更改UI
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                MyPopupWindow.dismissWaitingPopupWindow();
            }else if(msg.what == 2){  //2代表登陆成功
                MyPopupWindow.dismissWaitingPopupWindow();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.PREFERENCE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("stu_name",studentNumber);
                editor.putString("password",password);
                editor.putBoolean("isLogin",true);
                editor.apply();
            }else  if(msg.what == 3){  //3代表登录失败
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.PREFERENCE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLogin",false);
                editor.apply();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my,container,false);

        final EditText et_studentNumber = (EditText) view.findViewById(R.id.stu_num);
        final EditText et_password = (EditText) view.findViewById(R.id.password);
        Button btn_login = (Button) view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentNumber = et_studentNumber.getText().toString();
                password = et_password.getText().toString();
                if(studentNumber.length() == 3){
                    MyPopupWindow.showWaitingPopupWindow(getActivity(),"正在登录...");
                    Login(studentNumber,password);
                }else{
                    et_studentNumber.setError("学号长度不正确");
                }
            }
        });
        LinearLayout ll_login = (LinearLayout) view.findViewById(R.id.ll_fra_my);
        TextView tv_showLogin = (TextView) view.findViewById(R.id.tv_show_login);
        if (MainActivity.isLogin){
            ll_login.setVisibility(View.INVISIBLE);
            tv_showLogin.setText("学号："+MainActivity.student_id+"\n"+"姓名：");

        }

        Button btn_update = (Button) view.findViewById(R.id.check_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("info","click the button to Service");
//                Intent intent = new Intent();
//                intent.setAction("android.intent.action.VIEW");
//                Uri uri = Uri.parse(CHECKUPDATEURL);
//                intent.setData(uri);
//                startActivity(Intent.createChooser(intent,"请选择下载的浏览器"));

//                intent.putExtra("apkUrl",CHECKUPDATEURL);
//                intent.putExtra("appName","qq_hd_mini_1.4.apk");
//                getActivity().startService(intent);
            }
        });
        return view;
    }

    public void Login(String stu_num, String password){
        //建立表单上传数据
        RequestBody formBody = new FormBody.Builder()
                .add("student_id",stu_num)
                .add("psword",password)
                .build();
        final Request request = new Request.Builder().post(formBody).url(loginURL).build();
        final OkHttpClient client = new OkHttpClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                Message msg = new Message();
                msg.what = 1;
                try {
                    response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        if(response.body().string().equals("{\"state\":\"\\u767b\\u9646\\u6210\\u529f\"}")){    //偷懒不解析json了
                            msg.what = 2;
                            handler.sendMessage(msg);
                            Snackbar.make(getView(),"登陆成功",Snackbar.LENGTH_SHORT).show();
                        }else{
                            msg.what = 3;
                            handler.sendMessage(msg);
                            Snackbar.make(getView(),"学号或密码错误",Snackbar.LENGTH_SHORT).show();
                        }
                        Log.i("info","请求成功");
                    }else{
                        handler.sendMessage(msg);
                        Log.i("info","请求失败");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

}

