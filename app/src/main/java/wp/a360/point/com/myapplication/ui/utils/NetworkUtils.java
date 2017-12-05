package wp.a360.point.com.myapplication.ui.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by DN on 2017/8/17.
 * 网络状态判断
 */

public class NetworkUtils {
    public static final int WIFI = 1;//wifi网络
    public static final int CMWAP = 1;//中国移动代理
    public static final int CMNET = 1;//CMNET上网

    /**
     * 网络是否可用
     *
     * @param
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
        } else {
            //NetworkInfo[] info = connectivity.getAllNetworkInfo();
            NetworkInfo mNetworkInfo = connectivity.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                /*for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }*/
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 网络类型
     * @param
     * @return 返回-1，表示没有网络
     * */
    /** 没有网络 */
    public static final int NETWORKTYPE_INVALID = -1;
    /** wap网络 */
    public static final int NETWORKTYPE_WAP = 1;
    /** 2G网络 */
    public static final int NETWORKTYPE_2G = 2;//2:2G网络，
    /** 3G和3G以上网络，或统称为快速网络 */
    public static final int NETWORKTYPE_3G = 3;
    /** wifi网络 */
    public static final int NETWORKTYPE_WIFI = 4;

    public static int getAPNType(Context context){
        int mNetWorkType=-1;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                mNetWorkType = TextUtils.isEmpty(proxyHost)? (isFastMobileNetwork(context) ? NETWORKTYPE_3G : NETWORKTYPE_2G): NETWORKTYPE_WAP;
            }
        } else {
            mNetWorkType = NETWORKTYPE_INVALID;
        }
        return mNetWorkType;
    }

    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:return true; // ~ 10-20 Mbps   3G网络
            case TelephonyManager.NETWORK_TYPE_IDEN:return false; // ~25 kbps   2G网络
            case TelephonyManager.NETWORK_TYPE_LTE:return true; // ~ 10+ Mbps 4G网络
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:return false; //没网络
            default:return false;
        }
    }

    /**
     * Gps是否打开
     *
     * @param context
     * @return
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        List<String> accessibleProviders = locationManager.getProviders(true);
        return accessibleProviders != null && accessibleProviders.size() > 0;
    }

    /**
     * wifi是否打开
     */
    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null
                && mgrConn.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED)
                || mgrTel.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    /**
     * 判断当前网络是否是wifi网络
     * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) {
     *
     * @param context
     * @return boolean
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前网络是否3G网络
     *
     * @param context
     * @return boolean
     */
    public static boolean is3G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }
}
