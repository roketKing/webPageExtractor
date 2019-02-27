package cn.edu.seu.webPageExtractor.graph.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class QueryGenerateUtil {

    public static String categoryUriGenerator(String categoryName,String kgName){
        StringBuilder result = new StringBuilder();
        result.append("<http://zhishi.me/");
        result.append(kgName);
        result.append("/category/");
        try {
            categoryName = URLEncoder.encode(categoryName,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        result.append(categoryName);
        result.append(">");
        return result.toString();
    }

    public static String propertyUriGenerator(String propertyName,String kgName){
        StringBuilder result = new StringBuilder();
        result.append("<http://zhishi.me/");
        result.append(kgName);
        result.append("/property/");
        try {
            propertyName = URLEncoder.encode(propertyName,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        result.append(propertyName);
        result.append(">");
        return result.toString();
    }
}
