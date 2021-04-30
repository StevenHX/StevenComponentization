package com.hx.main;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hx.steven.activity.BaseActivity;
import com.hx.steven.component.SuperTextView;
import com.hx.steven.constant.Constants;

@Route(path = Constants.A_WELCOME)
public class WelcomeActivity extends BaseActivity {
    private SuperTextView tvName;

    @Override
    protected void initView() {
        tvName = findViewById(R.id.tvName);

        Animation anim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.splash);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                tvName.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                openMain();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tvName.startAnimation(anim);
    }

    @Override
    protected int getContentId() {
        return R.layout.welcome_activity;
    }

    private void openMain() {
        ARouter.getInstance().build(Constants.A_MAIN).navigation();
        finish();
    }
}
