package wp.a360.point.com.myapplication.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import wp.a360.point.com.myapplication.R;
import wp.a360.point.com.myapplication.ui.base.BaseFragment;

/**
 * Created by DN on 2017/11/24.
 */

public class HomeFragment extends BaseFragment {
    @ViewInject(R.id.switch_slidemenu)
    private Button switch_slidemenu;

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

    @Override
    protected void initData() {
        switch_slidemenu.setOnClickListener(this);
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
