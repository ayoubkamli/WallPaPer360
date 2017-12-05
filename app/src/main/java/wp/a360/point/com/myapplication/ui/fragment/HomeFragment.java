package wp.a360.point.com.myapplication.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import wp.a360.point.com.myapplication.R;
import wp.a360.point.com.myapplication.ui.base.BaseFragment;
import wp.a360.point.com.myapplication.ui.constant.Constant;
import wp.a360.point.com.myapplication.ui.utils.OkHttpUtils;

/**
 * Created by DN on 2017/11/24.
 */

public class HomeFragment extends BaseFragment {
    @ViewInject(R.id.switch_slidemenu)
    private Button switch_slidemenu;
    @ViewInject(R.id.home_list)
    private ListView home_list;
    private SlidemenuClickListener onSlidemenuListener;
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

    Handler mHadnler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };
    @Override
    protected void initData() {
        switch_slidemenu.setOnClickListener(this);
        /***
         * 获取数据
         */
        ArrayMap<String,String> arrayMap = new ArrayMap<>();
        arrayMap.put("1","1");
        OkHttpUtils.doStaleTimeGet(mContext, Constant.HttpConstants.getHomeData, arrayMap, Constant.maxAge, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //请求错误时的回调
                Message msg = new Message();
                msg.what = Constant.ERROR;
                msg.obj = e.getMessage().toString();
                mHadnler.sendMessage(msg);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try{
                    if (response != null) {
                        String json = response.body().string();
                        //请求成功时的回调
                        Message msg = new Message();
                        msg.what = Constant.ERROR;
                        msg.obj = json;
                        mHadnler.sendMessage(msg);
                    }
                }catch (Exception e){
                    //请求成功时的回调
                    Message msg = new Message();
                    msg.what = Constant.ERROR;
                    msg.obj = e.getMessage().toString();
                    mHadnler.sendMessage(msg);
                    e.printStackTrace();
                }


            }
        });

    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()){
            case  R.id.switch_slidemenu:
                onSlidemenuListener.onSlideCallback();
                break;
        }
    }
    public interface  SlidemenuClickListener{
        void onSlideCallback();
    }
    public void setOnSlidemenuListener(SlidemenuClickListener onSlidemenuListener) {
        this.onSlidemenuListener = onSlidemenuListener;
    }

}
