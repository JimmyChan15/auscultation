package com.example.chens.yidongzuoye.Activity;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chens.yidongzuoye.CourseFragment;
import com.example.chens.yidongzuoye.FragmentAdapter;
import com.example.chens.yidongzuoye.MyFragment;
import com.example.chens.yidongzuoye.R;
import com.example.chens.yidongzuoye.TestFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView course_tv,test_tv,my_tv;
    private ImageView tabLine;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private int currIndex;        //当前选项卡
    private int bmpW;             //滚动条的宽度
    private int screenWidth;      //屏幕宽度
    private int offset;           //图片移动偏移量
    private Toolbar toolbar;
    public static final String PREFERENCE_NAME = "user";
    public static Boolean isLogin;   //用户登录状态，读取是否登录的状态
    public static String student_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle("移动作业");
        toolbar.setTitleTextColor(Color.WHITE);
        initTextView();
        initTabLine();
        initViewPager();

        String versionName = getVersionName();
        Log.i("info",versionName);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME,MODE_PRIVATE);
        isLogin = sharedPreferences.getBoolean("isLogin",false);
        student_id = sharedPreferences.getString("stu_name","00000");
        if (!isLogin)
            Snackbar.make(viewPager,"您尚未登录，请到\"我的\"页面登录",Snackbar.LENGTH_SHORT).show();
        Log.i("info","是否登录："+isLogin.toString());
    }

    public void initTextView(){
        course_tv = (TextView) findViewById(R.id.id_course);
        test_tv = (TextView) findViewById(R.id.id_test);
        my_tv = (TextView) findViewById(R.id.id_my);

        course_tv.setOnClickListener(new TabOnClickListener(0));
        test_tv.setOnClickListener(new TabOnClickListener(1));
        my_tv.setOnClickListener(new TabOnClickListener(2));
    }

    public void initTabLine(){
        tabLine = (ImageView) findViewById(R.id.id_tabLine);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;       //获取屏幕宽度

        bmpW = BitmapFactory.decodeResource(getResources(),R.drawable.ic_blue_bg_i).getWidth();
        offset = (screenWidth/3 - bmpW)/2;

        //设置平移，使下划线平移到初始位置
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset,0);
        tabLine.setImageMatrix(matrix);

        //设置滑动条宽度为屏幕1/3
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tabLine.getLayoutParams();
        layoutParams.width = screenWidth / 3;
        tabLine.setLayoutParams(layoutParams);
    }

    public void initViewPager(){
        viewPager = (ViewPager) findViewById(R.id.viewPager_main);
        fragments = new ArrayList<Fragment>();
        Fragment courseFragment = new CourseFragment();
        Fragment testFragment = new TestFragment();
        Fragment myFragment = new MyFragment();

        fragments.add(courseFragment);
        fragments.add(testFragment);
        fragments.add(myFragment);

        //设置并装载适配器
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(),fragments));
        viewPager.setCurrentItem(0);        //当前页面
        viewPager.setOnPageChangeListener(new TabOnPageChangeListener());
    }

    //选项卡点击事件
    public class TabOnClickListener implements View.OnClickListener{
        private int index = 0;

        public TabOnClickListener(int i){
            index = i;
        }
        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }
    }

    //滑动时的监听事件，通过设置动画实现滑动效果
    public class TabOnPageChangeListener implements ViewPager.OnPageChangeListener{

        private int one = offset*2+bmpW;   //相邻页面的偏移量
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Animation animation = new TranslateAnimation(currIndex * one, position*one, 0, 0);  //平移动画
            currIndex = position;
            animation.setFillAfter(true);        //动画停在最后一帧，否则会回到初始状态
            animation.setDuration(100);          //持续时间
            tabLine.startAnimation(animation);
            resetColor();
            switch (position){
                case 0:
                    course_tv.setTextColor(getResources().getColor(R.color.currentIndex));
                    break;
                case 1:
                    test_tv.setTextColor(getResources().getColor(R.color.currentIndex));
                    break;
                case 2:
                    my_tv.setTextColor(getResources().getColor(R.color.currentIndex));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void resetColor(){
        course_tv.setBackgroundResource(R.color.lightcyan);
        test_tv.setBackgroundResource(R.color.lightcyan);
        my_tv.setBackgroundResource(R.color.lightcyan);
        course_tv.setTextColor(getResources().getColor(R.color.green2));
        test_tv.setTextColor(getResources().getColor(R.color.green2));
        my_tv.setTextColor(getResources().getColor(R.color.green2));
    }

    public String getVersionName(){
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = new PackageInfo();
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo.versionName;
    }
}
