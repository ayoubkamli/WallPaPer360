package wp.a360.point.com.myapplication.ui.adapter;

import android.content.Context;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import wp.a360.point.com.myapplication.R;
import wp.a360.point.com.myapplication.ui.constant.Constant;
import wp.a360.point.com.myapplication.ui.entity.DailySelect;
import wp.a360.point.com.myapplication.ui.utils.SharedPreferencesUtils;

/**
 * Created by DN on 2017/12/6.
 */

public class HomeAdapter extends BaseAdapter {
    private Context context;
    private List<DailySelect> mData;
    private LayoutInflater layoutInflater;
    private ArrayMap<String, DailySelect> collection;

    public HomeAdapter(Context context, List<DailySelect> mData,ArrayMap<String, DailySelect> collection){
        this.context = context;
        this.mData = mData;
        this.collection = collection;
        layoutInflater = LayoutInflater.from(context);

    }
    private OnCollectionListener collectionListener;
    public interface  OnCollectionListener{
        void onClickCollection(DailySelect dailySelect,int collectionType);
    }
    public void setCollectionListener(OnCollectionListener collectionListener) {
        this.collectionListener = collectionListener;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        final HomeHolder homeHolder;
        if(view==null){
            homeHolder = new HomeHolder();
            view = layoutInflater.inflate(R.layout.home_item_layout,null);
            x.view().inject(homeHolder,view);
            view.setTag(homeHolder);
        }else{
            homeHolder = (HomeHolder) view.getTag();
        }
        final DailySelect dailySelect = mData.get(i);
        final int collectionNumber = dailySelect.getCollectionNumber();
        homeHolder.home_item_cNumber.setText(collectionNumber+"");
        homeHolder.home_item_date.setText(dailySelect.getImageDate()+"");
        /*Picasso.with(context)
                .load(dailySelect.getImageUrl())
                .error(R.drawable.test2)
                //.resize(480,250)
                .centerCrop()
                .fit()
                .into(homeHolder.home_item_image);*/
        if(collection!=null){
            homeHolder.iv_collection.setImageResource(collection.containsKey(dailySelect.getImageID()+"")?R.mipmap.collection:R.mipmap.uncollection);
        }else{
            homeHolder.iv_collection.setImageResource(R.mipmap.uncollection);

        }
        if(dailySelect.getImageUrl()!=null){
            Glide.with(context)
                    .load(dailySelect.getImageUrl())
                    //设置加载中图片
                    .placeholder(R.mipmap.lodinging) // can also be a drawable
                    //加载失败图片
                    .error(R.mipmap.lodinging)
                    //缓存源资源 result：缓存转换后的资源 none:不作任何磁盘缓存 all:缓存源资源和转换后的资源
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .thumbnail(1f) //设置缩略图支持
                    .fitCenter()
                    .into(homeHolder.home_item_image);
        }

        homeHolder.ll_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    ArrayMap<String, DailySelect> collection = SharedPreferencesUtils.getInstance(context).getHashMapData("collection", DailySelect.class);
                    if(collection==null){
                        if(collection==null){
                            collection = new ArrayMap<>();
                        }
                        homeHolder.iv_collection.setImageResource(R.mipmap.collection);
                        homeHolder.home_item_cNumber.setText((dailySelect.getCollectionNumber()+1)+"");
                        dailySelect.setCollectionNumber(dailySelect.getCollectionNumber()+1);
                        collection.put(dailySelect.getImageID() + "", dailySelect);
                        SharedPreferencesUtils.getInstance(context).putHashMapData("collection",collection);
                        collectionListener.onClickCollection(dailySelect, Constant.Collection.COLLECTION_TYPE_ADD);
                    }else{
                        //不为空，判断是否包含此图片ID
                        if(!collection.containsKey(dailySelect.getImageID()+"")){
                            if(collection==null){
                                collection = new ArrayMap<>();
                            }
                            homeHolder.iv_collection.setImageResource(R.mipmap.collection);
                            homeHolder.home_item_cNumber.setText((dailySelect.getCollectionNumber()+1)+"");
                            dailySelect.setCollectionNumber(dailySelect.getCollectionNumber()+1);
                            collection.put(dailySelect.getImageID() + "", dailySelect);
                            SharedPreferencesUtils.getInstance(context).putHashMapData("collection",collection);
                            collectionListener.onClickCollection(dailySelect, Constant.Collection.COLLECTION_TYPE_ADD);
                            return;
                        }
                        homeHolder.iv_collection.setImageResource(R.mipmap.uncollection);
                        if(collection.size()>0){
                            DailySelect dailySelect1 = collection.get(dailySelect.getImageID()+"");
                            if(dailySelect1.getCollectionNumber()>=1){
                                homeHolder.home_item_cNumber.setText((dailySelect1.getCollectionNumber()-1)+"");
                            }else{
                                homeHolder.home_item_cNumber.setText("0");
                            }
                        }else{
                            homeHolder.home_item_cNumber.setText("0");
                        }
                        collection.remove(dailySelect.getImageID()+"");//包含，就移除掉收藏的图片
                        SharedPreferencesUtils.getInstance(context).putHashMapData("collection",collection);
                        collectionListener.onClickCollection(dailySelect, Constant.Collection.COLLECTION_TYPE_CANCEL);
                    }
                }catch (Exception ex){
                    Log.i("TAG","收藏异常信息："+ex.getMessage().toString());
                    Toast.makeText(context,ex.getMessage().toString()+"",Toast.LENGTH_SHORT).show();
                }
            }
        });
        /**
         * Glide.get(this).clearDiskCache();//清理磁盘缓存 需要在子线程中执行
         Glide.get(this).clearMemory();//清理内存缓存  可以在UI主线程中进行
         * */
        return view;
    }

    public void refresh(List<DailySelect> listData,ArrayMap<String, DailySelect> collection) {
        if(this.collection!=null){
            this.collection.clear();
        }
        this.collection = collection;
        mData.addAll(listData);
        notifyDataSetChanged();
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
