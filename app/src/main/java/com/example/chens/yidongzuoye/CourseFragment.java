package com.example.chens.yidongzuoye;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class CourseFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course,container,false);

        CardView liaoJie = (CardView) view.findViewById(R.id.cv_liaojie);
        CardView yaoQiu = (CardView) view.findViewById(R.id.cv_yaoqiu);
        CardView zhongDian = (CardView) view.findViewById(R.id.cv_zhongdian);
        CardView nanDian = (CardView) view.findViewById(R.id.cv_nandian);
        CardView anLi = (CardView) view.findViewById(R.id.cv_anli);
        liaoJie.setOnClickListener(this);
        yaoQiu.setOnClickListener(this);
        zhongDian.setOnClickListener(this);
        nanDian.setOnClickListener(this);
        anLi.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Resources res = getResources();
        switch(v.getId()){
            case R.id.cv_liaojie:
                showDialog(res.getString(R.string.liaojie));
                break;
            case R.id.cv_yaoqiu:
                showDialog(res.getString(R.string.yaoqiu));
                break;
            case R.id.cv_zhongdian:
                showDialog(res.getString(R.string.zhongdian));
                break;
            case R.id.cv_nandian:
                showDialog(res.getString(R.string.nandian));
                break;
            case R.id.cv_anli:
                showDialog("暂无");
                break;
        }
    }

    private void showDialog(String string){
        DialogPlus dialogPlus = DialogPlus.newDialog(getContext())
                .setContentHolder(new ViewHolder(R.layout.dialog))
                .setMargin(10,10,10,10)
                .setPadding(10,10,10,10)
                .setOverlayBackgroundResource(android.R.color.transparent)
                .create();
        dialogPlus.show();
        View view = dialogPlus.getHolderView();
        TextView showText = (TextView) view.findViewById(R.id.show_dialog);
        showText.setText(string);
    }
}
