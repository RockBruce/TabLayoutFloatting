package cn.edsmall.tablayoutfloatting.utils;

import android.content.res.AssetManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import cn.edsmall.tablayoutfloatting.model.BottomBar;
import cn.edsmall.tablayoutfloatting.model.Destination;

public class AppConfig {
    private static HashMap<String, Destination> sDestConfig;
   private static BottomBar sBottomBar;
    public static HashMap<String, Destination> getDestConfig() {
        if (sDestConfig == null) {
            String parseFile = parseFile("destnation.json");
            sDestConfig=  JSON.parseObject(parseFile,new TypeReference<HashMap<String,Destination>>(){}.getType());
        }
        return sDestConfig;
    }
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
