package com.example.elasticsearchdemo.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @Description: TODO 字符串工具
 * @author: jlsong E-mail: rainmap@163.com
 * @date: created in 下午2:31 19-7-1
 */
public class StringUtils {

    /**
     * json字符串转换成完美状态
     * @param jsonString
     * @return
     */
    public static String prettyJsonString(String jsonString){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(jsonString);
        return gson.toJson(jsonElement);
    }
}
