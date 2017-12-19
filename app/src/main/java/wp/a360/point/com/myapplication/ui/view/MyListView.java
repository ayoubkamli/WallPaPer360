package wp.a360.point.com.myapplication.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import wp.a360.point.com.myapplication.ui.entity.DailySelect;

/**
 * Created by DN on 2017/12/7.
 */

public class MyListView extends ListView implements AbsListView.OnScrollListener{

    private List<DailySelect> mData = new ArrayList<>();
    public void initData(List<DailySelect> mData){
            this.mData.addAll(mData);
    }
    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    /**
     *监听着ListView的滑动状态改变。官方的有三种状态SCROLL_STATE_TOUCH_SCROLL、SCROLL_STATE_FLING、SCROLL_STATE_IDLE：
     * SCROLL_STATE_TOUCH_SCROLL：手指正拖着ListView滑动
     * SCROLL_STATE_FLING：ListView正自由滑动
     * SCROLL_STATE_IDLE：ListView滑动后静止
     * */
    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        switch (scrollState){
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                // 第一个可见 item 的 position
                int first = this.getFirstVisiblePosition();
                // 最后一个可见 item 的 position
                int last = this.getLastVisiblePosition();
                // 屏幕上可见 item 的总数
                int onScreenCount = this.getChildCount();





                break;
        }
    }
    /**
     * firstVisibleItem: 表示在屏幕中第一条显示的数据在adapter中的位置
     * visibleItemCount：则表示屏幕中最后一条数据在adapter中的数据，
     * totalItemCount:则是在adapter中的总条数
     * */
    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
