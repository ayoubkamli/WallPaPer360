package wp.a360.point.com.myapplication.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import wp.a360.point.com.myapplication.R;
import wp.a360.point.com.myapplication.ui.entity.ImageType;

/**
 * Created by DN on 2017/12/7.
 */

public class SortAdapter extends BaseAdapter {

    private  Context context ;
    private List<ImageType> mData;
    private LayoutInflater layoutInflater;
    public SortAdapter(Context context ,List<ImageType> mData){
        this.context = context;
        this.mData = mData;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
            SortHolder sortHolder = null;
            if(view==null){
                sortHolder = new SortHolder();
                view = layoutInflater.inflate(R.layout.sort_item_layout,null);
                x.view().inject(sortHolder,view);
                view.setTag(sortHolder);
            }else{
                sortHolder = (SortHolder) view.getTag();
            }
            if(mData!=null){
                ImageType imageType = mData.get(i);
                if(imageType!=null){
                    sortHolder.sort_item_name.setText(imageType.getImageTypeName()+""); //分类名称
                    if(imageType.getImageTypeNum()>=0){
                        sortHolder.sort_item_number.setText(imageType.getImageTypeNum()+"张");
                    }else{
                        sortHolder.sort_item_number.setText("0张");
                    }

                    if(imageType.getImageTypeBackground()!=null){
                        Glide.with(context)
                                .load(imageType.getImageTypeBackground())
                                //设置加载中图片
                                .placeholder(R.mipmap.lodinging) // can also be a drawable
                                //加载失败图片
                                .error(R.mipmap.lodinging)
                                //缓存源资源 result：缓存转换后的资源 none:不作任何磁盘缓存 all:缓存源资源和转换后的资源
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .thumbnail(0.1f) //设置缩略图支持
                                .fitCenter()
                                .into(sortHolder.sort_background);
                }
            }

            }

            return view;
    }

    class SortHolder{
        @ViewInject(R.id.sort_item_number)
        TextView sort_item_number;
        @ViewInject(R.id.sort_item_name)
        TextView sort_item_name;
        @ViewInject(R.id.sort_background)
        ImageView sort_background;
    }



}
