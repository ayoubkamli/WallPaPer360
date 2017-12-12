package wp.a360.point.com.myapplication.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import wp.a360.point.com.myapplication.R;
import wp.a360.point.com.myapplication.ui.entity.DailySelect;

/**
 * Created by DN on 2017/12/8.
 */

public class RecyclerAdapter extends RecyclerView.Adapter {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.home_item_layout,null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);
        return new RecyclerHomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerHomeViewHolder rhvHolder = (RecyclerHomeViewHolder) holder;
        rhvHolder.position = position;
        DailySelect dailySelect = mData.get(position);
        rhvHolder.home_item_cNumber.setText(dailySelect.getCollectionNumber()+"");
        rhvHolder.home_item_date.setText(dailySelect.getImageDate()+"");
        Glide.with(context)
                .load(dailySelect.getImageUrl())
                //设置加载中图片
                .placeholder(R.drawable.test2) // can also be a drawable
                //加载失败图片
                .error(R.drawable.test2)
                //缓存源资源 result：缓存转换后的资源 none:不作任何磁盘缓存 all:缓存源资源和转换后的资源
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .thumbnail(0.1f) //设置缩略图支持
                .fitCenter()
                .into(rhvHolder.home_item_image);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public  interface OnRecyclerViewListener {
        void onItemClick(int position);
    }
    private OnRecyclerViewListener onRecyclerViewListener;
    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }
    private Context context;
    private List<DailySelect> mData;
    private LayoutInflater layoutInflater;
    public RecyclerAdapter(Context context, List<DailySelect> mData){
        this.context = context;
        this.mData = mData;
        layoutInflater = LayoutInflater.from(context);
    }

    class RecyclerHomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @ViewInject(R.id.home_item_image)
        ImageView home_item_image;
        @ViewInject(R.id.home_item_date)
        TextView home_item_date;
        @ViewInject(R.id.home_item_cNumber)
        TextView home_item_cNumber;
        public int position;
        public RecyclerHomeViewHolder(View itemView) {
            super(itemView);
            x.view().inject(itemView);
            home_item_image.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(position);
            }
        }
    }




}
