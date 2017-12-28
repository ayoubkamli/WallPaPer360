package wp.a360.point.com.myapplication.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.wp.point.qj.jb.R;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.SettingService;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import wp.a360.point.com.myapplication.ui.base.BaseActivity;
import wp.a360.point.com.myapplication.ui.constant.Constant;

/**
 * Created by DN on 2017/12/18.
 */

public class GuideActivity extends BaseActivity {
    @ViewInject(R.id.iv_guide)
    private ImageView iv_guide;

    private BaseAnimatorSet mBasIn;
    private BaseAnimatorSet mBasOut;
    public void setBasIn(BaseAnimatorSet bas_in) {
        this.mBasIn = bas_in;
    }
    public void setBasOut(BaseAnimatorSet bas_out) {
        this.mBasOut = bas_out;
    }

    @Override
    public void initParms(Bundle parms) {
        setAllowFullScreen(true);
        setScreenRoate(false);
        setSteepStatusBar(false);
        setSetActionBarColor(true, R.color.white);// 设置状态栏颜色
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.guide_activity;
    }

    @Override
    public void initView(View view) {
        x.view().inject(this);
        mBasIn = new BounceTopEnter();
        mBasOut = new SlideBottomExit();
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        runOnUiThread(new Runnable() {
            public void run() {
                iv_guide.setBackground(getResources().getDrawable(R.mipmap.splash));
                startAnimAndTime();
            }
        });
    }

    @Override
    public void widgetClick(View v) {

    }

    private Handler mHandler = new Handler();
    /**
     * 播放动画和倒计时
     */
    private int mTime = 3;

    protected void startAnimAndTime() {
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.8f, 1.0f);
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(scaleAnimation);
            animationSet.addAnimation(alphaAnimation);
            animationSet.setDuration(3000);
            animationSet.setFillAfter(true);
            iv_guide.startAnimation(animationSet);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTime--;
                    if(mTime>0){
                        mHandler.postDelayed(this, 1000);
                    }else{
                        startMain();
                    }
                }
            }, 1000);
    }

    private void startMain() {
        // 先判断是否有权限。
        if(AndPermission.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //有权限
            startActivity(new Intent(GuideActivity.this, MainActivity.class));
            finish();
        } else {
            // 申请权限。
            AndPermission.with(GuideActivity.this)
                    .requestCode(Constant.PERMISSION_CODE)
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .send();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
    }

    private PermissionListener listener = new PermissionListener() {
        // 权限申请成功回调。
        @Override
        public void onSucceed(int requestCode, List<String> grantPermissions) {
            startActivity(new Intent(GuideActivity.this, MainActivity.class));
            finish();
        }

        // 权限申请失败回调。
        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            if (AndPermission.hasAlwaysDeniedPermission(GuideActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                //AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING).show();
                 //第二种：用自定义的提示语。
//                 AndPermission.defaultSettingDialog(GuideActivity.this, Constant.REQUEST_CODE_SETTING)
//                 .setTitle("权限申请失败")
//                 .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
//                 .setPositiveButton("好，去设置")
//                 .show();
                // 第三种：自定义dialog样式。
                final SettingService settingService =AndPermission.defineSettingDialog(GuideActivity.this, Constant.REQUEST_CODE_SETTING);
                final MaterialDialog dialog = new MaterialDialog(mContext);
                dialog.content(
                        "我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                        .btnText("取消", "确定")//
                        .showAnim(mBasIn)//
                        .dismissAnim(mBasOut)//
                        .show();
                dialog.setOnBtnClickL(
                        new OnBtnClickL() {//left btn click listener
                            @Override
                            public void onBtnClick() {
                                settingService.cancel();
                                dialog.dismiss();
                            }
                        },
                        new OnBtnClickL() {//right btn click listener
                            @Override
                            public void onBtnClick() {
                                settingService.execute();
                                dialog.dismiss();
                            }
                        }
                );
            }
        }
    };
}
