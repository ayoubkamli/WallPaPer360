package wp.a360.point.com.myapplication.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArrayMap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 
 * SharedPreferences的一个工具类，调用setParam就能保存String, Integer, Boolean, Float, Long类型的参数 
 * 同样调用getParam就能获取到保存在手机里面的数据 
 *
 * 
 */  
public class SharedPreferencesUtils {
    /**
     * 保存在手机里面的文件名 
     */  
    private static final String FILE_NAME = "share_date"; //可自行修改
    private static SharedPreferences.Editor editor;
    private static SharedPreferences sp;
    private static SharedPreferencesUtils spu;
    public SharedPreferencesUtils(Context context){
         sp = context.getApplicationContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
         editor = sp.edit();
    }
    //单例模式
    public static SharedPreferencesUtils getInstance(Context context) {
        if (spu ==null){
            spu =new SharedPreferencesUtils(context.getApplicationContext());
        }
        return  spu;
    }

    /** 
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法 
     * @param key
     * @param object  
     */  
    public void setParam( String key, Object object){
        String type = object.getClass().getSimpleName();
        if("String".equals(type)){
            editor.putString(key, (String)object);  
        }  
        else if("Integer".equals(type)){  
            editor.putInt(key, (Integer)object);  
        }  
        else if("Boolean".equals(type)){  
            editor.putBoolean(key, (Boolean)object);  
        }  
        else if("Float".equals(type)){  
            editor.putFloat(key, (Float)object);  
        }  
        else if("Long".equals(type)){  
            editor.putLong(key, (Long)object);  
        }  
        editor.commit();
    }  
      
      
    /** 
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值 
     * @param key
     * @param defaultObject 
     * @return 
     */  
    public Object getParam(String key, Object defaultObject){
        String type = defaultObject.getClass().getSimpleName();  

        if("String".equals(type)){  
            return sp.getString(key, (String)defaultObject);  
        }  
        else if("Integer".equals(type)){  
            return sp.getInt(key, (Integer)defaultObject);  
        }  
        else if("Boolean".equals(type)){  
            return sp.getBoolean(key, (Boolean)defaultObject);  
        }  
        else if("Float".equals(type)){  
            return sp.getFloat(key, (Float)defaultObject);  
        }  
        else if("Long".equals(type)){  
            return sp.getLong(key, (Long)defaultObject);  
        }  
          
        return null;  
    } 


    /**
     * 用于保存集合
     *
     * @param key key
     * @param map map数据
     * @return 保存结果
     */
    public  <K, V> boolean putHashMapData(String key, ArrayMap<K, V> map) {
        boolean result;
        try {
            Gson gson = new Gson();
            String json = gson.toJson(map);
            editor.putString(key, json);
            result = true;
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        editor.apply();
        return result;
    }

    /**
     * 用于获取保存的集合
     *
     * @param key key
     * @return HashMap
     */
    public  <V> ArrayMap<String, V> getHashMapData(String key, Class<V> clsV) {
        try{
            String json = sp.getString(key, "");
            if(json==null){return null;}
            ArrayMap<String, V> map = new ArrayMap<>();
            Gson gson = new Gson();
            JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet = obj.entrySet();
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                String entryKey = entry.getKey();
                JsonObject value = (JsonObject) entry.getValue();
                map.put(entryKey, gson.fromJson(value, clsV));
            }
            //Log.e("SharedPreferencesUtil", obj.toString());
            return map;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存List
     * @param tag
     * @param datalist
     */
    public  <T> boolean setDataList(String tag, List<T> datalist) {
        try{
            if (null == datalist || datalist.size() <= 0)
                return false;
            Gson gson = new Gson();
            //转换成json数据，再保存
            String strJson = gson.toJson(datalist);
            editor.clear();
            editor.putString(tag, strJson);
            editor.commit();
            return true;
        }catch (Exception ex ){
            Log.i("保存pag异常信息>>>",ex.toString()+"");
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 获取List
     * @param tag
     * @return
     */
    public <T> List<T> getDataList(String tag) {
        try{
            List<T> datalist=new ArrayList<T>();
            String strJson = sp.getString(tag, null);
            if (null == strJson) {
                return datalist;
            }
            Gson gson = new Gson();
            datalist = gson.fromJson(strJson, new TypeToken<List<T>>() {
            }.getType());
            return datalist;
        }catch (Exception ex){
            Log.i("获取pag异常信息>>>",ex.toString()+"");
            ex.printStackTrace();
            return null;
        }


    }





}
