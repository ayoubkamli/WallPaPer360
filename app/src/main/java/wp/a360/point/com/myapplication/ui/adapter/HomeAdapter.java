package wp.a360.point.com.myapplication.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wp.point.qj.jb.R;
import wp.a360.point.com.myapplication.ui.constant.Constant;
import wp.a360.point.com.myapplication.ui.entity.DailySelect;
import wp.a360.point.com.myapplication.ui.utils.SharedPreferencesUtils;
import wp.a360.point.com.myapplication.ui.utils.XutilsHttp;
import wp.a360.point.com.myapplication.ui.view.MyListView;

/**
 * Created by DN on 2017/12/6.
 */

public class HomeAdapter extends BaseAdapter {
    private Context context;
    private List<DailySelect> mData;
    private LayoutInflater layoutInflater;
    private ArrayMap<String,DailySelect> collection;

    public HomeAdapter(Context context, List<DailySelect> mData){
        this.context = context;
        this.mData = mData;
        this.collection = SharedPreferencesUtils.getInstance(context).getHashMapData("collection", DailySelect.class);
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final HomeHolder homeHolder;
        if(view==null){
            homeHolder = new HomeHolder();
            view = layoutInflater.inflate(R.layout.home_item_layout,null);
            x.view().inject(homeHolder,view);
            view.setTag(homeHolder);
        }else{
            homeHolder = (HomeHolder) view.getTag();
        }
        final DailySelect ds = mData.get(position);
        if(ds!=null){
            if(ds.getCollectionNumber()>=0){
                int collectionNumber = ds.getCollectionNumber();
                homeHolder.home_item_cNumber.setText(collectionNumber+"");
            }else{
                homeHolder.home_item_cNumber.setText("0");
            }
            if(ds.getImageDate()!=null&&!ds.getImageDate().equals("")){
                homeHolder.home_item_date.setText(ds.getImageDate()+"");
            }else{
                homeHolder.home_item_date.setText("xx年xx月");
            }
            if(collection !=null){
                if(collection.containsKey(ds.getImageID()+"")){
                    homeHolder.iv_collection.setImageResource(R.mipmap.collection);
                }else{
                    homeHolder.iv_collection.setImageResource(R.mipmap.uncollection);
                }
            }
            if(ds.getImageUrl()!=null){
                Glide.with(context)
                        .load(ds.getImageUrl())
                        //设置加载中图片
                        .placeholder(R.mipmap.lodinging) // can also be a drawable
                        //加载失败图片
                        .error(R.mipmap.lodinging)
                        //缓存源资源 result：缓存转换后的资源 none:不作任何磁盘缓存 all:缓存源资源和转换后的资源,SOURCE：缓存原始数据
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .thumbnail(0.5f) //设置缩略图支持
                        .override(960,480)
                        .fitCenter()
                        .into(homeHolder.home_item_image);
            }
            homeHolder.ll_collection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        ArrayMap<String,DailySelect> collection = SharedPreferencesUtils.getInstance(context).getHashMapData("collection", DailySelect.class);
                        if(collection ==null){
                            homeHolder.iv_collection.setImageResource(R.mipmap.collection);
                            homeHolder.home_item_cNumber.setText((ds.getCollectionNumber()+1)+"");
                            //增加收藏的操作
                            collection = new ArrayMap<>();
                            collection.put(ds.getImageID()+"",ds);
                            SharedPreferencesUtils.getInstance(context).putHashMapData("collection",collection);
                            ds.setCollectionNumber(ds.getCollectionNumber()+1);
                            collectionImage(ds,Constant.Collection.COLLECTION_TYPE_ADD);
                        }else{
                            //不为空，判断是否包含此图片ID
                            if(!collection.containsKey(ds.getImageID()+"")){
                                homeHolder.iv_collection.setImageResource(R.mipmap.collection);
                                homeHolder.home_item_cNumber.setText((ds.getCollectionNumber()+1)+"");
                                collection.put(ds.getImageID()+"",ds);
                                SharedPreferencesUtils.getInstance(context).putHashMapData("collection",collection);
                                ds.setCollectionNumber(ds.getCollectionNumber()+1);
                                collectionImage(ds,Constant.Collection.COLLECTION_TYPE_ADD);
                            }else{
                                Toast.makeText(context,"已经收藏过了！",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }catch (Exception ex){
                        Log.i("TAG","收藏异常信息："+ex.getMessage().toString());
                        Toast.makeText(context,ex.getMessage().toString()+"",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        /**
         * Glide.get(this).clearDiskCache();//清理磁盘缓存 需要在子线程中执行
         Glide.get(this).clearMemory();//清理内存缓存  可以在UI主线程中进行
         * */
        return view;
    }

    public void refresh(List<DailySelect> listData,ArrayMap<String,DailySelect> collection) {
        this.collection = collection;
        this.mData.addAll(listData);
        notifyDataSetChanged();
    }

    public void upView(MyListView home_list,int position,int clickNumber){
        View childAt = home_list.getChildAt(position);
        HomeHolder tag = (HomeHolder) childAt.getTag();
        tag.home_item_cNumber.setText(clickNumber+"");
        tag.iv_collection.setImageResource(R.mipmap.collection);
    }

    /**
     * 增，减收藏
     * @param dailySelect 点击该图片的实体类
     * @param collectionType  增加，减少标识
     */
    Handler mHandler = new Handler();
    private void collectionImage(final DailySelect dailySelect, final int collectionType) {
        new Thread(){
          @Override
           public void run() {
                String collectionUrl = Constant.HttpConstants.collectionImage;
                ArrayMap<String,String> am = new ArrayMap<>();
                am.put(Constant.HttpConstants.imageID,dailySelect.getImageID()+"");
                am.put(Constant.HttpConstants.type,collectionType+"");
                XutilsHttp.xUtilsRequest(collectionUrl, am, new XutilsHttp.XUilsCallBack() {
                    @Override
                    public void onResponse(final String result) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(context,"收藏结果信息："+result+"",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onFail(final String result) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(context,"收藏失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
           }
         }.start();
    }

    class HomeHolder{
        @ViewInject(R.id.home_item_image)
        ImageView home_item_image;
        @ViewInject(R.id.home_item_date)
        TextView home_item_date;
        @ViewInject(R.id.home_item_cNumber)
        TextView home_item_cNumber;
        @ViewInject(R.id.ll_collection)
        LinearLayout ll_collection;
        @ViewInject(R.id.iv_collection)
        ImageView iv_collection;
    }

}
