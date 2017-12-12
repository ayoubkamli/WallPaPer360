package wp.a360.point.com.myapplication.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import wp.a360.point.com.myapplication.R;
import wp.a360.point.com.myapplication.ui.base.BaseActivity;
import wp.a360.point.com.myapplication.ui.entity.DailySelect;
import wp.a360.point.com.myapplication.ui.widget.SVProgressHUD;

/**
 * Created by DN on 2017/12/6.
 */

public class WallPaperDetailsActivity extends BaseActivity {

    @ViewInject(R.id.pager)
    private ImageView mPager;
    @ViewInject(R.id.download_wallpaper)
    private ImageView download_wallpaper;
    @ViewInject(R.id.setting_wallpaper)
    private ImageView setting_wallpaper;
    @ViewInject(R.id.collection_unselect)
    private ImageView collection_unselect;
    @ViewInject(R.id.wallpaper_loding)
    private LinearLayout wallpaper_loding;
    @ViewInject(R.id.circleProgressBar)
    private AVLoadingIndicatorView circleProgressBar;
    //private List<DailySelect> listDailySelect;
    //private DailySelect dailySelect;
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
        }, 2000);
    }

    @Override
    public void doBusiness(final Context mContext) {
        wallpaper_loding.setVisibility(View.VISIBLE);
        circleProgressBar.setVisibility(View.VISIBLE);
        DailySelect dailySelect = (DailySelect) getIntent().getSerializableExtra("dailySelect");
        String imageUrl;
        if(dailySelect==null){
            imageUrl = getIntent().getStringExtra("topImageUrl");
        }else{
            imageUrl = dailySelect.getImageUrl();
        }
        //listDailySelect = (List<DailySelect>) getIntent().getSerializableExtra("listDailySelect");
        /*Picasso.with(mContext)
                .load(dailySelect.getImageUrl())
                .error(R.drawable.test2)
                //.resize(480,250)
                .centerCrop()
                .fit()
                .into(mPager);*/
            Glide.with(mContext)
                    .load(imageUrl)
                    //设置加载中图片
                    .placeholder(R.mipmap.lodinging) // can also be a drawable
                    //加载失败图片
                    .error(R.mipmap.lodinging)
                    //缓存源资源 result：缓存转换后的资源 none:不作任何磁盘缓存 all:缓存源资源和转换后的资源
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .thumbnail(0.2f) //设置缩略图支持
                    .fitCenter()
                    .into(mPager);
    }
    private long mkeyTime;
    @Override
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.download_wallpaper:
                showToast("下载了壁纸");
                break;
            case R.id.setting_wallpaper:
                showToast("设置了壁纸");
                break;
            case R.id.collection_unselect:
                showToast("收藏了壁纸");
                //collection_unselect.se
                break;
            case R.id.pager:
                    if ((System.currentTimeMillis() - mkeyTime) > 2000) {
                        mkeyTime = System.currentTimeMillis();
                        showToast("再按一次退出预览");
                    } else {
                        finish();
                    }
                break;
        }
    }
}
