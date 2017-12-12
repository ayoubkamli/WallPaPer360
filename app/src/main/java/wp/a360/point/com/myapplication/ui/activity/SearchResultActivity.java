package wp.a360.point.com.myapplication.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.andview.refreshview.XRefreshView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import wp.a360.point.com.myapplication.R;
import wp.a360.point.com.myapplication.ui.adapter.TypeDetailsAdapter;
import wp.a360.point.com.myapplication.ui.base.BaseActivity;
import wp.a360.point.com.myapplication.ui.constant.Constant;
import wp.a360.point.com.myapplication.ui.entity.DailySelect;
import wp.a360.point.com.myapplication.ui.entity.FindDimImageMode;
import wp.a360.point.com.myapplication.ui.utils.NetworkUtils;
import wp.a360.point.com.myapplication.ui.utils.XutilsHttp;
import wp.a360.point.com.myapplication.ui.view.MyGridView;

/**
 * 分类详情界面
 * Created by DN on 2017/12/9.
 */

public class SearchResultActivity extends BaseActivity implements XRefreshView.XRefreshViewListener{

    @ViewInject(R.id.typedetails_refresh_view)
    private XRefreshView xRefreshView;
    @ViewInject(R.id.details_gridview)
    private MyGridView details_gridview;
    @ViewInject(R.id.details_top_image)
    private ImageView details_top_image;
    @ViewInject(R.id.details_top_name)
    private TextView details_top_name;
    @ViewInject(R.id.details_type_size)
    private TextView details_type_size;

    private static int start; //分页，从0页开始拿
    private int num = 8; //首次获取9条数据，之后没页拿（num-1）条数据
    private boolean isLoadOK;//false ：还有数据，true :没有数据
    private List<DailySelect> mData =new ArrayList<>();
    private TypeDetailsAdapter tdAdapter;
    private String topImageUrl;
    private String searchKeyName = null;
    private static int type = 1; //分页，从0页开始拿
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
        return R.layout.imagetype_details_activity;
    }

    @Override
    public void initView(View view) {
        x.view().inject(this);
    }

    @Override
    public void setListener() {
        details_top_image.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        initRefreshView();
        searchKeyName = getIntent().getStringExtra("searchKeyName");
        details_top_name.setText(searchKeyName+"");
        details_gridview.setFocusable(false);
        searchData(searchKeyName);  //请求搜数据
    }

    /**
     * 请求搜索结果
     * @param searchKeyName
     */
    private void searchData(String searchKeyName) {
        if(searchKeyName==null){showLog("信息：搜索结果为空");return; }
        String url = Constant.HttpConstants.getSearchResult;
        ArrayMap<String,String> arrayMap = new ArrayMap<>();
        arrayMap.put(Constant.HttpConstants.imageLabel,searchKeyName+"");
        arrayMap.put(Constant.HttpConstants.pageNum,mData.size()+"");
        arrayMap.put(Constant.HttpConstants.pageSize,num+"");
        arrayMap.put(Constant.HttpConstants.pageType,type+"");
        XutilsHttp.xUtilsPost(url, arrayMap, new XutilsHttp.XUilsCallBack() {
            @Override
            public void onResponse(String result) {
                if(result!=null){
                    Message msg = new Message();
                    msg.what = Constant.RESULT;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }else{
                    Message msg = new Message();
                    msg.what = Constant.ERROR;
                    msg.obj = "服务器异常";
                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void onFail(String result) {
                Message msg = new Message();
                msg.what = Constant.ERROR;
                msg.obj = result.toString();
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()){
            case  R.id.details_top_image:
                Intent intent = new Intent(mContext,WallPaperDetailsActivity.class);
                intent.putExtra("topImageUrl",topImageUrl);
                startActivity(intent);
                break;
        }
    }
    /**初始化下拉刷新控件*/
    private void initRefreshView() {
        // 设置是否可以下拉刷新
        xRefreshView.setPullRefreshEnable(false);
        // 设置是否可以上拉加载
        xRefreshView.setPullLoadEnable(true);
        // 设置上次刷新的时间
        //xRefreshView.restoreLastRefreshTime(lastRefreshTime);
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
    public void onRefresh() {
    }
    @Override
    public void onRefresh(boolean isPullDown) {
    }
    @Override
    public void onRelease(float direction) {
    }
    @Override
    public void onHeaderMove(double headerMovePercent, int offsetY) {
    }
    @Override
    public void onLoadMore(boolean isSilence) {
        //加载更多
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
        }, 500);
    }

    /**
     * 加载更多数据
     */
    private void loadData() {
        String url = Constant.HttpConstants.getSearchResult;
        ArrayMap<String,String> arrayMap = new ArrayMap<>();
        arrayMap.put(Constant.HttpConstants.imageLabel,searchKeyName+"");
        arrayMap.put(Constant.HttpConstants.pageNum,start+"");
        arrayMap.put(Constant.HttpConstants.pageSize,num+"");
        arrayMap.put(Constant.HttpConstants.pageType,type+"");
        XutilsHttp.xUtilsPost(url, arrayMap, new XutilsHttp.XUilsCallBack() {
            @Override
            public void onResponse(String result) {
                try{
                    if (result != null) {
                        //请求成功时的回调
                        Gson gson = new Gson();
                        TypeToken<FindDimImageMode> tToken = new TypeToken<FindDimImageMode>() {};
                        final FindDimImageMode listData = gson.fromJson(result, tToken.getType());
                        details_gridview.post(new Runnable() {
                            @Override
                            public void run() {
                                if(listData!=null){
                                    List<DailySelect> image = listData.getImage();
                                    start = start+image.size();
                                    type = 0;
                                    tdAdapter.refresh(image);
                                    if(!xRefreshView.isStopLoadMore()){
                                        xRefreshView.stopLoadMore();
                                        showToast("加载完成");
                                    }
                                }else{
                                    start=0;
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
                    mHandler.sendMessage(msg);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String result) {
                //请求错误时的回调
                Message msg = new Message();
                msg.what = Constant.ERROR;
                msg.obj = result.toString();
                mHandler.sendMessage(msg);
            }
        });
    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constant.RESULT:
                    try{
                        String str = (String)msg.obj;
                        Gson gson = new Gson();
                        TypeToken<FindDimImageMode> tToken = new TypeToken<FindDimImageMode>() {};
                        FindDimImageMode data =  gson.fromJson(str, tToken.getType());
                        if(data!=null){
                            List<DailySelect> image = data.getImage();
                            details_type_size.setText(data.getImageNum()+"张");
                            if(image.size()<num){
                                isLoadOK = true;
                            }
                            start = start+image.size();//不为空，记录加一页数据
                            type=0;
                            List<DailySelect> data1 = new ArrayList<>();
                            //取第一条数据作为顶部展示图片
                            for(int i = 0;i<image.size();i++){
                                if(i==0){
                                    topImageUrl = image.get(i).getImageUrl();
                                }else{
                                    data1.add(image.get(i));
                                }
                            }
                            mData.addAll(data1);
                            if(topImageUrl!=null){
                                Glide.with(mContext)
                                        .load(topImageUrl)
                                        //设置加载中图片
                                        .placeholder(R.mipmap.lodinging) // can also be a drawable
                                        //加载失败图片
                                        .error(R.mipmap.lodinging)
                                        //缓存源资源 result：缓存转换后的资源 none:不作任何磁盘缓存 all:缓存源资源和转换后的资源
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .thumbnail(1f) //设置缩略图支持
                                        .centerCrop()
                                        .into(details_top_image);
                            }
                            if(tdAdapter==null){
                                tdAdapter = new TypeDetailsAdapter(mContext,mData);
                            }
                            details_gridview.setAdapter(tdAdapter);
                            details_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(mContext,WallPaperDetailsActivity.class);
                                    DailySelect dailySelect = mData.get(i);
                                    intent.putExtra("dailySelect",dailySelect);
                                    intent.putExtra("listDailySelect",(Serializable)mData);
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
                    String message = (String) msg.obj;
                    showLog("ERROR信息："+message);
                    showToast("ERROR信息："+message);
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            start = 0;
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
