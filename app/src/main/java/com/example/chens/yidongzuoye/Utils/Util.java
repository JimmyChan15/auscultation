package com.example.chens.yidongzuoye.Utils;

import java.io.File;

/**
 * Created by chens on 2017/2/20.
 */
public class Util {

    public static String getParentPath(String path){
        int pos = path.lastIndexOf('/');
        if (pos != -1){
            return path.substring(0,pos);
        }
        return  "";
    }

    public static String makePath(String path1,String path2){
        if (path1.endsWith(File.separator)){
            return path1 + path2;
        }
        return path1+File.separator+path2;
    }

    public static String end(String path){
        if (path.endsWith(".jpg")) return ".jpg";
        else if(path.endsWith(".JPG")) return ".JPG";
        else if(path.endsWith(".png")) return ".png";
        else if(path.endsWith(".png")) return ".PNG";
        else return "";
    }
}
