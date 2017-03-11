package com.example.chens.yidongzuoye;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by chens on 2017/2/13.
 */
public class MyPopupWindow {
    public static android.widget.PopupWindow popupWindow;

    public static TextView showWaitingPopupWindow(final Activity context, String string) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.waiting_popwindow, null);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.pb_waiting);
        if (progressBar.getProgressDrawable() != null) {
            progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(context, R.color.paleturquoise), PorterDuff.Mode.MULTIPLY);
        }
        TextView waitingText = (TextView) view.findViewById(R.id.waitingText);
        waitingText.setText(string);
        // 创建一个PopuWidow对象
        popupWindow = new android.widget.PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        /**
         * 定位PopupWindow，让它恰好显示在Action Bar的下方。 通过设置Gravity，确定PopupWindow的大致位置。
         * 首先获得状态栏的高度，再获取Action bar的高度，这两者相加设置y方向的offset样PopupWindow就显示在action
         * bar的下方了。 通过dp计算出px，就可以在不同密度屏幕统一X方向的offset.但是要注意不要让背景阴影大于所设置的offset，
         * 否则阴影的宽度为offset.
         */
        // 获取状态栏高度
        View parentView = layoutInflater.inflate(R.layout.activity_main, null);
        Rect frame = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        //状态栏高度：frame.top
        WindowManager.LayoutParams params = context.getWindow().getAttributes();
        params.alpha = 0.7f;
        context.getWindow().setAttributes(params);
        // 使其聚集 ，要想监听菜单里控件的事件就必须要调用此方法
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(false);
        // 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        // PopupWindow的显示及位置设置
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        popupWindow.setAnimationStyle(R.style.more_popwinow_anim_style);
        popupWindow.setOnDismissListener(new android.widget.PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 改变显示的按钮图片为正常状态
                WindowManager.LayoutParams params = context.getWindow().getAttributes();
                params.alpha = 1f;
                context.getWindow().setAttributes(params);

            }
        });
        return waitingText;
    }

    public static void dismissWaitingPopupWindow() {
        popupWindow.dismiss();
    }
}
