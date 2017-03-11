package com.example.chens.yidongzuoye.data;

/**
 * Created by chens on 2017/2/15.
 */
public class AnswerBean {
    private int question_id;
    private int knowledge_point_code;
    private int question_type;
    private String ans;

    public void setQuestion_id(int question_id){ this.question_id = question_id;}
    public int getQuestion_id(){return  question_id;}

    public void setKnowledge_point_code(int knowledge_point_code){this.knowledge_point_code = knowledge_point_code;}
    public int getKnowledge_point_code(){return knowledge_point_code;}

    public void setQuestion_type(int question_type){this.question_type = question_type;}
    public int getQuestion_type(){return question_type;}

    public void setAns(String ans){ this.ans = ans;}
    public String getAns(){return ans;}

    @Override
    public String toString() {
        return "question_id:"+question_id+"  question_type:"+question_type
                +"  knowledge_point_code:"+knowledge_point_code +"  ans"+ans;
    }
}
