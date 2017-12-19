package wp.a360.point.com.myapplication.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import wp.a360.point.com.myapplication.R;
import wp.a360.point.com.myapplication.ui.base.BaseFragmentActivity;
import wp.a360.point.com.myapplication.ui.constant.Constant;
import wp.a360.point.com.myapplication.ui.fragment.CollectionFragment;
import wp.a360.point.com.myapplication.ui.fragment.DownloadWallpaperFragment;
import wp.a360.point.com.myapplication.ui.view.PagerSlidingTabStrip;

/**
 * Created by DN on 2017/12/12.
 */

public class MyWallpaperActivity extends BaseFragmentActivity {

    @ViewInject(R.id.pleasure_tabs)
    private PagerSlidingTabStrip pleasure_tabs;
    @ViewInject(R.id.mywallpaper_pager)
    private ViewPager mywallpaper_pager;

    private ClassifyViewPagerAdapter classViewPagerAdapter;
    private List<String> list = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private DownloadWallpaperFragment dwFragment;
    private CollectionFragment collectionFragment;

    @Override
    public void initParms(Bundle parms) {
        setAllowFullScreen(true);
        setScreenRoate(false);
        setSteepStatusBar(false);
        setSetActionBarColor(true);// 设置状态栏颜色
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.my_wallpaper_activity;
    }

    @Override
    public void initView(View view) {
        x.view().inject(this,mContextView);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {

        list.add(Constant.MyTabs.COLLECTION_WALLLPAPER);
        list.add(Constant.MyTabs.DOWNLOAD_WALLLPAPER);

        if(collectionFragment ==null){
            collectionFragment = new CollectionFragment();
        }
        if(dwFragment==null){
            dwFragment = new DownloadWallpaperFragment();
        }
        fragments.add(collectionFragment);
        fragments.add(dwFragment);
        classViewPagerAdapter = new ClassifyViewPagerAdapter(getSupportFragmentManager(), list, fragments);
        mywallpaper_pager.setAdapter(classViewPagerAdapter);
        mywallpaper_pager.setOffscreenPageLimit(list.size());//依据传过来的tab页的个数来设置缓存的页数
        //tabs.setFollowTabColor(true);//设置标题是否跟随
        pleasure_tabs.setViewPager(mywallpaper_pager);
        //
    }


    @Override
    public void widgetClick(View v) {

    }

    public class ClassifyViewPagerAdapter extends FragmentPagerAdapter {
        private List<String> mPagerTitles;
        private List<Fragment> mData;

        public ClassifyViewPagerAdapter(FragmentManager fm, List<String> titles, List<Fragment> lists) {
            super(fm);
            this.mPagerTitles = titles;
            this.mData = lists;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPagerTitles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mData.get(position);
        }

        @Override
        public int getCount() {
            return mPagerTitles.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

    }



    }
