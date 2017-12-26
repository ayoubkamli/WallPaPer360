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
import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTarget;
import com.arialyy.aria.core.download.DownloadTask;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wang.avi.AVLoadingIndicatorView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import wp.a360.point.com.myapplication.R;
import wp.a360.point.com.myapplication.ui.base.BaseActivity;
import wp.a360.point.com.myapplication.ui.constant.Constant;
import wp.a360.point.com.myapplication.ui.entity.DailySelect;
import wp.a360.point.com.myapplication.ui.utils.FileUtils;
import wp.a360.point.com.myapplication.ui.utils.NetworkUtils;
import wp.a360.point.com.myapplication.ui.utils.SharedPreferencesUtils;
import wp.a360.point.com.myapplication.ui.utils.XutilsHttp;
import wp.a360.point.com.myapplication.ui.widget.SVProgressHUD;

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
    private DailySelect dailySelect;
    private List<String> collection;
    private WallpaperManager mWallManager;
    private boolean isWallpaper = false;
    private String imageUrl;
    private int isCollection; //1：我的收藏界面，2：我的下载界面，
    private int posiotion;//主界面传递过来的position
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
        Aria.download(this).register();//将当前类注册到Aria，download的参数不能为上下文，否则注解的回调无用。
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
        collection = SharedPreferencesUtils.getInstance(mContext).getDownloadList("collection", String.class);

        dailySelect = (DailySelect) getIntent().getSerializableExtra("dailySelect");
        isCollection = getIntent().getIntExtra("isCollection", 0);
        posiotion = getIntent().getIntExtra("posiotion", 0);
        if(dailySelect==null){
            dailySelect = (DailySelect)getIntent().getSerializableExtra("topImageUrl");
        }
        if(dailySelect!=null){
            imageUrl = dailySelect.getImageUrl();
        }
        if(isCollection==1){
            collection_unselect.setVisibility(View.GONE);
        }else if(isCollection==2){
            collection_unselect.setImageDrawable(getResources().getDrawable(R.mipmap.livepaperico));
            download_wallpaper.setVisibility(View.GONE);

        }else{
            if(collection!=null){
                if(collection.contains(dailySelect.getImageUrl()+"")){
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
                    .load(imageUrl)
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
    private long mkeyTime;
    List<DailySelect> downloadImg;
    @Override
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.download_wallpaper:
                //showToast("下载了壁纸");
                String imageUrl = FileUtils.getDefaultDirectory(null) + FileUtils.getFileName(dailySelect.getImageUrl()); //下载文件的保存地址
                downloadImg = SharedPreferencesUtils.getInstance(mContext).getDownloadList("downloadImage", DailySelect.class);
                if(downloadImg==null){
                    downloadImg = new ArrayList<>();
                }
                if (NetworkUtils.isNetworkAvailable(mContext)) {
                    DownloadTarget target = Aria.download(mContext).load(dailySelect.getImageUrl());
                    if(target.isDownloading()){
                        showToast("正在下载中....");
                        return;
                    }
                    //判断文件是否存在
                    if(FileUtils.exists(imageUrl)){
                        //判断图片是否完整
                        if(!FileUtils.isIntact(imageUrl)){
                            //保存至数据库中
                            showToast("开始下载.可在我的壁纸查看!");
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
                            target.setDownloadPath(imageUrl).start();//开启下载
                        }else{
                            showToast("图片已经下载过");
                        }
                    }else{
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
                        showToast("开始下载.可在我的壁纸查看!");
                        target.setDownloadPath(imageUrl).start();//开启下载
                    }
                }
                break;
            case R.id.setting_wallpaper:
                SVProgressHUD.showWithStatus(mContext, "设置中...");
                if(mWallManager==null){
                    mWallManager =WallpaperManager.getInstance(this);
                }
                new Thread(){
                    @Override
                    public void run() {
                        setWallPaper(mWallManager);
                    }
                }.start();
                break;
            case R.id.collection_unselect:
                if(isCollection==2){
                    //删除
                    List<DailySelect> downloadImage = SharedPreferencesUtils.getInstance(mContext).getDownloadList("downloadImage", DailySelect.class);
                    boolean b = FileUtils.cleanFile(dailySelect.getImageUrl());
                    showLog("详情页面删除信息："+b);
                    ArrayList<DailySelect> list = new ArrayList<>();
                    for(DailySelect d :downloadImage){
                        if(d.getImageID()!=dailySelect.getImageID()){
                            list.add(d);
                        }
                    }
                    SharedPreferencesUtils.getInstance(mContext).setDownloadList("downloadImage",list,false);
                    //发送广播更新我的下载界面
                    Intent mIntent = new Intent(Constant.DOWNLOAD_ACTION);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent);
                    finish();
                }else{
                    if(collection==null){
                        //添加收藏
                        collection_unselect.setImageDrawable(getResources().getDrawable(R.mipmap.collection_slecet));
                        collection = new ArrayList<>();
                        dailySelect.setCollectionNumber(dailySelect.getCollectionNumber()+1);
                        collection.add(dailySelect.getImageUrl()+"");
                        SharedPreferencesUtils.getInstance(mContext).setDownloadList("collection",collection,false);
                        upCollection(dailySelect,Constant.Collection.COLLECTION_TYPE_ADD);
                        //发送广播更新主界面的信息
                        Intent mIntent = new Intent(Constant.COLLECTION_ACTION);
                        mIntent.putExtra("position",posiotion);
                        mIntent.putExtra("clickNumber",(dailySelect.getCollectionNumber()));
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent);
                    }else{
                        if(collection.contains(dailySelect.getImageID()+"")){
                            showToast("你已经添加过收藏了！");
                        }else{
                            //添加收藏
                            collection_unselect.setImageDrawable(getResources().getDrawable(R.mipmap.collection_slecet));
                            collection_unselect.setImageDrawable(getResources().getDrawable(R.mipmap.collection_slecet));
                            dailySelect.setCollectionNumber(dailySelect.getCollectionNumber()+1);
                            collection.add(dailySelect.getImageUrl()+"");
                            SharedPreferencesUtils.getInstance(mContext).setDownloadList("collection",collection,false);
                            upCollection(dailySelect,Constant.Collection.COLLECTION_TYPE_ADD);
                            //发送广播更新主界面的信息
                            Intent mIntent = new Intent(Constant.COLLECTION_ACTION);
                            mIntent.putExtra("position",posiotion);
                            mIntent.putExtra("clickNumber",(dailySelect.getCollectionNumber()));
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent);
                        }
                    }
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

    private Handler mHadnler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    if(SVProgressHUD.isShowing(mContext)){
                        SVProgressHUD.dismiss(mContext);
                    }
                    String message = (String) msg.obj;
                    showToast(message.toString()+"");
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
                            if(collectionType==Constant.Collection.COLLECTION_TYPE_ADD){
                                //showToast("添加收藏");
                            }else{
                                //showToast("取消收藏");
                            }
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

    /*** 下载完成监听*/
    @Download.onTaskComplete
    public void onTaskComplete(DownloadTask task) {
        if(task.getKey().equals(dailySelect.getImageUrl())){
            if(isWallpaper){
                Bitmap bitmap = BitmapFactory.decodeFile(task.getDownloadPath());
                if(bitmap!=null){
                    try {
                        mWallManager.setBitmap(bitmap);
                        if(!bitmap.isRecycled()){
                            bitmap.recycle();
                            bitmap=null;
                        }
                        Message msg = new Message();
                        msg.what=0;
                        msg.obj = "设置壁纸成功！";
                        mHadnler.sendMessage(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Message msg = new Message();
                        msg.what=0;
                        msg.obj = "设置壁纸失败！";
                        mHadnler.sendMessage(msg);
                    }
                }
            }else{
                if(isCollection==1){
                    //广播通知下载界面更新
                    Intent mIntent = new Intent(Constant.DOWNLOAD_ACTION);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent);
                }
                Message msg = new Message();
                msg.what=0;
                msg.obj = "下载完成！可在我的壁纸中查看！";
                mHadnler.sendMessage(msg);
            }

        }
    }

    /**
     * 设置壁纸
     * @param wallPaper
     */
    public void setWallPaper(WallpaperManager wallPaper) {
        try{
            String imageUrl1 = FileUtils.getDefaultDirectory(null) + FileUtils.getFileName(dailySelect.getImageUrl());
            if(!FileUtils.isIntact(imageUrl1)){  //说明没有下载，先去下载
                downloadImg = SharedPreferencesUtils.getInstance(mContext).getDownloadList("downloadImage", DailySelect.class);
                if(downloadImg==null){
                    downloadImg = new ArrayList<>();
                }
                if(!downloadImg.contains(dailySelect)){
                    downloadImg.add(dailySelect);
                }
                SharedPreferencesUtils.getInstance(mContext).setDownloadList("downloadImage",downloadImg,false);
                isWallpaper = true;
                Aria.download(mContext).load(dailySelect.getImageUrl()).setDownloadPath(imageUrl1).start();
                //开启线程检测是否下载完成
                //checkThreed = new CheckThreed(wallPaper,imageUrl1);
            }else{
                Bitmap bitmap = BitmapFactory.decodeFile(imageUrl1);
                if(bitmap!=null){
                    wallPaper.setBitmap(bitmap);
                    if(!bitmap.isRecycled()){
                        bitmap.recycle();
                        bitmap=null;
                    }
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = "设置壁纸成功！";
                    mHadnler.sendMessage(msg);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            if(SVProgressHUD.isShowing(mContext)){
                SVProgressHUD.dismiss(mContext);
            }
            showToast("设置失败！");
            showLog("设置壁纸失败信息："+e.getMessage().toString());
        }
    }


}
