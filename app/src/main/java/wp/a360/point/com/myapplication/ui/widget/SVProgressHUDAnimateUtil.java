package wp.a360.point.com.myapplication.ui.widget;



import android.view.Gravity;

import wp.a360.point.com.myapplication.R;


/**
 * Created by Sai on 15/8/16.
 */
public class SVProgressHUDAnimateUtil {
    private static final int INVALID = -1;
    static int getAnimationResource(int gravity, boolean isInAnimation) {
        switch (gravity) {
            case Gravity.TOP:
                return isInAnimation ? R.anim.em_svslide_in_top : R.anim.em_svslide_out_top;
            case Gravity.BOTTOM:
                return isInAnimation ? R.anim.em_svslide_in_bottom : R.anim.em_svslide_out_bottom;
            case Gravity.CENTER:
                return isInAnimation ? R.anim.em_svfade_in_center : R.anim.em_svfade_out_center;
            default:
                // This case is not implemented because we don't expect any other gravity at the moment
        }
        return INVALID;
    }
}
