package com.example.chens.yidongzuoye.data;

/**
 * Created by chens on 2017/2/15.
 */
public class PracticeBean {
    private int practice_id;       //第几章
    private String release_time;
    private int teacher_id;    //用了int，要看实际够不够用
    private String deadline;
    private String practice_name;  //试题名称
    private int is_release_ans;

    public void setPractice_id(int practice_id){
        this.practice_id = practice_id;
    }
    public int getPractice_id(){
        return practice_id;
    }

    public void setRelease_time(String release_time){
        this.release_time = release_time;
    }
    public String getRelease_time(){
        return release_time;
    }

    public void setTeacher_id(int teacher_id){this.teacher_id = teacher_id;}
    public int getTeacher_id(){
        return teacher_id;
    }

    public void setDeadline(String deadline){
        this.deadline = deadline;
    }
    public String getDeadline(){
        return deadline;
    }

    public void setPractice_name(String practice_name){
        this.practice_name = practice_name;
    }
    public String getPractice_name(){
        return practice_name;
    }

    public void setIs_release_ans(int is_release_ans){this.is_release_ans = is_release_ans;}
    public int getIs_release_ans(){return is_release_ans;}

    @Override
    public String toString() {
        return "practice_id:"+practice_id+"  release_time:"+release_time
                +"  teacher_id:" +teacher_id+"  deadline:" +deadline
                +"  practice_name:"+practice_name+"  is_release_ans:"+is_release_ans;
    }
}
