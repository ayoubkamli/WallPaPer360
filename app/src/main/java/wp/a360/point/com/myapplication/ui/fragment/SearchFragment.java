package wp.a360.point.com.myapplication.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import wp.a360.point.com.myapplication.R;
import wp.a360.point.com.myapplication.ui.activity.ImageTypeDetailsActivity;
import wp.a360.point.com.myapplication.ui.activity.SearchResultActivity;
import wp.a360.point.com.myapplication.ui.base.BaseFragment;
import wp.a360.point.com.myapplication.ui.constant.Constant;
import wp.a360.point.com.myapplication.ui.entity.DailySelect;
import wp.a360.point.com.myapplication.ui.entity.FindDimImageMode;
import wp.a360.point.com.myapplication.ui.entity.KeyWord;
import wp.a360.point.com.myapplication.ui.utils.RandomUtils;
import wp.a360.point.com.myapplication.ui.utils.XutilsHttp;
import wp.a360.point.com.myapplication.ui.view.FlowLayout;

/**
 * Created by DN on 2017/11/24.
 */

public class SearchFragment extends BaseFragment {

    @ViewInject(R.id.search_edit)
    private EditText search_edit;
    @ViewInject(R.id.search_commit)
    private ImageView search_commit;
    @ViewInject(R.id.flow_layout)
    private FlowLayout flow_layout;

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.search_activity;
    }

    @Override
    protected void initView() {
        x.view().inject(this,mContextView);
    }

    @Override
    protected void initData() {
        //search_edit.setInputType(InputType.TYPE_NULL);
        InputMethodManager inputMethodManager = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(search_edit.getWindowToken(), 0);
        search_commit.setOnClickListener(this);
        /**
         * 获取热搜词集合
         */
        String url = Constant.HttpConstants.getSearchKeyWord;
        XutilsHttp.xUtilsPost(url, null, new XutilsHttp.XUilsCallBack() {
            @Override
            public void onResponse(String result) {
                if(result!=null){
                    Message msg = new Message();
                    msg.what = Constant.RESULT;
                    msg.obj = result;
                    searchHandler.sendMessage(msg);
                }else{
                    Message msg = new Message();
                    msg.what = Constant.ERROR;
                    msg.obj = result.toString();
                    searchHandler.sendMessage(msg);
                }
            }
            @Override
            public void onFail(String result) {
                Message msg = new Message();
                msg.what = Constant.ERROR;
                msg.obj = result.toString();
                searchHandler.sendMessage(msg);
            }
        });




    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.search_commit:
                String searchKey = search_edit.getText().toString().trim();
                if(searchKey==null||searchKey.equals("")){showToast("请输入搜索的内容！");return;}
                Intent intent = new Intent(mContext, SearchResultActivity.class);
                intent.putExtra("searchKeyName",searchKey);
                startActivity(intent);
                break;

        }
    }

    private Handler searchHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case  Constant.RESULT:
                    try{
                        String result = (String) msg.obj;
                        Gson gson = new Gson();
                        TypeToken<List<KeyWord>> tToken = new TypeToken<List<KeyWord>>(){};
                        List<KeyWord> data =  gson.fromJson(result, tToken.getType());
                        if(data!=null){
                            showLog("data的大小："+data.size()+"");
                            initFlowLayout(flow_layout,data);
                        }else{
                            showLog("获取关键词信息：NULL");
                        }
                    }catch (Exception ex){
                        showLog("异常信息："+ex.getMessage().toString());
                        showToast("异常信息："+ex.getMessage().toString());
                        ex.printStackTrace();
                    }
                    break;
                case Constant.ERROR:
                    String message = (String) msg.obj;
                    showLog("ERROR信息："+message.toString());
                    showToast("ERROR信息："+message.toString());
                    break;
            }
        }
    };

    /**动态添加关键词*/
    private void initFlowLayout(FlowLayout flow_layout, final List<KeyWord> data) {
        if(data==null){return; }
        if(data.size()<=0){return;}
        for(int i = 0;i<data.size();i++){
            TextView textView = new TextView(mContext);
            textView.setText(data.get(i).getKeyWordName()+"");
            textView.setHeight(dp2px(45));
            textView.setTextSize(16);
            textView.setGravity(Gravity.CENTER);
            textView.setBackground(getResources().getDrawable(R.drawable.search_defualt_back_shape));
            //0：默认  1：字体颜色改变  2：背景改变，字体为白色
            switch (data.get(i).getKeyWordType()){
                case 0:
                    textView.setTextColor(getResources().getColor(R.color.bgColor_overlay));
                    break;
                case 1:
                    textView.setTextColor(getResources().getColor(RandomUtils.getRandomColor()));
                    break;
                case  2:
                    int randomColor = RandomUtils.getRandomColor();
                    textView.setTextColor(getResources().getColor(randomColor));
                    break;
            }
            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //showToast("点击了"+data.get(finalI).getKeyWordName());
                    Intent intent = new Intent(mContext, SearchResultActivity.class);
                    intent.putExtra("searchKeyName",data.get(finalI).getKeyWordName());
                    startActivity(intent);
                }
            });
            flow_layout.addView(textView);
        }



    }

    public int dp2px(int dpValue) {
        return (int) (dpValue * getResources().getDisplayMetrics().density);
    }



}
