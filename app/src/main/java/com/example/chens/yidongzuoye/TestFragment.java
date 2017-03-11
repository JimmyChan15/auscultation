package com.example.chens.yidongzuoye;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.chens.yidongzuoye.Activity.FindHWActivity;
import com.example.chens.yidongzuoye.Activity.PopupActivity;

public class TestFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test,container,false);

//        Button ziYouLianXi = (Button) view.findViewById(R.id.btn_ziYouLianXi);
        Button keHouZuoYe = (Button) view.findViewById(R.id.btn_keHouZuoYe);
        Button danYuanCeShi = (Button) view.findViewById(R.id.btn_danYuanCeShi);
//        ziYouLianXi.setOnClickListener(this);
        keHouZuoYe.setOnClickListener(this);
        danYuanCeShi.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
//            case R.id.btn_ziYouLianXi:
//                intent.setClass(getActivity(), FindHWActivity.class);
//                break;
            case R.id.btn_keHouZuoYe:
                intent.setClass(getActivity(), FindHWActivity.class);
                break;
            case R.id.btn_danYuanCeShi:
                intent.setClass(getActivity(), PopupActivity.class);
                break;
        }
        startActivity(intent);
    }
}
