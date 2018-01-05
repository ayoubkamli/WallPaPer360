package wp.a360.point.com.myapplication.ui.activity;

import android.app.Application;
import android.content.Context;

import org.xutils.BuildConfig;
import org.xutils.x;

/**
 * Created by DN on 2017/12/8.
 */

public class MyAppLication extends Application {

    public static Context context = null;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        x.Ext.init(this);
        x.Ext.setDebug(false); // 是否输出debug日志, BuildConfig.DEBUG开启debug会影响性能.
    }
}
