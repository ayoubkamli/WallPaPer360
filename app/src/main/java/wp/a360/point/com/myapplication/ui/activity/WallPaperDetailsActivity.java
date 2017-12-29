package wp.a360.point.com.myapplication.ui.activity;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.ArrayMap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wang.avi.AVLoadingIndicatorView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.wp.point.qj.jb.R;
import wp.a360.point.com.myapplication.ui.base.BaseActivity;
import wp.a360.point.com.myapplication.ui.constant.Constant;
import wp.a360.point.com.myapplication.ui.entity.DailySelect;
import wp.a360.point.com.myapplication.ui.utils.FileUtils;
import wp.a360.point.com.myapplication.ui.utils.NetworkUtils;
import wp.a360.point.com.myapplication.ui.utils.SharedPreferencesUtils;
import wp.a360.point.com.myapplication.ui.utils.XutilsHttp;
/**
 * Created by DN on 2017/12/6.
 */

public class WallPaperDetailsActivity extends BaseActivity {

    @ViewInject(R.id.details_image)
    private ImageView mPager;
    @ViewInject(R.id.download_wallpaper)
    private ImageView download_wallpaper;
    @ViewInject(R.id.setting_wallpaper)
    private ImageView setting_wallpaper;
    @ViewInject(R.id.collection_unselect)
    private ImageView collection_unselect;
    @ViewInject(R.id.wallpaper_loding)
    private LinearLayout wallpaper_loding;
    @ViewInject(R.id.circleProgressbar)
    private AVLoadingIndicatorView circleProgressBar;
    private ArrayMap<String,DailySelect> collection;
    private List<DailySelect> downloadImg;
    private long mkeyTime;
    private WallpaperManager mWallManager;
    private boolean isWallpaper = false;
    private DailySelect dailySelect;
    private int isCollection; //1：我的收藏界面，2：我的下载界面，
    private int posiotion;//主界面传递过来的position
    private boolean isDownload = false;//设置壁纸，是否需要先下载
    private String iamgePaht; //图片保存路径
    private static  String DOWNLOAD_IMAGE = "";

    @Override
    public void initParms(Bundle parms) {
        setAllowFullScreen(true);
        setScreenRoate(false);
        setSteepStatusBar(false);
        setSetActionBarColor(true, R.color.white);// 设置状态栏颜色
    }
    @Override
    public View bindView() {
        return null;
    }
    @Override
    public int bindLayout() {
        return R.layout.wallpaper_details_activity;
    }
    @Override
    public void initView(View view) {
        x.view().inject(this);
    }
    @Override
    public void setListener() {
        download_wallpaper.setOnClickListener(this);
        setting_wallpaper.setOnClickListener(this);
        collection_unselect.setOnClickListener(this);
        mPager.setOnClickListener(this);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                wallpaper_loding.setVisibility(View.GONE);
                circleProgressBar.setVisibility(View.GONE);
            }
        }, 1500);
    }
    @Override
    public void doBusiness(final Context mContext) {
        wallpaper_loding.setVisibility(View.VISIBLE);
        circleProgressBar.setVisibility(View.VISIBLE);
        collection = SharedPreferencesUtils.getInstance(mContext).getHashMapData("collection", DailySelect.class);
        dailySelect = (DailySelect) getIntent().getSerializableExtra("dailySelect");
        isCollection = getIntent().getIntExtra("isCollection", 0);
        posiotion = getIntent().getIntExtra("posiotion", 0);
        if(dailySelect==null){
            dailySelect = (DailySelect) getIntent().getSerializableExtra("topImageUrl");
        }
        //获取下载的图片地址
        DOWNLOAD_IMAGE = dailySelect.getImageUrl();
        //成图片保存路径
        iamgePaht = FileUtils.getDefaultDirectory(null) + FileUtils.getFileName(dailySelect.getImageUrl());
        if(isCollection==1){
            collection_unselect.setVisibility(View.GONE);
        }else if(isCollection==2){
            collection_unselect.setImageDrawable(getResources().getDrawable(R.mipmap.livepaperico));
            download_wallpaper.setVisibility(View.GONE);
        }else{
            if(collection!=null){
                if(collection.containsKey(dailySelect.getImageID()+"")){
                    collection_unselect.setImageDrawable(getResources().getDrawable(R.mipmap.collection_slecet));
                }else{
                    collection_unselect.setImageDrawable(getResources().getDrawable(R.mipmap.collection_unselect));
                }
            }else{
                collection_unselect.setImageDrawable(getResources().getDrawable(R.mipmap.collection_unselect));
            }
        }
        try{
            Glide.with(mContext)
                    .load(dailySelect.getImageUrl())
                    //设置加载中图片
                    .placeholder(R.mipmap.lodinging) // can also be a drawable
                    //加载失败图片
                    .error(R.mipmap.lodinging)
                    //缓存源资源 result：缓存转换后的资源 none:不作任何磁盘缓存 all:缓存源资源和转换后的资源
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .thumbnail(1f) //设置缩略图支持
                    .fitCenter()
                    .into(mPager);
        }catch (Exception ex){
            ex.printStackTrace();
            showLog(ex.getMessage().toString()+"");
            showToast("加载失败，请稍后尝试..");
        }

    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.download_wallpaper:
                downloadWallpaper(); //下载壁纸
                break;
            case R.id.setting_wallpaper:
                settingWallpaper(); //设置壁纸
                break;
            case R.id.collection_unselect:
                if(isCollection==2){
                    //删除
                    deleteDownloadWallpaper(); //删除下载的壁纸
                }else{
                    collectionWallpaper(); //收藏壁纸
                }
                break;
            case R.id.details_image:
                if ((System.currentTimeMillis() - mkeyTime) > 2000) {
                    mkeyTime = System.currentTimeMillis();
                    showToast("再按一次退出预览");
                } else {
                    finish();
                }
                break;
        }
    }

    /**
     * 收藏壁纸
     */
    private void collectionWallpaper() {
        if(collection==null){
            //添加收藏
            collection_unselect.setImageDrawable(getResources().getDrawable(R.mipmap.collection_slecet));
            collection = new ArrayMap<>();
            dailySelect.setCollectionNumber(dailySelect.getCollectionNumber()+1);
            collection.put(dailySelect.getImageID()+"",dailySelect);
            SharedPreferencesUtils.getInstance(mContext).putHashMapData("collection",collection);
            upCollection(dailySelect,Constant.Collection.COLLECTION_TYPE_ADD);
            //发送广播更新主界面的信息
            Intent mIntent = new Intent(Constant.COLLECTION_ACTION);
            mIntent.putExtra("position",posiotion);
            mIntent.putExtra("clickNumber",dailySelect.getCollectionNumber());
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent);
        }else{
            if(collection.containsKey(dailySelect.getImageID()+"")){
                showToast("你已经添加过收藏了！");
            }else{
                //添加收藏
                collection_unselect.setImageDrawable(getResources().getDrawable(R.mipmap.collection_slecet));
                collection_unselect.setImageDrawable(getResources().getDrawable(R.mipmap.collection_slecet));
                dailySelect.setCollectionNumber(dailySelect.getCollectionNumber()+1);
                collection.put(dailySelect.getImageID()+"",dailySelect);
                SharedPreferencesUtils.getInstance(mContext).putHashMapData("collection",collection);
                upCollection(dailySelect,Constant.Collection.COLLECTION_TYPE_ADD);
                //发送广播更新主界面的信息
                Intent mIntent = new Intent(Constant.COLLECTION_ACTION);
                mIntent.putExtra("position",posiotion);
                mIntent.putExtra("clickNumber",dailySelect.getCollectionNumber());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent);
            }
        }
    }

    /**
     * 删除下载的壁纸
     */
    private void deleteDownloadWallpaper() {
        List<DailySelect> downloadImage = SharedPreferencesUtils.getInstance(mContext).getDownloadList("downloadImage", DailySelect.class);
        boolean b = FileUtils.cleanFile(dailySelect.getImageUrl());
        showLog("详情页面删除信息："+b);
        List<DailySelect> list = new ArrayList<>();
        for(DailySelect d :downloadImage){
            if(d.getImageID()!=dailySelect.getImageID()){
                list.add(d);
            }
        }
        SharedPreferencesUtils.getInstance(MyAppLication.context).setDownloadList("downloadImage",list,false);
        //发送广播更新我的下载界面
        Intent mIntent = new Intent(Constant.DOWNLOAD_ACTION);
        LocalBroadcastManager.getInstance(MyAppLication.context).sendBroadcast(mIntent);
        finish();
    }

    /**
     * 设置壁纸
     */
    private void settingWallpaper() {
        if(mWallManager==null){
            mWallManager =WallpaperManager.getInstance(MyAppLication.context);
        }
        if(!FileUtils.isIntact(iamgePaht)) {  //说明没有下载，先去下载
            downloadImg = SharedPreferencesUtils.getInstance(MyAppLication.context).getDownloadList("downloadImage", DailySelect.class);
            if(downloadImg==null){
                downloadImg = new ArrayList<>();
            }
            boolean isContains = false;
            for(DailySelect dailySelect1:downloadImg){
                if(dailySelect1.getImageID()==dailySelect.getImageID()){
                    isContains = true;
                    break;
                }
            }
            if(!isContains){
                downloadImg.add(dailySelect);
            }
            SharedPreferencesUtils.getInstance(MyAppLication.context).setDownloadList("downloadImage",downloadImg,false);
            isWallpaper = true;
            isDownload = true;
            Toast.makeText(MyAppLication.context,"开始下载..",Toast.LENGTH_SHORT).show();
        }else{
            isDownload = false;
            Toast.makeText(MyAppLication.context,"设置中...",Toast.LENGTH_SHORT).show();
        }
        try{
            setWallPaper(mWallManager,iamgePaht,isDownload);
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(MyAppLication.context,ex.getMessage().toString()+"",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 下载壁纸
     */
    private void downloadWallpaper() {
        downloadImg = SharedPreferencesUtils.getInstance(mContext).getDownloadList("downloadImage", DailySelect.class);
        if(downloadImg==null){
            downloadImg = new ArrayList<>();
        }
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            //判断文件是否存在
            if(FileUtils.exists(iamgePaht)){
                //判断图片是否完整
                if(!FileUtils.isIntact(iamgePaht)){
                    //保存至数据库中
                    Toast.makeText(MyAppLication.context,"开始下载...",Toast.LENGTH_SHORT).show();
                    boolean isContains = false;
                    for(DailySelect dailySelect1:downloadImg){
                        if(dailySelect1.getImageID()==dailySelect.getImageID()){
                            isContains = true;
                            break;
                        }
                    }
                    if(!isContains){
                        downloadImg.add(dailySelect);
                    }
                    SharedPreferencesUtils.getInstance(mContext).setDownloadList("downloadImage",downloadImg,false);
                    new Thread(){
                        @Override
                        public void run() {
                            boolean b = FileUtils.downloadImage(DOWNLOAD_IMAGE, iamgePaht);
                            String message = "";
                            if(b){
                                message = "下载成功！";
                            }else{
                                message = "下载失败！";
                            }
                            Message msg = new Message();
                            msg.what=0;
                            msg.obj = message;
                            mHadnler.sendMessage(msg);
                        }
                    }.start();
                }else{
                    Toast.makeText(MyAppLication.context,"图片已经下载过",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(MyAppLication.context,"下载中...",Toast.LENGTH_SHORT).show();
                boolean isContains = false;
                for(DailySelect dailySelect1:downloadImg){
                    if(dailySelect1.getImageID()==dailySelect.getImageID()){
                        isContains = true;
                        break;
                    }
                }
                if(!isContains){
                    downloadImg.add(dailySelect);
                }
                SharedPreferencesUtils.getInstance(mContext).setDownloadList("downloadImage",downloadImg,false);
                new Thread(){
                    @Override
                    public void run() {
                        boolean b = FileUtils.downloadImage(DOWNLOAD_IMAGE, iamgePaht);
                        String message = "";
                        if(b){
                            message = "下载成功！";
                        }else{
                            message = "下载失败！";
                        }
                        Message msg = new Message();
                        msg.what=0;
                        msg.obj = message;
                        mHadnler.sendMessage(msg);
                    }
                }.start();


            }
        }else{
            Toast.makeText(MyAppLication.context,"请检查您的网络是否连接正常",Toast.LENGTH_SHORT).show();
        }
    }


    private Handler mHadnler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0://下载壁纸
                    try{
                        String message = (String) msg.obj;
                        showToast(message);
                        if(isCollection==1){
                            //广播通知下载界面更新
                            Intent mIntent = new Intent(Constant.DOWNLOAD_ACTION);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent);
                        }else if(isCollection==2){
                            //广播通知下载界面更新
                            Intent mIntent = new Intent(Constant.DOWNLOAD_ACTION);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent);
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                        Toast.makeText(MyAppLication.context,ex.getMessage().toString()+"",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:  //设置壁纸（未下载的情况）
                    try {
                        boolean message1 = (boolean) msg.obj;
                        if(message1){
                            Toast.makeText(MyAppLication.context,"下载成功！",Toast.LENGTH_SHORT).show();
                            Bitmap bitmap = BitmapFactory.decodeFile(iamgePaht);
                            if(bitmap!=null){
                                mWallManager.setBitmap(bitmap);
                                if(!bitmap.isRecycled()){
                                    bitmap.recycle();
                                    bitmap=null;
                                }
                                Toast.makeText(MyAppLication.context,"设置成功！",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(MyAppLication.context,"下载失败！",Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MyAppLication.context,"设置失败！",Toast.LENGTH_SHORT).show();
                    }catch (Exception ex){
                        ex.printStackTrace();
                        Toast.makeText(MyAppLication.context,ex.getMessage().toString()+"",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    try{
                        String message2 = (String) msg.obj;
                        Toast.makeText(MyAppLication.context,message2+"",Toast.LENGTH_SHORT).show();
                    }catch (Exception ex){
                        Toast.makeText(MyAppLication.context,ex.getMessage().toString()+"",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }


        }
    };
    /**修改服务器上面的收藏数量
     * */
    private void upCollection(DailySelect dailySelect, final int collectionType) {
        String collectionUrl = Constant.HttpConstants.collectionImage;
        ArrayMap<String,String> am = new ArrayMap<>();
        am.put(Constant.HttpConstants.imageID,dailySelect.getImageID()+"");
        am.put(Constant.HttpConstants.type,collectionType+"");
        XutilsHttp.xUtilsPost(collectionUrl, am, new XutilsHttp.XUilsCallBack() {
            @Override
            public void onResponse(final String result) {
                mHadnler.post(new Runnable() {
                    @Override
                    public void run() {
                        showLog("收藏结果信息"+result+"");
                        if(result.equals("yes")){
                            return;
                        }showToast("收藏失败！");
                    }
                });
            }

            @Override
            public void onFail(final String result) {
                mHadnler.post(new Runnable() {
                    @Override
                    public void run() {
                        showToast("收藏失败");
                        showLog("收藏失败信息："+result.toString());
                    }
                });
            }
        });
    }

    /**
     * 设置壁纸
     * @param wallPaper
     */
    public void setWallPaper(final WallpaperManager wallPaper, final String imagePaht, boolean isDownload) {
        try{
            if(isDownload){  //没有下载，先去下载
                new Thread(){
                    @Override
                    public void run() {
                        boolean message = FileUtils.downloadImage(DOWNLOAD_IMAGE, iamgePaht);
                        Message msg = new Message();
                        msg.what=1;
                        msg.obj = message;
                        mHadnler.sendMessage(msg);
                    }
                }.start();
            }else{
                new Thread(){
                    @Override
                    public void run() {
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePaht);
                        if(bitmap!=null){
                            try {
                                wallPaper.setBitmap(bitmap);
                                if(!bitmap.isRecycled()){
                                    bitmap.recycle();
                                    bitmap=null;
                                }
                                Message msg = new Message();
                                msg.what=2;
                                msg.obj = "设置壁纸成功！";
                                mHadnler.sendMessage(msg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                }.start();

            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(WallPaperDetailsActivity.this,e.getMessage().toString()+"",Toast.LENGTH_SHORT).show();
        }
    }


}
