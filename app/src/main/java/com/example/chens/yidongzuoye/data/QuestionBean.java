package com.example.chens.yidongzuoye.data;

import java.io.Serializable;

/**
 * Created by chens on 2017/2/5.
 */
public class QuestionBean implements Serializable {
    private int question_id;
    private String knowledge_point_code;  //问题知识点编号
    private int question_type;        //1:选择、2:填空、3:简答、4:论述
    private String text;
    private String option_1;
    private String option_2;
    private String option_3;
    private String option_4;

    public void setQuestion_id(int id){
        this.question_id = id;
    }
    public int getQuestion_id(){
        return question_id;
    }

    public void setKnowledge_point_code(String knowledge_point_code){this.knowledge_point_code = knowledge_point_code;}
    public String getKnowledge_point_code(){return knowledge_point_code;}

    public void setQuestion_type(int question_type){
        this.question_type = question_type;
    }
    public int getQuestion_type(){
        return question_type;
    }

    public void setText(String text){
        this.text = text;
    }
    public String getText(){
        return text;
    }

    public void setOption_1(String option_1){
        this.option_1 = option_1;
    }
    public String getOption_1(){
        return option_1;
    }

    public void setOption_2(String option_2){
        this.option_2 = option_2;
    }
    public String getOption_2(){
        return option_2;
    }

    public void setOption_3(String option_3){
        this.option_3 = option_3;
    }
    public String getOption_3(){
        return option_3;
    }

    public void setOption_4(String option_4){
        this.option_4 = option_4;
    }
    public String getOption_4(){
        return option_4;
    }

    @Override
    public String toString() {
        return "id:"+question_id+"  knowledge_point_code:"+knowledge_point_code+"  question_type:"+question_type
                +"  text:"+text+"  option1:"+option_1+"  option2:"+option_2
                +"  option3:"+option_3+"  option4:"+option_4;
    }
}
