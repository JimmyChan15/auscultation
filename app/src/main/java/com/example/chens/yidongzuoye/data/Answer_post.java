package com.example.chens.yidongzuoye.data;

/**
 *这是需要上传的答案数据格式
 */
public class Answer_post {
    private String student_id;
    private int practice_id;
    private int question_id;
    private String knowledge_point_code;
    private String ans_student;

    public void setStudent_id(String student_id){this.student_id = student_id;}
    public String getStudent_id(){return student_id;}

    public void setPractice_id(int practice_id){this.practice_id = practice_id;}
    public int getPractice_id(){return practice_id;}

    public void setKnowledge_point_code(String knowledge_point_code){this.knowledge_point_code = knowledge_point_code;}
    public String getKnowledge_point_code(){return knowledge_point_code;}

    public void setQuestion_id(int question_id){this.question_id = question_id;}
    public int getQuestion_id(){return question_id;}

    public void setAns_student(String ans_student){this.ans_student = ans_student;}
    public String getAns_student(){return ans_student;}

    @Override
    public String toString() {
        return "student_id:"+ student_id +"  knowledge_point_code:"+ knowledge_point_code
                +"  question_id:"+question_id+"  ans_student:"+ ans_student;
    }
}
