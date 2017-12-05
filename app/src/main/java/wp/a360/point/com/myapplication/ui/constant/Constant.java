package wp.a360.point.com.myapplication.ui.constant;

/**
 * Created by DN on 2017/12/5.
 */

public class Constant {

    //目录
    public static final String[] SETTINGS = {"一屏一换","清理缓存","退出应用"};
    //链接服务器获取数据的开关
    public static final  boolean isInter = false;
    //缓存数据的时间为10分钟
    public static final int maxAge = 60*10;
    //网络请求错误码
    public static final int ERROR = 99;
    //网络请求成功码
    public static final int RESULT = 100;
    /**网络请求地址*/
    public static class HttpConstants{
        private static final String URL_WEB_ZS = "http://120.24.75.75/GameAnswer";// 正式Ui
        private static final String URL_WEB_NW_W = "http://192.168.0.129:8088/AppStore"; // 本地服务器接口
        private static final String URL_Web = isInter ? URL_WEB_ZS: URL_WEB_NW_W;
        //获取首页数据集合接口
        public static final String getHomeData =  URL_Web+"/classify/getClassifyByClassify";
    }
    /**
     * 是否关闭吐司
     * true :打开，false:关闭
     */
    public static final boolean isShowToast = true;
    /**
     * 是否关闭打印的日志
     * true :打开，false:关闭
     */
    public static final boolean isShowLog = true;


}
