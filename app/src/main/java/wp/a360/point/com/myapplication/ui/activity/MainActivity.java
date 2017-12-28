package wp.a360.point.com.myapplication.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.wp.point.qj.jb.R;
import wp.a360.point.com.myapplication.ui.adapter.MenuAdapter;
import wp.a360.point.com.myapplication.ui.base.MenuFragmentActivity;
import wp.a360.point.com.myapplication.ui.constant.Constant;
import wp.a360.point.com.myapplication.ui.entity.DailySelect;
import wp.a360.point.com.myapplication.ui.fragment.HomeFragment;
import wp.a360.point.com.myapplication.ui.fragment.SearchFragment;
import wp.a360.point.com.myapplication.ui.fragment.SortFragment;
import wp.a360.point.com.myapplication.ui.utils.FileUtils;
import wp.a360.point.com.myapplication.ui.utils.SharedPreferencesUtils;
import wp.a360.point.com.myapplication.ui.widget.SVProgressHUD;

public class MainActivity extends MenuFragmentActivity {
    @ViewInject(R.id.tab0)
    private ImageView tab0;
    @ViewInject(R.id.tab1)
    private ImageView tab1;
    @ViewInject(R.id.tab2)
    private ImageView tab2;
    private ListView mMenuListview;
    // 未选中
    private int[] imageNormals = { R.mipmap.ic_home0,// 首页
            R.mipmap.ic_home1,// 分类
            R.mipmap.ic_home2,// 搜索
    };
    // 选中
    private int[] imgsHovers = { R.mipmap.ic_home_select0,
            R.mipmap.ic_home_select1,
            R.mipmap.ic_home_select2,
            };
    private int [] menuIcon = {R.mipmap.onescreen_onechange,R.mipmap.clean_upcach,R.mipmap.signout};

    /**首页界面*/
    HomeFragment mianFragment;
    /**分类界面*/
    SortFragment sortFragment;
    /**搜索界面*/
    SearchFragment searchFragment;

    private int flResId1 = R.id.fl_menu_container1;
    private int flResId2 = R.id.fl_menu_container2;
    private int flResId3 = R.id.fl_menu_container3;
    private SlidingMenu slidingMenu;
    private HomeFragment.SlidemenuClickListener listener;
    private BaseAnimatorSet mBasIn;
    private BaseAnimatorSet mBasOut;
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
        return R.layout.activity_main;
    }
    @Override
    public void initView(View view) {
        x.view().inject(this,mContextView);
        //创建对象
        slidingMenu=new SlidingMenu(this);
        slidingMenu.setSlidingEnabled(true);//设置启动滑动
        mBasIn = new BounceTopEnter();
        mBasOut = new SlideBottomExit();

        List<ImageView> image = new ArrayList<>();
        List<Fragment> listFragment = new ArrayList<>();
        image.add(tab0);
        image.add(tab1);
        image.add(tab2);
        translate(image);
        int[] tabResIds = { R.id.tab0, R.id.tab1, R.id.tab2 };
        int [] flResId = {R.id.fl_menu_container1, R.id.fl_menu_container2, R.id.fl_menu_container3};
        super.initTab(tabResIds);
        if (mianFragment == null) {
            mianFragment = new HomeFragment();
        }
        if (sortFragment == null) {
            sortFragment = new SortFragment();
        }
        if (searchFragment == null) {
            searchFragment = new SearchFragment();
        }
        listFragment.add(mianFragment);
        listFragment.add(sortFragment);
        listFragment.add(searchFragment);

        setFragments(flResId,listFragment);

        showFragment(flResId1,mianFragment);
        //switchFragment(flResId1, mianFragment);
    }
    public void setBasIn(BaseAnimatorSet bas_in) {
        this.mBasIn = bas_in;
    }

    public void setBasOut(BaseAnimatorSet bas_out) {
        this.mBasOut = bas_out;
    }
    @Override
    public void setListener() {
        listener  = new HomeFragment.SlidemenuClickListener() {
            @Override
            public void onSlideCallback() {
                if(!slidingMenu.isMenuShowing()){
                    slidingMenu.showMenu();
                }else{
                    slidingMenu.showContent();
                }
            }
        };
        mianFragment.setOnSlidemenuListener(listener);
    }
    private Handler mHandler;
    private NormalDialog dialog;
    @Override
    public void doBusiness(final Context mContext) {
        if(mHandler==null){
            mHandler = new Handler();
        }
        //设置滑动模式
        slidingMenu.setMode(SlidingMenu.LEFT);
        //SlidingMenu划出时主页面显示的剩余宽度
        slidingMenu.setShadowWidthRes(R.dimen.height_alert_title);
        slidingMenu.setBehindOffsetRes(R.dimen.header_logo_size);
        //设置滑动的区域
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //使SlidingMenu附加在Activity上
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //设置布局文件
        View view = slidingMenu.setMenu(R.layout.layout_menu);
        mMenuListview = view.findViewById(R.id.menu_listview);
        TextView menu_appName = view.findViewById(R.id.menu_appName);
        TextView menu_brief = view.findViewById(R.id.menu_brief);
        menu_appName.setText(getResources().getString(R.string.app_name));
        menu_brief.setText(getResources().getString(R.string.menu_brief));

        MenuAdapter adapter = new MenuAdapter(mContext,Constant.SETTINGS,menuIcon);
        mMenuListview.setAdapter(adapter);
        mMenuListview.setDivider(null);
        mMenuListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //showToast("点击了"+Constant.SETTINGS[i]);
                if(Constant.SETTINGS[i].equals("清理缓存")){
                    clear();
                }else if(Constant.SETTINGS[i].equals("退出应用")){
                    if(dialog==null){
                        dialog = new NormalDialog(mContext);
                    }
                    dialog.content("是否确定退出程序?")//
                            .showAnim(mBasIn)//
                            .dismissAnim(mBasOut)//
                            .show();
                    dialog.setOnBtnClickL(new OnBtnClickL() {
                        @Override
                        public void onBtnClick() {
                            dialog.dismiss();
                        }
                    }, new OnBtnClickL() {
                        @Override
                        public void onBtnClick() {
                            slidingMenu.showContent();
                            finish();
                        }
                    });



                }
            }
        });

    }

    /**
     * 清理缓存
     */
    private void clear() {
        if(dialog==null){
            dialog = new NormalDialog(mContext);
        }
        dialog.content("是否清理已下载的壁纸?")//
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                //取消
                dialog.dismiss();
                SVProgressHUD.showWithStatus(mContext, "开始清理...");
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(SVProgressHUD.isShowing(mContext)){
                            SVProgressHUD.dismiss(mContext);
                            showToast("清理完成！");
                        }
                    }
                },2000);
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                //确定
                dialog.dismiss();
                SVProgressHUD.showWithStatus(mContext, "开始清理...");
                try {
                    //清理图片
                    FileUtils.cleanImage(new File(Constant.clearImagePath));
                    //清理SP数据库中保存的下载数据
                    List<DailySelect> downloadImage = SharedPreferencesUtils.getInstance(mContext).getDownloadList("downloadImage", DailySelect.class);
                    if(downloadImage!=null&&downloadImage.size()>0){
                        downloadImage.clear();
                        downloadImage=null;
                        SharedPreferencesUtils.getInstance(mContext).setDownloadList("downloadImage", downloadImage ,true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showLog("错误信息："+e.getMessage().toString());
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(SVProgressHUD.isShowing(mContext)){
                            SVProgressHUD.dismiss(mContext);
                            showToast("清理完成！");
                        }
                    }
                },1000);
            }
        });
    }

    @Override
    public void widgetClick(View v) {
    }
    private long mkeyTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mkeyTime) > 2000) {
                mkeyTime = System.currentTimeMillis();
                if(slidingMenu.isMenuShowing()){
                    slidingMenu.showContent();
                }
                showToast("再按一次退出程序");
            } else {
                HomeFragment.start = 0;
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected boolean onTabClick(int tabId) {
        tab0.setImageResource(imageNormals[0]);
        tab1.setImageResource(imageNormals[1]);
        tab2.setImageResource(imageNormals[2]);
        switch (tabId) {
            case R.id.tab0:
                slidingMenu.setSlidingEnabled(true);
                tab0.setImageResource(imgsHovers[0]);
                if (mianFragment == null) {
                    mianFragment = new HomeFragment();
                }
                //switchFragment(flResId1, mianFragment);
                showFragment(flResId1,mianFragment);
                break;
            case R.id.tab1:
                slidingMenu.setSlidingEnabled(false);
                tab1.setImageResource(imgsHovers[1]);
                if (sortFragment == null) {
                    sortFragment = new SortFragment();
                }
                //switchFragment(flResId2, sortFragment);
                showFragment(flResId2,sortFragment);
                break;
            case R.id.tab2:
                slidingMenu.setSlidingEnabled(false);
                tab2.setImageResource(imgsHovers[2]);
                if (searchFragment == null) {
                    searchFragment = new SearchFragment();
                }
                //switchFragment(flResId3, searchFragment);
                showFragment(flResId3,searchFragment);
                break;
        }
        return true;
    }

    public void translate(List<ImageView> image) {
        TranslateAnimation translateAnimation;
        for(int i=0;i<image.size();i++){
            translateAnimation =new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0f,  //X轴的开始位置
                    Animation.RELATIVE_TO_SELF, 0f,  //X轴的结束位置
                    Animation.RELATIVE_TO_SELF, 0f,  //Y轴的开始位置
                    Animation.RELATIVE_TO_SELF, -0.1f);  //Y轴的结束位置
            translateAnimation.setRepeatCount(Animation.INFINITE);//无限次数
            translateAnimation.setRepeatMode(Animation.REVERSE);//RESTART表示从头开始，REVERSE表示从末尾倒播。
            if(i==0){
                translateAnimation.setDuration(1000);
            }else if(i==1){
                translateAnimation.setDuration(1500);
            }else if(i==2){
                translateAnimation.setDuration(2000);
            }
            image.get(i).setAnimation(translateAnimation);
        }

    }






}
