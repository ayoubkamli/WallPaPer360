package wp.a360.point.com.myapplication.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.ArrayMap;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import wp.a360.point.com.myapplication.R;
import wp.a360.point.com.myapplication.ui.activity.WallPaperDetailsActivity;
import wp.a360.point.com.myapplication.ui.adapter.HomeAdapter;
import wp.a360.point.com.myapplication.ui.entity.DailySelect;
import wp.a360.point.com.myapplication.ui.base.BaseFragment;
import wp.a360.point.com.myapplication.ui.constant.Constant;
import wp.a360.point.com.myapplication.ui.utils.NetworkUtils;
import wp.a360.point.com.myapplication.ui.utils.SharedPreferencesUtils;
import wp.a360.point.com.myapplication.ui.utils.XutilsHttp;
import wp.a360.point.com.myapplication.ui.view.MyListView;

/**
 * Created by DN on 2017/11/24.
 */

public class HomeFragment extends BaseFragment implements XRefreshView.XRefreshViewListener{
    @ViewInject(R.id.switch_slidemenu)
    private ImageView switch_slidemenu;
    @ViewInject(R.id.home_list)
    private MyListView home_list;
    @ViewInject(R.id.main_pull_refresh_view)
    private XRefreshView xRefreshView;
    @ViewInject(R.id.home_topName)
    private TextView home_topName;
    public static long lastRefreshTime;//刷新的时间
    public static int start = 0; //分页，从0页开始拿
    public int num = 8; //每页拿8条数据
    private boolean isLoadOK;//false ：还有数据，true :没有数据
    private HomeAdapter homeAdapter;
    private List<DailySelect> mData =new ArrayList<>();
    private SlidemenuClickListener onSlidemenuListener;
    private CollectionBoradCastReceiver collectionBoradCast;
    private LocalBroadcastManager instance;


    private Handler mHadnler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constant.RESULT:
                    try{
                        String str = (String)msg.obj;
                        Gson gson = new Gson();
                        TypeToken<List<DailySelect>> type = new TypeToken<List<DailySelect>>() {};
                        mData = gson.fromJson(str, type.getType());
                        if(mData!=null){
                            if(mData.size()<num){
                                //如果第一次请求的数据小于num,表示已经没有数据了。
                                isLoadOK = true;
                            }
                            start = start+mData.size();//不为空，记录加一页数据
                           if(homeAdapter==null){homeAdapter = new HomeAdapter(mContext,mData);}
                            home_list.setAdapter(homeAdapter);
                            home_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int posiotion, long l) {
                                    Intent intent = new Intent(mContext,WallPaperDetailsActivity.class);
                                    DailySelect dailySelect = mData.get(posiotion);
                                    intent.putExtra("dailySelect",dailySelect);
                                    intent.putExtra("posiotion",posiotion);
                                    startActivity(intent);
                                }
                            });
                            initCollection(homeAdapter);
                        }else{
                            isLoadOK=true;
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                        if(!xRefreshView.isStopLoadMore()){
                            xRefreshView.stopLoadMore();
                            showToast("已没有更多壁纸了");
                        }
                        showToast("RESULT异常:"+ex.getMessage().toString());
                        showLog("RESULT异常:"+ex.getMessage().toString());
                    }
                    break;
                case Constant.ERROR:
                    if(!xRefreshView.isStopLoadMore()){
                        xRefreshView.stopLoadMore();
                        showToast("已没有更多壁纸了");
                    }
                    showToast("ERROR异常:"+msg.obj.toString());
                    showLog("ERROR异常:"+msg.obj.toString());
                    break;
            }
        }
    };

    private void initCollection(HomeAdapter homeAdapter) {
        /**点击收藏按钮事件的监听*/
        homeAdapter.setCollectionListener(new HomeAdapter.OnCollectionListener() {
            @Override
            public void onClickCollection(DailySelect dailySelect,int collectionType,List<DailySelect> collection) {
                try{
                    if(collection==null){collection = new ArrayList<>();}
                    if(collectionType==Constant.Collection.COLLECTION_TYPE_ADD){ //为空，说明点击的图片没有收藏过 DailySelect
                        //增加收藏的操作
                        dailySelect.setCollectionNumber(dailySelect.getCollectionNumber()+1);
                        collection.add(dailySelect);
                        SharedPreferencesUtils.getInstance(mContext).setDataList("collection",collection);
                        collectionImage(dailySelect,Constant.Collection.COLLECTION_TYPE_ADD);
                    }
                }catch (Exception ex){
                    showToast("收藏失败！");
                    showLog("收藏异常信息："+ex.getMessage().toString());
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * 增，减收藏
     * @param dailySelect 点击该图片的实体类
     * @param collectionType  增加，减少标识
     */
    private void collectionImage(final DailySelect dailySelect, final int collectionType) {
        new Thread(){
            @Override
            public void run() {
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
                                showLog("收藏结果信息："+result+"");
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
        }.start();
    }

    @Override
    public View bindView() {
        return null;
    }
    @Override
    public int bindLayout() {
        return R.layout.home;
    }
    @Override
    protected void initView() {
        x.view().inject(this,mContextView);
    }
    @Override
    protected void initData() {
        switch_slidemenu.setOnClickListener(this);
        home_topName.setText(getResources().getString(R.string.home_topName));
        instance = LocalBroadcastManager.getInstance(mContext);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.COLLECTION_ACTION);
        collectionBoradCast = new CollectionBoradCastReceiver();
        instance.registerReceiver(collectionBoradCast,intentFilter);

        initRefreshView();
        getDailySelect();
    }
    /**初始化下拉刷新控件*/
    private void initRefreshView() {
        // 设置是否可以下拉刷新
        xRefreshView.setPullRefreshEnable(false);
        // 设置是否可以上拉加载
        xRefreshView.setPullLoadEnable(true);
        // 设置上次刷新的时间
        xRefreshView.restoreLastRefreshTime(lastRefreshTime);
        //当下拉刷新被禁用时，调用这个方法并传入false可以不让头部被下拉
        xRefreshView.setMoveHeadWhenDisablePullRefresh(false);
        // 设置时候可以自动刷新true 为自动刷新
        xRefreshView.setAutoRefresh(false);
        // 设置时候可以自动加载  true为自动加载
        xRefreshView.setAutoLoadMore(true);
        //刷新时不想让里面的列表滑动
        xRefreshView.setPinnedContent(true);
        xRefreshView.setXRefreshViewListener(this);
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()){
            case  R.id.switch_slidemenu:
                onSlidemenuListener.onSlideCallback();
                break;
        }
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onRefresh(boolean isPullDown) {
        //刷新
    }
    /**加载更多*/
    @Override
    public void onLoadMore(boolean isSilence) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isLoadOK) {
                    if (NetworkUtils.isNetworkAvailable(mContext)) {
                        loadData();
                    } else {
                        xRefreshView.stopLoadMore();
                        showToast("网络连接失败，请检查您的网络！");
                    }
                } else {
                    xRefreshView.stopLoadMore();
                    showToast("已经没有更多数据了");
                }
            }
        }, 1500);
    }

    @Override
    public void onRelease(float direction) {
    }

    @Override
    public void onHeaderMove(double headerMovePercent, int offsetY) {
    }

    /**
     * 获取数据
     */
    public void getDailySelect() {
        final String url = Constant.HttpConstants.getHomeData;
        final ArrayMap<String, String> arrayMap = new ArrayMap<>();
        arrayMap.put(Constant.HttpConstants.pageNum, start + "");
        arrayMap.put(Constant.HttpConstants.pageSize, num + "");
        try{
            new Thread(){
                @Override
                public void run() {
                   XutilsHttp.xUtilsPost(url, arrayMap, new XutilsHttp.XUilsCallBack() {
                        @Override
                        public void onResponse(String result) {
                            if(result!=null){
                                Message msg = new Message();
                                msg.what = Constant.RESULT;
                                msg.obj = result;
                                mHadnler.sendMessage(msg);
                            }else{
                                isLoadOK = true;
                            }
                        }
                        @Override
                        public void onFail(String result) {
                            //请求错误时的回调
                            Message msg = new Message();
                            msg.what = Constant.ERROR;
                            msg.obj = result.toString();
                            mHadnler.sendMessage(msg);
                        }
                    });
                }
            }.start();

        }catch (Exception ex){
            Message msg = new Message();
            msg.what = Constant.ERROR;
            msg.obj = ex.getMessage().toString();
            mHadnler.sendMessage(msg);
            ex.printStackTrace();
        }
    }

    public interface  SlidemenuClickListener{
        void onSlideCallback();
    }
    public void setOnSlidemenuListener(SlidemenuClickListener onSlidemenuListener) {
        this.onSlidemenuListener = onSlidemenuListener;
    }
    /**
     * 加载更多数据
     */
    private void loadData() {
        final List<DailySelect> collection = SharedPreferencesUtils.getInstance(mContext).getDataList("collection", DailySelect.class);
        String url = Constant.HttpConstants.getHomeData;
        ArrayMap<String,String> arrayMap = new ArrayMap<>();
        arrayMap.put(Constant.HttpConstants.pageNum,start+"");
        arrayMap.put(Constant.HttpConstants.pageSize,num+"");
        XutilsHttp.xUtilsPost(url, arrayMap, new XutilsHttp.XUilsCallBack() {
            @Override
            public void onResponse(String result) {
                try{
                    if (result != null) {
                        //请求成功时的回调
                        Gson gson = new Gson();
                        TypeToken<List<DailySelect>> type = new TypeToken<List<DailySelect>>() {
                        };
                        final List<DailySelect> listData = gson.fromJson(result, type.getType());
                        home_list.post(new Runnable() {
                            @Override
                            public void run() {
                                if(listData!=null){
                                    start = start+listData.size();
                                    if(homeAdapter==null){
                                        homeAdapter = new HomeAdapter(mContext,mData);
                                    }
                                    homeAdapter.refresh(listData,collection);
                                    if(!xRefreshView.isStopLoadMore()){
                                        xRefreshView.stopLoadMore();
                                        showToast("加载完成");
                                    }
                                }else{
                                    xRefreshView.stopLoadMore();
                                    showToast("数据已全部加载完");
                                    isLoadOK=true;
                                }
                            }
                        });
                    }
                }catch (Exception e){
                    Message msg = new Message();
                    msg.what = Constant.ERROR;
                    msg.obj = e.getMessage().toString();
                    mHadnler.sendMessage(msg);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String result) {
                //请求错误时的回调
                Message msg = new Message();
                msg.what = Constant.ERROR;
                msg.obj = result.toString();
                mHadnler.sendMessage(msg);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(collectionBoradCast!=null){
            instance.unregisterReceiver(collectionBoradCast);
        }
    }

    class CollectionBoradCastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Constant.COLLECTION_ACTION)){
                int position = intent.getIntExtra("position", 0);
                int clickNumber = intent.getIntExtra("clickNumber", 0);
                if(homeAdapter!=null){
                    homeAdapter.upView(home_list,position,clickNumber);
                }
            }
        }
    }





}
