package wp.a360.point.com.myapplication.ui.utils;

import android.content.Context;
import android.util.ArrayMap;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by DN on 2017/8/24.
 * okhttp3 网络请求工具类
 *
 * maxAge与maxStale的区别：
 * maxAge：超出了maxAge，缓存会被清除，回调onFailure
 *maxStale：超过过时时间StaleTime的话，不删除缓存。
 *
 */

public class OkHttpUtils {

    private static  OkHttpClient client = null;
    private static OkHttpClient.Builder builder;
    private static final long cacheSize = 1024*1024*40;//缓存文件最大限制大小40M
    private static String cachedirectory = FileUtils.getAbsolutePath() + "/360wallpaper";  //设置缓存文件路径
    private static Cache cache = new Cache(new File(cachedirectory), cacheSize);  //
    private static Context context;

    /**由于okhttp3不建议创建多个对象，所以采用饿汉式的单例模式*/
    public static OkHttpClient getInstance(Context c) {
        context = c;
        if (client == null) {
            synchronized (OkHttpUtils.class) {
                if (client == null){
                    builder = new OkHttpClient.Builder();
                    builder.addInterceptor(interceptor);//添加日志拦截器
                    builder.addNetworkInterceptor(interceptor);//服务器不支持缓存的情况添加缓存拦截器
                    builder.connectTimeout(8, TimeUnit.SECONDS);  //设置连接超时时间
                    builder.writeTimeout(8, TimeUnit.SECONDS);//设置写入超时时间
                    builder.readTimeout(8, TimeUnit.SECONDS);//设置读取数据超时时间
                    builder.retryOnConnectionFailure(false);//设置不进行连接失败重试
                    builder.cache(cache);//这种缓存
                    client = builder.build();
                }
            }
        }
        return client;
    }
    //拦截器
    private static final Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if(NetworkUtils.getAPNType(context)<0){
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response response = chain.proceed(request);
            if (NetworkUtils.getAPNType(context)>0) {//有网络，缓存半小时
                int maxAge = 60*1; // read from cache for 1 minute
                response.newBuilder()
                        .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {//无网络，缓存1小时
                int maxStale = 60*60;//tolerate 4-weeks stale
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        }
    };

    /**接口的参数拼接*/
    private static String getPlayPath(ArrayMap<String,String> map, String playPath){
        if(null==map&&map.equals(""))return playPath;//参数为空，直接返回地址
        StringBuffer sb = new StringBuffer();
        sb.append(playPath);
        sb.append("?");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();// 网格控件中KEY(5)
            String value = entry.getValue();// 网格控件中的value(?)注:是从你点击中传过来的
            sb.append(key).append("=").append(value).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        String entity = sb.toString();
        return entity;
    }


    /**
     *  maxStale Get请求异步(缓存)
     *@param staletime 过时时间，秒为单位
     * @param url 请求地址
     * @param callback 回调
     */
    public static void doStaleTimeGet(Context context, String url, ArrayMap<String, String> mapParams, int staletime,Callback callback) {
        String path = getPlayPath(mapParams, url);//拼接参数，获取地址
        Request request = new Request.Builder()
            //.cacheControl(new CacheControl.Builder().maxStale(staletime, TimeUnit.SECONDS).build())//超过过时时间不会删除缓存
            .cacheControl(new CacheControl.Builder().maxAge(staletime, TimeUnit.SECONDS).build())//超过过时时间会删除缓存
            .url(path)
            .build();
        Call call = getInstance(context).newCall(request);
        call.enqueue(callback);
    }

    /**
     * Post请求发送键值对数据(无缓存)
     * @param url
     * @param mapParams
     * @param callback
     */
    public static void doPost(Context context, String url, ArrayMap<String, String> mapParams, Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : mapParams.keySet()) {
            builder.add(key, mapParams.get(key));
        }
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        Call call = getInstance(context).newCall(request);
        call.enqueue(callback);
    }

    /**
     * 普通Get请求(无参数,无缓存)
     * @param url
     * @param callback
     */
    public static void doGet(Context context,String url,Callback callback) {
        Request request = new Request.Builder()
                //.cacheControl(new CacheControl.Builder().maxAge(0, TimeUnit.SECONDS).build())
                .url(url)
                .build();
        Call call = getInstance(context).newCall(request);
        call.enqueue(callback);
    }

    /**
     * 普通Get请求(无缓存)
     * @param url
     * @param callback
     */
    public static void doUnGet(Context context,String url, ArrayMap<String, String> mapParams,Callback callback) {
        String path = getPlayPath(mapParams, url);//拼接参数，获取地址
        Request request = new Request.Builder()
                //.cacheControl(new CacheControl.Builder().maxAge(0, TimeUnit.SECONDS).build())
                .url(path)
                .build();
        Call call = getInstance(context).newCall(request);
        call.enqueue(callback);
    }

    /**
     * maxAge Get同步请求
     * @param context //x上下文
     * @param url 请求地址
     * @param mapParams 请求参数
     * *get请求，并缓存请求数据，返回的是缓存数据，注意，如果超出了maxAge，缓存会被清除，回调onFailure
     */
    public static Call doMaxAgeExeGet(Context context,String url, ArrayMap<String, String> mapParams) {
        String path = getPlayPath(mapParams, url);//拼接参数，获取地址
        //创建缓存文件
        Request request = new Request.Builder()
                .url(path)
                .build();
        final Call call = getInstance(context).newCall(request);
        return call;
    }

    /**与下载有关*/
    /**
     * @param url        下载链接
     * @param startIndex 下载起始位置
     * @param endIndex   结束为止
     * @param callback   回调
     * @throws IOException
     */
    public void downloadFileByRange(String url, long startIndex, long endIndex, Callback callback) throws IOException {
        // 创建一个Request
        // 设置分段下载的头信息。 Range:做分段数据请求,断点续传指示下载的区间。格式: Range bytes=0-1024或者bytes:0-1024
        Request request = new Request.Builder().header("RANGE", "bytes=" + startIndex + "-" + endIndex)
                .url(url)
                .build();
        doAsync(request, callback);
    }

    public void getContentLength(String url, Callback callback) throws IOException {
        // 创建一个Request
        Request request = new Request.Builder()
                .url(url)
                .build();
        doAsync(request, callback);
    }
    /**
     * 异步请求
     */
    private void doAsync(Request request, Callback callback) throws IOException {
        //创建请求会话
        Call call = client.newCall(request);
        //同步执行会话请求
        call.enqueue(callback);
    }

    /**
     * 同步请求
     */
    private Response doSync(Request request) throws IOException {

        //创建请求会话
        Call call = client.newCall(request);
        //同步执行会话请求
        return call.execute();
    }




}
