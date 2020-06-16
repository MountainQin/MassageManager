package com.baima.massagemanager.util;

import android.content.ContentValues;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * LitePal数据 库的备份还原工具类
 * 要添加的依赖：Litepal,Gson
 */
public class BackupRetuceUtil {

    /**
     * 把指定类的数据 备份到指定路径
     * 备份文件里，类名对应数据库
     *
     * @param path
     * @param classes
     * @param <T>
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static <T extends LitePalSupport> void backup(String path, Class<T>[] classes) throws IOException, FileNotFoundException {
        Map map = new HashMap();
        for (Class<T> aClass : classes) {
            List<T> all = LitePal.findAll(aClass);
            map.put(aClass.getSimpleName(), all);
        }
        String json = new Gson().toJson(map);

        //如果 目录不存在先创建目录
        File folder = new File(path).getParentFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }


        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
            bufferedWriter.write(json);
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 把指定的备份文件还原到数据 库里
     * 在备份文件里，类名对应数据 表
     *
     * @param path
     * @param classes
     * @param <T>
     * @throws FileNotFoundException
     * @throws JSONException
     * @throws IOException
     */

    public static <T extends LitePalSupport> void retuce(String path, Class<T>[] classes) throws FileNotFoundException, JSONException, IOException {
        //读取备份文件
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            StringBuffer stringBuffer = new StringBuffer();

            int len = -1;
            char[] chars = new char[1024];
            while ((len = bufferedReader.read(chars)) != -1) {
                stringBuffer.append(chars, 0, len);
            }

            // 对应数据 库
            JSONObject jsonObject = new JSONObject(stringBuffer.toString());
            for (Class<T> aClass : classes) {
                //对应一个数据 表
                String simpleName = aClass.getSimpleName();
                JSONArray jsonArray = jsonObject.getJSONArray(simpleName);
                //删除原来的数据
                LitePal.deleteAll(aClass);
                //遍历对应数据 表，得到每条对应数据
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    //根据键得到字段 对应的值
                    Iterator<String> keys = jsonObject1.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = jsonObject1.getString(key);
                        //多出这个键值
                        if ("baseObjId".equals(key)) {
                            continue;
                        }
                        contentValues.put(key, value);
                    }
                    LitePal.getDatabase().insert(simpleName, "", contentValues);
                }
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
