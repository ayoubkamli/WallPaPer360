package wp.a360.point.com.myapplication.ui.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import com.wp.point.qj.jb.R;
import wp.a360.point.com.myapplication.ui.activity.ImageTypeDetailsActivity;
import wp.a360.point.com.myapplication.ui.activity.MyWallpaperActivity;
import wp.a360.point.com.myapplication.ui.adapter.SortAdapter;
import wp.a360.point.com.myapplication.ui.base.BaseFragment;
import wp.a360.point.com.myapplication.ui.constant.Constant;
import wp.a360.point.com.myapplication.ui.entity.DailySelect;
import wp.a360.point.com.myapplication.ui.entity.ImageType;
import wp.a360.point.com.myapplication.ui.utils.SharedPreferencesUtils;
import wp.a360.point.com.myapplication.ui.utils.XutilsHttp;

/**
 * Created by DN on 2017/11/24.
 */

public class SortFragment extends BaseFragment {
    @ViewInject(R.id.sort_list)
    private ListView sort_list;
    @ViewInject(R.id.my_sort_wallpaper)
    private RelativeLayout my_sort_wallpaper;
    @ViewInject(R.id.sort_item_myNumber)
    private TextView sort_item_myNumber;
    @ViewInject(R.id.sort_myWallpaperName)
    private TextView sort_myWallpaperName;
    private SortAdapter sortAdapter;
    private List<ImageType> mData = new ArrayList<>();
    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.sort_activity;
    }

    @Override
    protected void initView() {
        x.view().inject(this,mContextView);
        my_sort_wallpaper.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        sort_list.setFocusable(false);
        sort_myWallpaperName.setText(getResources().getString(R.string.sort_myWallpaperName));
        List<String> collection = SharedPreferencesUtils.getInstance(mContext).getDownloadList("collection", String.class);
        List<DailySelect> downloadImage = SharedPreferencesUtils.getInstance(mContext).getDownloadList("downloadImage", DailySelect.class);
        if(collection!=null){
            if(downloadImage==null){
                sort_item_myNumber.setText(collection.size()+"张");
            }else{
                sort_item_myNumber.setText((collection.size()+downloadImage.size())+"张");
            }
        }else{
            if(downloadImage==null){
                sort_item_myNumber.setText("0张");
            }else{
                sort_item_myNumber.setText(downloadImage.size()+"张");
            }
        }

        /**
         * 获取服务器数据
         */
        String url = Constant.HttpConstants.getTypeData;
        XutilsHttp.xUtilsPost(url, null, new XutilsHttp.XUilsCallBack() {
            @Override
            public void onResponse(String result) {
                        if(result!=null){
                            Message msg = new Message();
                            msg.what = Constant.RESULT;
                            msg.obj = result;
                            tyHandler.sendMessage(msg);
                        }else{
                            Message msg = new Message();
                            msg.what = Constant.ERROR;
                            msg.obj = "服务器异常..";
                            tyHandler.sendMessage(msg);
                        }
            }
            @Override
            public void onFail(String result) {
                Message msg = new Message();
                msg.what = Constant.ERROR;
                msg.obj = result.toString();
                tyHandler.sendMessage(msg);
            }
        });

    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()){
            case  R.id.my_sort_wallpaper:
                Intent myWallpaper = new Intent(mContext,MyWallpaperActivity.class);
                startActivity(myWallpaper);
                break;
        }
    }

    private Handler tyHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case  Constant.RESULT:
                    try{
                        String str = (String)msg.obj;
                        Gson gson = new Gson();
                        TypeToken<List<ImageType>> type = new TypeToken<List<ImageType>>() {};
                        mData =  gson.fromJson(str, type.getType());
                        if(sortAdapter==null){
                            sortAdapter = new SortAdapter(mContext,mData);
                        }
                        sort_list.setAdapter(sortAdapter);
                        sort_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                //showToast("点击了"+mData.get(i).getImageTypeName()+"");
                                if(mData==null){
                                    showToast("网络出了点问题，请稍后再试试！");
                                    return;}
                                //mData.get(i).getImageTypeID()
                                Intent intent = new Intent(mContext, ImageTypeDetailsActivity.class);
                                intent.putExtra("imageTypeID",mData.get(i).getImageTypeID());
                                intent.putExtra("imageTypeName",mData.get(i).getImageTypeName());
                                intent.putExtra("imageTypeSize",mData.get(i).getImageTypeNum());
                                startActivity(intent);

                            }
                        });
                    }catch (Exception ex){
                        showLog("ERROR信息："+ex.getMessage().toString());
                        showToast("ERROR信息："+ex.getMessage().toString());
                        ex.printStackTrace();
                    }
                    break;
                case  Constant.ERROR:
                    String message = (String) msg.obj;
                    showLog("ERROR信息："+message.toString());
                    showToast("ERROR信息："+message.toString());
                    break;
            }

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        ArrayMap<String,DailySelect> collection = SharedPreferencesUtils.getInstance(mContext).getHashMapData("collection", DailySelect.class);
        List<DailySelect> downloadImage = SharedPreferencesUtils.getInstance(mContext).getDownloadList("downloadImage", DailySelect.class);
        if(collection!=null){
            if(downloadImage==null){
                sort_item_myNumber.setText(collection.size()+"张");
            }else{
                sort_item_myNumber.setText((collection.size()+downloadImage.size())+"张");
            }
        }else{
            if(downloadImage==null){
                sort_item_myNumber.setText("0张");
            }else{
                sort_item_myNumber.setText(downloadImage.size()+"张");
            }
        }
    }
}
