package cn.edu.seu.webPageExtractor.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class StringUtil {
    public static String replaceSpeace(String oldString){
        return oldString.trim().replace(" ", "");
    }

    public static String getLastSubString(String oldString){
        String[] olds = oldString.split("/");
        return olds[olds.length-1];
    }

    public static String decodeString(String oldString){
        try {
            oldString = oldString.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            oldString = oldString.replaceAll("\\+", "%2B");
            return URLDecoder.decode(oldString,"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oldString;
    }
}
