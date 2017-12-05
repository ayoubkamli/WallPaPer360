package wp.a360.point.com.myapplication.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import wp.a360.point.com.myapplication.R;
import wp.a360.point.com.myapplication.ui.adapter.MenuAdapter;
import wp.a360.point.com.myapplication.ui.base.MenuFragmentActivity;
import wp.a360.point.com.myapplication.ui.constant.Constant;
import wp.a360.point.com.myapplication.ui.fragment.HomeFragment;
import wp.a360.point.com.myapplication.ui.fragment.SearchFragment;
import wp.a360.point.com.myapplication.ui.fragment.SortFragment;

public class MainActivity extends MenuFragmentActivity {


    @ViewInject(R.id.tab0)
    private ImageView tab0;
    @ViewInject(R.id.tab1)
    private ImageView tab1;
    @ViewInject(R.id.tab2)
    private ImageView tab2;

   // @ViewInject(R.id.menu_listview)
    private ListView mMenuListview;
    //@ViewInject(R.id.slideMenu)
    //private SlideMenu mSlideMenu;

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

    private int flResId = R.id.fl_menu_container;
    private SlidingMenu slidingMenu;
    private HomeFragment.SlidemenuClickListener listener;
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
        slidingMenu=new SlidingMenu(this);
        slidingMenu.setSlidingEnabled(true);//设置启动滑动
        int[] tabResIds = { R.id.tab0, R.id.tab1, R.id.tab2 };
        super.initTab(tabResIds);
        if (mianFragment == null) {
            mianFragment = new HomeFragment();
        }
        switchFragment(flResId, mianFragment);
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
    @Override
    public void doBusiness(Context mContext) {
        //隐藏标题栏
        //getSupportActionBar().hide();
        // configure the SlidingMenu
        //创建对象

        //设置滑动模式
        slidingMenu.setMode(SlidingMenu.LEFT);
        //SlidingMenu划出时主页面显示的剩余宽度
        slidingMenu.setShadowWidthRes(R.dimen.height_alert_title);
        slidingMenu.setBehindOffsetRes(R.dimen.height_alert_title);
        //设置滑动的区域
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //使SlidingMenu附加在Activity上
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //设置布局文件
        View view = slidingMenu.setMenu(R.layout.layout_menu);
        mMenuListview = view.findViewById(R.id.menu_listview);
        MenuAdapter adapter = new MenuAdapter(mContext,Constant.SETTINGS,menuIcon);
        mMenuListview.setAdapter(adapter);
        mMenuListview.setDivider(null);
        mMenuListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showToast("点击了"+Constant.SETTINGS[i]);
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
                slidingMenu.toggle();
                showToast("再按一次退出程序");
            } else {
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
                switchFragment(flResId, mianFragment);
                break;
            case R.id.tab1:
                slidingMenu.setSlidingEnabled(false);
                tab1.setImageResource(imgsHovers[1]);
                if (sortFragment == null) {
                    sortFragment = new SortFragment();
                }
                switchFragment(flResId, sortFragment);
                break;
            case R.id.tab2:
                slidingMenu.setSlidingEnabled(false);
                tab2.setImageResource(imgsHovers[2]);
                if (searchFragment == null) {
                    searchFragment = new SearchFragment();
                }
                switchFragment(flResId, searchFragment);
                break;
        }
        return true;



    }
}
