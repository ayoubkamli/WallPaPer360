package wp.a360.point.com.myapplication.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wp.a360.point.com.myapplication.R;

/**
 * Created by DN on 2017/11/24.
 */

public class SearchFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_activity, container, false);
    }
}
