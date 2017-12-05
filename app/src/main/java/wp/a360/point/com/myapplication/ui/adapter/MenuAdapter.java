package wp.a360.point.com.myapplication.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import wp.a360.point.com.myapplication.R;

/**
 * Created by DN on 2017/12/5.
 */

public class MenuAdapter extends BaseAdapter {

    private Context context;
    private String [] menuName;
    private int []  menuDraw;
    private LayoutInflater inflater;
    public MenuAdapter(Context context,String [] menuName,int []  menuDraw){
        this.context = context;
        this.menuName = menuName;
        this.menuDraw = menuDraw;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return menuName.length;
    }

    @Override
    public Object getItem(int i) {
        return menuName[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MenuHolder menuHolder;
        if(view==null){
            menuHolder = new MenuHolder();
            view = inflater.inflate(R.layout.menu_list_item_layout,null);
            x.view().inject(menuHolder,view);
            view.setTag(menuHolder);
        }else{
            menuHolder = (MenuHolder) view.getTag();
        }
        menuHolder.menu_item_name.setText(menuName[i]+"");
        menuHolder.menu_item_icon.setImageResource(menuDraw[i]);
        return view;
    }

    class MenuHolder {
        @ViewInject(R.id.menu_item_name)
        TextView menu_item_name;
        @ViewInject(R.id.menu_item_icon)
        ImageView menu_item_icon;
    }
}
