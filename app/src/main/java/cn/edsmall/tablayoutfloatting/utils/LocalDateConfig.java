package cn.edsmall.tablayoutfloatting.utils;

import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.edsmall.tablayoutfloatting.model.BottomBar;

/**
 * 本地假数据
 */
public class LocalDateConfig {
    private static BottomBar sBottomBar;
    public  static BottomBar getsBottomBar(){
        if (sBottomBar==null){
            String content = parseFile("main_tabs_config.json");
            sBottomBar=  JSON.parseObject(content,BottomBar.class);
        }
        return sBottomBar;
    }
    private static String parseFile(String fileName) {
        AssetManager assets = AppGlobals.getApplication().getResources().getAssets();
        InputStream stream = null;
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            stream = assets.open(fileName);
            reader = new BufferedReader(new InputStreamReader(stream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }
}
