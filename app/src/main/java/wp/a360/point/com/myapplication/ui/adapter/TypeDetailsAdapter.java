package wp.a360.point.com.myapplication.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import wp.a360.point.com.myapplication.R;
import wp.a360.point.com.myapplication.ui.entity.DailySelect;

/**
 * Created by DN on 2017/12/9.
 */

public class TypeDetailsAdapter extends BaseAdapter {
    private Context context;
    private List<DailySelect> mData;
    private LayoutInflater inflater;
    public TypeDetailsAdapter(Context context, List<DailySelect> mData){
        this.context = context;
        this.mData = mData;
        inflater = LayoutInflater.from(context);
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

    public void cleanRefresh(List<DailySelect> mData1){
        if(mData!=null){
            mData.clear();
        }
        this.mData = mData1;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TypeDetailsHolder  tdHolder;
        if(view==null){
            tdHolder = new TypeDetailsHolder();
            view = inflater.inflate(R.layout.type_details_item_layout,null);
            x.view().inject(tdHolder,view);
            view.setTag(tdHolder);
        }else{
            tdHolder = (TypeDetailsHolder) view.getTag();
        }
            if(mData!=null){
                DailySelect dailySelect = mData.get(i);
                if(dailySelect!=null){
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
                                .centerCrop()
                                .into(tdHolder.type_details_item_image);
                }
            }

        }
        return view;
    }
    public void refresh(List<DailySelect> listData) {
        mData.addAll(listData);
        notifyDataSetChanged();
    }

    class TypeDetailsHolder{
        @ViewInject(R.id.type_details_item_image)
        ImageView type_details_item_image;
    }
}