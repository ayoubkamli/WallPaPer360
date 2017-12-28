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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;
import com.wp.point.qj.jb.R;
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
    public int type = 1; //区分请求数据是否是本应用数据
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
                        TypeToken<List<DailySelect>> listTypeToken = new TypeToken<List<DailySelect>>() {};
                        mData = gson.fromJson(str, listTypeToken.getType());
                        if(mData!=null){
                            if(mData.size()<num){
                                //如果第一次请求的数据小于num,表示已经没有数据了。
                                isLoadOK = true;
                                type = 2;
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
        arrayMap.put(Constant.HttpConstants.pageType, type + "");
        arrayMap.put(Constant.HttpConstants.appName,mContext.getResources().getString(R.string.intener_name)+"");
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
        final ArrayMap<String,DailySelect> collection = SharedPreferencesUtils.getInstance(mContext).getHashMapData("collection", DailySelect.class);
        String url = Constant.HttpConstants.getHomeData;
        ArrayMap<String,String> arrayMap = new ArrayMap<>();
        arrayMap.put(Constant.HttpConstants.pageNum,start+"");
        arrayMap.put(Constant.HttpConstants.pageSize,num+"");
        arrayMap.put(Constant.HttpConstants.pageType,type+"");
        arrayMap.put(Constant.HttpConstants.appName,mContext.getResources().getString(R.string.intener_name)+"");
        XutilsHttp.xUtilsPost(url, arrayMap, new XutilsHttp.XUilsCallBack() {
            @Override
            public void onResponse(String result) {
                try{
                    if (result != null) {
                        //请求成功时的回调
                        Gson gson = new Gson();
                        TypeToken<List<DailySelect>> listTypeToken = new TypeToken<List<DailySelect>>() {
                        };
                        final List<DailySelect> listData = gson.fromJson(result, listTypeToken.getType());
                        home_list.post(new Runnable() {
                            @Override
                            public void run() {
                                if(listData!=null){
                                    if(listData.size()<num){
                                        type = 2;
                                    }
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
