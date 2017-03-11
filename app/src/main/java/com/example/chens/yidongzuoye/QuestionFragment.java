package com.example.chens.yidongzuoye;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chens.yidongzuoye.Activity.MainActivity;
import com.example.chens.yidongzuoye.Activity.QuestionActivity;
import com.example.chens.yidongzuoye.Utils.UploadUtil;
import com.example.chens.yidongzuoye.Utils.Util;
import com.example.chens.yidongzuoye.data.QuestionBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.rey.material.widget.Button;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chens on 2017/2/18.
 */
public class QuestionFragment extends Fragment implements View.OnClickListener, UploadUtil.OnUploadProcessListener {
    private static final String PARAM = "param1";
    private static final String PHOTO_URL = "http://116.57.86.220/ci/index.php/Uploadfile";
    private final int REQUESTCODE = 100;
    private QuestionBean questionBean;
    private List<TextDimensions> textDimensionsList = new ArrayList<>();
    private ArrayList<EditText> editTextList = new ArrayList<>();
    private int lineWidth[];    //每行文字宽度的信息
    private View view;
    private ImageView iv_camera,iv_question;
    private FloatingActionButton floatButton;
    private ImageButton imgBtn_A,imgBtn_B,imgBtn_C,imgBtn_D;
    private TextView tv_Q,tv_A,tv_B,tv_C,tv_D;
    private FrameLayout fl_question;
    private Button btn_updatePhoto;
    private String answer;

    private Uri photoUri;    //照片的Uri地址
    private String newPath;     //照片地址

    //优雅的单例模式
    public QuestionFragment(){}
    public static QuestionFragment newInstance(QuestionBean questionBean){
        QuestionFragment fragment = new QuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM,questionBean);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            questionBean = (QuestionBean) getArguments().getSerializable(PARAM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_question,container,false);
        initView();
        return view;
    }

    private void initView(){
        iv_camera = (ImageView) view.findViewById(R.id.iv_camera_fragment);
        iv_question = (ImageView) view.findViewById(R.id.iv_question);
        imgBtn_A = (ImageButton) view.findViewById(R.id.ques_imgBtn_a);
        imgBtn_B = (ImageButton) view.findViewById(R.id.ques_imgBtn_b);
        imgBtn_C = (ImageButton) view.findViewById(R.id.ques_imgBtn_c);
        imgBtn_D = (ImageButton) view.findViewById(R.id.ques_imgBtn_d);
        floatButton = (FloatingActionButton) view.findViewById(R.id.float_button);
        btn_updatePhoto = (Button) view.findViewById(R.id.btn_update_photo);
        tv_Q = (TextView) view.findViewById(R.id.ques_text);
        tv_A = (TextView) view.findViewById(R.id.ques_option_1);
        tv_B = (TextView) view.findViewById(R.id.ques_option_2);
        tv_C = (TextView) view.findViewById(R.id.ques_option_3);
        tv_D = (TextView) view.findViewById(R.id.ques_option_4);
        fl_question = (FrameLayout) view.findViewById(R.id.fl_question);
        floatButton.setOnClickListener(this);
        btn_updatePhoto.setOnClickListener(this);
        imgBtn_A.setOnClickListener(this);
        imgBtn_B.setOnClickListener(this);
        imgBtn_C.setOnClickListener(this);
        imgBtn_D.setOnClickListener(this);

        if (questionBean.getQuestion_type() == 1){                   //选择题
            tv_Q.setText(questionBean.getText());
            tv_A.setVisibility(View.VISIBLE);
            tv_B.setVisibility(View.VISIBLE);
            tv_C.setVisibility(View.VISIBLE);
            tv_D.setVisibility(View.VISIBLE);
            imgBtn_A.setVisibility(View.VISIBLE);
            imgBtn_B.setVisibility(View.VISIBLE);
            imgBtn_C.setVisibility(View.VISIBLE);
            imgBtn_D.setVisibility(View.VISIBLE);
            btn_updatePhoto.setVisibility(View.GONE);
            iv_camera.setVisibility(View.GONE);
            tv_A.setText(questionBean.getOption_1());
            tv_B.setText(questionBean.getOption_2());
            tv_C.setText(questionBean.getOption_3());
            tv_D.setText(questionBean.getOption_4());
            Log.i("info","数据为："+questionBean.toString());
        }else if (questionBean.getQuestion_type() == 2){           //填空题
            tv_Q.setText(questionBean.getText());
            imgBtn_A.setVisibility(View.GONE);
            imgBtn_B.setVisibility(View.GONE);
            imgBtn_C.setVisibility(View.GONE);
            imgBtn_D.setVisibility(View.GONE);
            tv_Q.post(new Runnable() {
                @Override
                public void run() {
//                    int height = tv_Q.getHeight();
//                    int width1 = tv_Q.getWidth();
//                    int measure_width = tv_Q.getMeasuredWidth();
                    int et_Height = tv_Q.getHeight() / tv_Q.getLineCount();     //不知道会不会为0，需要注意
//                    Log.i("info","lineHeight："+tv_Q.getLineHeight() + "  height:"+height+"  width:"+width1+"  measure_width:"+measure_width+"  et_Height:"+et_Height);
                    Log.i("info","数据为："+questionBean.toString());
                    lineWidth = new int[tv_Q.getLineCount()];
                    Layout layout = tv_Q.getLayout();
                    if (layout != null){
                        for (int i=0;i<tv_Q.getLineCount();i++){
//                            Log.i("info","lineCount is:"+tv_Q.getLineCount()+"  getLineWidth is:"+ layout.getLineWidth(i));
                            lineWidth[i] = (int) layout.getLineWidth(i);
                        }
                    }else{
                        Log.i("info","layout is null");
                    }
                    find_XiaHuaXian(questionBean.getText());
                    generateEditText(getContext(),fl_question,textDimensionsList,et_Height);
                }
            });
        }else{                                                  //大题
            imgBtn_A.setVisibility(View.GONE);
            imgBtn_B.setVisibility(View.GONE);
            imgBtn_C.setVisibility(View.GONE);
            imgBtn_D.setVisibility(View.GONE);
            tv_A.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);
            tv_D.setVisibility(View.GONE);
            floatButton.setVisibility(View.VISIBLE);
            iv_camera.setVisibility(View.VISIBLE);
            tv_Q.setText(questionBean.getText());
            Log.i("info","数据为："+questionBean.toString());
        }

        String imageUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1489084826440&di=c590eed207de6569028064667123817e&imgtype=0&src=http%3A%2F%2Fww1.sinaimg.cn%2Flarge%2F4dd50acfjw6dbo87eyuafj.jpg";
        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .showImageOnLoading()
//                .showImageOnFail()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(imageUrl,iv_question,options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUESTCODE){
            if(resultCode == Activity.RESULT_OK){
//                String[] pojo = {MediaStore.Images.Media.DATA};
//                Cursor cursor = getActivity().managedQuery(photoUri,pojo,null,null,null);
//                if(cursor != null){
//                    int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
//                    cursor.moveToFirst();
//                    oldPath = cursor.getString(columnIndex);
//                    cursor.close();
//                }
                String oldPath = photoUri.getPath();
                String rootPath = Util.getParentPath(oldPath);
                String end = Util.end(oldPath);
                newPath = Util.makePath(rootPath, MainActivity.student_id+"_" +QuestionActivity.practice_id
                        +"_"+questionBean.getKnowledge_point_code()
                        +"_"+questionBean.getQuestion_id()+end);
                File file = new File(oldPath);
                if (file.renameTo(new File(newPath))){
                    Log.i("info","photo path is :"+newPath);
                    if (QuestionActivity.photo_Url.size()>1){
                        boolean hasAdd = false;
                        for (int j=0;j<QuestionActivity.photo_Url.size();j++){
                            if (QuestionActivity.photo_Url.get(j).equals(newPath)) {
                                hasAdd = true;
                                break;
                            }
                        }
                        if (!hasAdd)
                            QuestionActivity.photo_Url.add(newPath);
                    }else
                        QuestionActivity.photo_Url.add(newPath);
                    Bitmap bitmap = BitmapFactory.decodeFile(newPath);
                    iv_camera.setImageBitmap(bitmap);
                    btn_updatePhoto.setVisibility(View.VISIBLE);
                }else{
                    Log.i("info","重命名失败");
                }
//                if(oldPath != null &&(oldPath.endsWith(".png") || oldPath.endsWith(".PNG") || oldPath.endsWith(".jpg") || oldPath.endsWith(".JPG"))){
//                    String rootPath = Util.getParentPath(oldPath);   //修改文件名
//                    String end = Util.end(oldPath);
//                    String newPath = Util.makePath(rootPath,"123"+end);
//                    File file = new File(oldPath);
//                    if(file.renameTo(new File(newPath))){
//                        Bitmap bitmap = BitmapFactory.decodeFile(newPath);
//                        iv_camera.setImageBitmap(bitmap);
//                        Log.i("info","重命名成功");
//                    }else {
////                        Snackbar.make(getView(),"拍照失败",Snackbar.LENGTH_SHORT).show();
//                        Log.i("info","重命名失败");
//                    }
////                    Snackbar.make(getView(),"拍照失败",Snackbar.LENGTH_SHORT).show();
//                    Log.i("info","图片地址："+newPath);
//                }else{
////                    Snackbar.make(getView(),"拍照失败",Snackbar.LENGTH_SHORT).show();
//                    Log.i("info","没有返回正常路径");
//                }
            }
        }
    }

    /**
     * 遍历字符串找出下划线
     * 并给dimension数组赋值
     */
    private void find_XiaHuaXian(String QuestionText){
        textDimensionsList.clear();
        boolean lock = false;
        int position = 0;
        StringBuffer sb = new StringBuffer("_");
        for (int i=1;i<QuestionText.length();i++){      //从1开始遍历，若连续出现两个'_'则挖空
            if (('_' == QuestionText.charAt(i)) && ('_' == QuestionText.charAt(i-1))){
                if(!lock){
                    position = i-1;
                    lock = true;
                }
                sb.append('_');
            }else{
                if (lock){
//                    Log.i("info","position是："+position);
                    TextDimensions dimensions = getDimensions(sb.toString(),position);
                    textDimensionsList.add(dimensions);
                    sb = new StringBuffer("_");
                    lock = false;
                }
            }
        }
    }

    /**
     * 根据获得字符的位置数据生成EditText进行替换
     */
    private void generateEditText(final Context context, FrameLayout container, List<TextDimensions> dimensions, int height) {
        editTextList.clear();
        for(TextDimensions dem:dimensions){
            final EditText editText=new EditText(context);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) dem.mTextWidth,height);
            container.addView(editText,lp);
            editText.setTranslationX(dem.mStartX);
            editText.setTranslationY(dem.mStartY);
            editText.setTextSize(15);           //这里设置生成的editText属性
            editText.setPadding(0,0,0,0);
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus){
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            });
//            editText.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {}
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                }
//            });
            editTextList.add(editText);
            Log.i("info","add a text  "+editTextList.size());
        }
    }

    /**
     * 获取挖空的位置数据及尺寸信息
     */
    private TextDimensions getDimensions(String targetText, int position){
        TextDimensions textDimensions = new TextDimensions();
        TextPaint textPaint = tv_Q.getPaint();
        textDimensions.mTextWidth = textPaint.measureText(targetText);  //字符长度
//        Log.i("info","measureText's length is:"+textDimensions.mTextWidth);

        int lineHeight = tv_Q.getHeight() / tv_Q.getLineCount();
        int length = (int) textPaint.measureText(tv_Q.getText(),0,position);
        int i;
        for (i=0;i<lineWidth.length;i++){
            if (length<lineWidth[i])
                break;
            length -= lineWidth[i];
        }
//        Log.i("info","tv_Q = "+tv_Q.getText().toString());
        textDimensions.mStartX = length;   //左边X坐标
        textDimensions.mStartY = i*lineHeight;   //顶部Y坐标

        return textDimensions;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ques_imgBtn_a:
                setAnswer_choose("A");
                resetButton();
                imgBtn_A.setBackgroundResource(R.drawable.tick);
                break;
            case R.id.ques_imgBtn_b:
                setAnswer_choose("B");
                resetButton();
                imgBtn_B.setBackgroundResource(R.drawable.tick);
                break;
            case R.id.ques_imgBtn_c:
                setAnswer_choose("C");
                resetButton();
                imgBtn_C.setBackgroundResource(R.drawable.tick);
                break;
            case R.id.ques_imgBtn_d:
                setAnswer_choose("D");
                resetButton();
                imgBtn_D.setBackgroundResource(R.drawable.tick);
                break;
            case R.id.float_button:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                //使用ContentValues存放照片路径
//                ContentValues values = new ContentValues();
//                photoUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                try {
                    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File image = File.createTempFile("imageName.jpg",".jpg",storageDir);
                    photoUri = Uri.fromFile(image);
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
                    getActivity().startActivityFromFragment(QuestionFragment.this,intent,REQUESTCODE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                getActivity().startActivityForResult(intent,REQUESTCODE);
                break;
            case R.id.btn_update_photo:
                if (newPath != null){
                    Snackbar.make(floatButton,"正在上传...",Snackbar.LENGTH_SHORT).show();
                    toUploadFile(newPath);
                }else{
                    Snackbar.make(floatButton,"无法获取图片路径，请重新拍摄",Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void resetButton(){
        imgBtn_A.setBackgroundResource(R.drawable.a);
        imgBtn_B.setBackgroundResource(R.drawable.b);
        imgBtn_C.setBackgroundResource(R.drawable.c);
        imgBtn_D.setBackgroundResource(R.drawable.d);
    }

    /**
     *  表示文本的尺寸信息的数据结构
     */
    private class TextDimensions {
        public float mStartX;
        public float mStartY;
        public float mTextWidth;
    }

    public String getAnswer_fill(){
        if (editTextList.size() != 0){
            StringBuilder sb = new StringBuilder(editTextList.get(0).getText());
            for (int i=1;i<editTextList.size();i++){
                sb.append("，");
                sb.append(editTextList.get(i).getText());
            }
            Log.i("info","edittextlist is not null");
            return sb.toString();
        }else{
            Log.i("info","edittextlist is null");
            return "";
        }

    }

    public void setAnswer_choose(String answer){this.answer = answer;}
    public String getAnswer_choose(){return answer;}

    private void toUploadFile(String path){
        String fileKey = "file";
        UploadUtil uploadUtil = UploadUtil.getInstance();
        uploadUtil.setOnUploadProcessListener(this);

        Map<String,String> params = new HashMap<>();
        params.put("orderId","11111");
        uploadUtil.uploadFile(path,fileKey, PHOTO_URL,params);
    }


    @Override
    public void onUploadDone(int responseCode, String message) {
        Log.i("info","the response is:"+message + "responseCode:"+responseCode);
        if(responseCode == 1){
            Snackbar.make(floatButton,"上传成功",Snackbar.LENGTH_SHORT).show();
        }else
            Snackbar.make(floatButton,"上传失败",Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onUploadProcess(int uploadSize) {}

    @Override
    public void initUpload(int fileSize) {}
}
