package wp.a360.point.com.myapplication.ui.constant;

import android.os.Environment;

import java.io.File;

/**
 * Created by DN on 2017/12/5.
 */

public class Constant {

    //目录
    public static final String[] SETTINGS = {"清理缓存","退出应用"};
    //链接服务器获取数据的开关
    public static final  boolean isInter = true;
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

    //缓存数据的时间为10分钟
    public static final int maxAge = 60*10;
    //网络请求错误码
    public static final int ERROR = 99;
    //网络请求成功码
    public static final int RESULT = 100;

    public static final  int  PERMISSION_CODE = 100;
    public static final  int  REQUEST_CODE_SETTING = 100;

    public static final  String COLLECTION_ACTION = "clickCollection";
    public static final  String DOWNLOAD_ACTION = "downloadCollection";

    public  static  final  String clearImagePath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "360wallpaper";
    /**
     * 增，减 收藏图片的标识
     */
    public static  class Collection{
        public  static final  int COLLECTION_TYPE_ADD = 1;
        public  static final  int COLLECTION_TYPE_CANCEL = 2;
    }
    /**
     * 我的壁纸tab
     */
    public static class MyTabs{
        public static final String DOWNLOAD_WALLLPAPER = "已下载";
        public static final String COLLECTION_WALLLPAPER = "已收藏";
    }


    /**网络请求地址*/
    public static class HttpConstants{
        private static final String URL_WEB_ZS = "http://120.24.152.185/WallPaper";// 正式Ui
        private static final String URL_WEB_NW_W = "http://192.168.0.127:8088/WallPaper"; // 本地服务器接口
        private static final String URL_Web = isInter ? URL_WEB_ZS: URL_WEB_NW_W;
        public static final String pageNum = "start";//参数 分页:从第几条开始拿
        public static final String pageSize = "num"; //参数 拿多少条
        public static final String pageType = "type"; //参数 拿多少条
        public static final String imageTypeID = "imageTypeID"; //图片类型ID
        public static final String imageLabel = "imageLabel"; //参数 标签
        public static final String imageID = "imageID"; //参数 图片ID
        public static final String type = "type"; //参数 1：标识收藏  2：标识取消收藏
        public static final  String appName = "appName";
        /**
         * 获取首页数据集合接口
         * */
        public static final String getHomeData =  URL_Web+"/image/findAllImage";

        /**
         * 获取分类界面数据集合接口
         * http://192.168.0.129:8088/WallPaper/ImageType/findAllImageType
         * 分类二级接口
         * http://192.168.0.129:8088/WallPaper/image/findImageByimageTypeID
         * */
        public static final String getTypeData =  URL_Web+"/ImageType/findAllImageType";
        public static final String getTypeDetails =  URL_Web+"/image/findImageByimageTypeID";

        /**
         * 获取搜索界面数据集合接口
         * 关键词集合
         * http://192.168.0.129:8088/WallPaper/keyword/findRandomKeyWord
         * 搜索结果
         * http://192.168.0.129:8088/WallPaper/image/findDimImage
         * */
        public static final String getSearchKeyWord =  URL_Web+"/keyword/findRandomKeyWord";
        public static final String getSearchResult =  URL_Web+"/image/findDimImage";

        /**
         * 收藏接口
         * http://192.168.0.129:8088/WallPaper/image/updateImageCollectionNumber
         */
        public static final String collectionImage =  URL_Web+"/image/updateImageCollectionNumber";


    }


}
