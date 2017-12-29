package wp.a360.point.com.myapplication.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import com.wp.point.qj.jb.R;
import wp.a360.point.com.myapplication.ui.activity.WallPaperDetailsActivity;
import wp.a360.point.com.myapplication.ui.adapter.TypeDetailsAdapter;
import wp.a360.point.com.myapplication.ui.base.BaseFragment;
import wp.a360.point.com.myapplication.ui.constant.Constant;
import wp.a360.point.com.myapplication.ui.entity.DailySelect;
import wp.a360.point.com.myapplication.ui.utils.SharedPreferencesUtils;
import wp.a360.point.com.myapplication.ui.view.MyGridView;

/**
 * Created by DN on 2017/12/12.
 */

public class DownloadWallpaperFragment extends BaseFragment {

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
    @ViewInject(R.id.download_top_image)
    private ImageView download_top_image;
    @ViewInject(R.id.my_wallpaper_error)
    private TextView my_wallpaper_error;

    private TypeDetailsAdapter tdAdapter;
    private List<DailySelect> mData1 = new ArrayList();

    private DownloadBoradCastReceiver downloadBoradCast;
    private LocalBroadcastManager instance;

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


    @Override
    protected void initData() {
        //查询已下载的图片
        download_gridview.setFocusable(false);

        instance = LocalBroadcastManager.getInstance(mContext);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.DOWNLOAD_ACTION);
        downloadBoradCast = new DownloadBoradCastReceiver();
        instance.registerReceiver(downloadBoradCast,intentFilter);
        download_top_name.setText("已下载");
        List<DailySelect> downloadImage = SharedPreferencesUtils.getInstance(mContext).getDownloadList("downloadImage", DailySelect.class);
        if(downloadImage!=null&&downloadImage.size()>0){
            setData(downloadImage);
        }else{
            my_wallpaper_null.setVisibility(View.VISIBLE);
            my_wallpaper_content.setVisibility(View.GONE);
            my_wallpaper_error.setText("暂无任何下载的壁纸");
        }
    }

    private void setData(List<DailySelect> downloadImage) {
        final DailySelect clickSelect = downloadImage.get(0);
        download_type_size.setText(downloadImage.size() + "张");
        Glide.with(mContext)
                .load(clickSelect.getImageUrl())
                //设置加载中图片
                .placeholder(R.mipmap.lodinging) // can also be a drawable
                //加载失败图片
                .error(R.mipmap.lodinging)
                //缓存源资源 result：缓存转换后的资源 none:不作任何磁盘缓存 all:缓存源资源和转换后的资源
                //.diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .thumbnail(1f) //设置缩略图支持
                .fitCenter()
                .into(download_top_image);
        for (int i = 0; i < downloadImage.size(); i++) {
            if (downloadImage.get(i).getImageID() != clickSelect.getImageID()) {
                mData1.add(downloadImage.get(i));
            }
        }
        if (tdAdapter == null) {
            tdAdapter = new TypeDetailsAdapter(mContext, mData1);
        }
        download_gridview.setAdapter(tdAdapter);
        download_top_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, WallPaperDetailsActivity.class);
                intent.putExtra("dailySelect", clickSelect);
                intent.putExtra("isCollection", 2);
                startActivity(intent);
            }
        });
        download_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(mContext, WallPaperDetailsActivity.class);
                DailySelect dailySelect = mData1.get(i);
                intent.putExtra("dailySelect", dailySelect);
                intent.putExtra("isCollection", 2);
                startActivity(intent);
            }
        });


    }

    @Override
    public void widgetClick(View v) {

    }

    private void setRefreshData(List<DailySelect> downloadImage){
        download_top_name.setText("已下载");
        if(downloadImage !=null&&downloadImage.size()>0){
            my_wallpaper_null.setVisibility(View.GONE);
            my_wallpaper_content.setVisibility(View.VISIBLE);
            final DailySelect clickSelect = downloadImage.get(0);
            download_type_size.setText(downloadImage.size() + "张");
            Glide.with(mContext)
                    .load(clickSelect.getImageUrl())
                    //设置加载中图片
                    .placeholder(R.mipmap.lodinging) // can also be a drawable
                    //加载失败图片
                    .error(R.mipmap.lodinging)
                    //缓存源资源 result：缓存转换后的资源 none:不作任何磁盘缓存 all:缓存源资源和转换后的资源
                    //.diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .thumbnail(1f) //设置缩略图支持
                    .fitCenter()
                    .into(download_top_image);

            final List<DailySelect> mData2 = new ArrayList();
            for (int i = 0; i < downloadImage.size(); i++) {
                if (downloadImage.get(i).getImageID() != clickSelect.getImageID()) {
                    mData2.add(downloadImage.get(i));
                }
            }

            if (tdAdapter == null) {
                tdAdapter = new TypeDetailsAdapter(mContext, mData2);
            }
            tdAdapter.cleanRefresh(mData2);
            download_top_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, WallPaperDetailsActivity.class);
                    intent.putExtra("dailySelect", clickSelect);
                    intent.putExtra("isCollection",2);
                    startActivity(intent);
                }
            });
            download_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(mContext, WallPaperDetailsActivity.class);
                    DailySelect dailySelect = mData2.get(i);
                    intent.putExtra("dailySelect", dailySelect);
                    intent.putExtra("isCollection", 2);
                    startActivity(intent);
                }
            });

        }else {
            my_wallpaper_null.setVisibility(View.VISIBLE);
            my_wallpaper_content.setVisibility(View.GONE);
            my_wallpaper_error.setText("暂无任何下载的壁纸");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(downloadBoradCast!=null){
            instance.unregisterReceiver(downloadBoradCast);
        }
    }

    class DownloadBoradCastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            List<DailySelect> downloadImage = SharedPreferencesUtils.getInstance(mContext).getDownloadList("downloadImage", DailySelect.class);
            if(null!=downloadImage&&downloadImage.size()>0){
                setRefreshData(downloadImage);
            }
        }
    }

}
