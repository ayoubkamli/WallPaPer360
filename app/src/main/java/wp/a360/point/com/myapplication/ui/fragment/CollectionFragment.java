package wp.a360.point.com.myapplication.ui.fragment;

import android.content.Intent;
import android.util.ArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import wp.a360.point.com.myapplication.R;
import wp.a360.point.com.myapplication.ui.activity.WallPaperDetailsActivity;
import wp.a360.point.com.myapplication.ui.adapter.TypeDetailsAdapter;
import wp.a360.point.com.myapplication.ui.base.BaseFragment;
import wp.a360.point.com.myapplication.ui.entity.DailySelect;
import wp.a360.point.com.myapplication.ui.utils.SharedPreferencesUtils;
import wp.a360.point.com.myapplication.ui.view.MyGridView;

/**
 * Created by DN on 2017/12/12.
 */

public class CollectionFragment extends BaseFragment {

    @ViewInject(R.id.my_wallpaper_null)
    private LinearLayout my_wallpaper_null;
    @ViewInject(R.id.my_wallpaper_content)
    private ScrollView my_wallpaper_content;
    @ViewInject(R.id.download_gridview)
    private MyGridView download_gridview;
    @ViewInject(R.id.download_type_size)
    private TextView download_type_size;
    @ViewInject(R.id.download_top_name)
    private TextView download_top_name;
    @ViewInject(R.id.my_wallpaper_error)
    private TextView my_wallpaper_error;
    @ViewInject(R.id.download_top_image)
    private ImageView download_top_image;

    private List<DailySelect> collection;
    private TypeDetailsAdapter tdAdapter;
    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.download_wallpaper_layout;
    }

    @Override
    protected void initView() {
        x.view().inject(this,mContextView);
    }

    List<DailySelect> mData = new ArrayList();
    List<DailySelect> mData1 = new ArrayList();
    @Override
    protected void initData() {
        download_gridview.setFocusable(false);
        //查询已下载的图片
        collection = SharedPreferencesUtils.getInstance(mContext).getDataList("collection", DailySelect.class);
        if(collection==null){
            my_wallpaper_null.setVisibility(View.VISIBLE);
            my_wallpaper_content.setVisibility(View.GONE);
            my_wallpaper_error.setText("暂无任何收藏的壁纸");
        }else{
            my_wallpaper_null.setVisibility(View.GONE);
            my_wallpaper_content.setVisibility(View.VISIBLE);

            mData.addAll(collection);
            DailySelect dailySelect = mData.get(0);
            if(collection.size()>0){
                download_type_size.setText(collection.size()+"张");
                download_top_name.setText("已收藏");
                //download_top_image
                Glide.with(mContext)
                        .load(dailySelect.getImageUrl())
                        //设置加载中图片
                        .placeholder(R.mipmap.lodinging) // can also be a drawable
                        //加载失败图片
                        .error(R.mipmap.lodinging)
                        //缓存源资源 result：缓存转换后的资源 none:不作任何磁盘缓存 all:缓存源资源和转换后的资源
                        //.diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .thumbnail(1f) //设置缩略图支持
                        .fitCenter()
                        .into(download_top_image);
                download_top_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext,WallPaperDetailsActivity.class);
                        DailySelect dailySelect = mData.get(0);
                        intent.putExtra("dailySelect",dailySelect);
                        startActivity(intent);
                    }
                });
            }
            for(int i = 0;i<mData.size();i++){
                if(mData.get(i).getImageID()!=dailySelect.getImageID()){
                    mData1.add(mData.get(i));
                }
            }
            if(tdAdapter==null){
                tdAdapter = new TypeDetailsAdapter(mContext,mData1);
            }
            download_gridview.setAdapter(tdAdapter);
            download_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(mContext,WallPaperDetailsActivity.class);
                    DailySelect dailySelect = mData1.get(i);
                    intent.putExtra("dailySelect",dailySelect);
                    intent.putExtra("isCollection",1);//收藏界面跳转过去的标识
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();

    }






}
