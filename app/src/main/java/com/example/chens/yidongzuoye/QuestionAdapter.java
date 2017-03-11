package com.example.chens.yidongzuoye;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.example.chens.yidongzuoye.data.QuestionBean;

import java.util.ArrayList;

/**
 * 这次viewpager+fragment学到了两个新的知识点，一是传入数据更新adapter，二是动态生成fragment
 */
public class QuestionAdapter extends FragmentPagerAdapter{
    private ArrayList<QuestionBean> questionBeanArrayList;
    public QuestionFragment currentFragment;

    public QuestionAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setQuestionBeanArrayList(ArrayList<QuestionBean> questionBeanArrayList){
        this.questionBeanArrayList = questionBeanArrayList;
    }

    @Override
    public Fragment getItem(int position) {
        QuestionBean questionBean = questionBeanArrayList.get(position);
        return QuestionFragment.newInstance(questionBean);
    }

    @Override
    public int getCount() {
        if (questionBeanArrayList == null){
            return 0;
        }else {
            return questionBeanArrayList.size();
        }
    }

    /**
     * 这是用来返回当前fragment
     * */
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        currentFragment = (QuestionFragment) object;
        super.setPrimaryItem(container, position, object);
    }
}
