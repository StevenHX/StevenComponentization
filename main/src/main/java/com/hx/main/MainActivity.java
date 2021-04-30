package com.hx.main;

import android.Manifest;
import androidx.viewpager.widget.ViewPager;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.hx.steven.activity.BaseActivity;
import com.hx.steven.component.ProgressBarView;
import com.hx.steven.constant.Constants;
import com.hx.steven.util.BarColorUtils;
import com.hx.steven.viewpageTransformer.ScaleInTransformer;

@Route(path = Constants.A_MAIN)
public class MainActivity extends BaseActivity {
    {
        setEnableMultiple(false);
    }

    ViewPager viewPager;
    ProgressBarView pbView;
    @Override
    protected void initView() {
        RequestPermissions(0,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE);

        /**viewPager变换操作*/
        viewPager = findViewById(R.id.id_viewpager);
        viewPager.setOffscreenPageLimit(3);
        adapter = new pageAdapter(this, images);
        viewPager.setPageTransformer(false, new ScaleInTransformer());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        /**自定义progressView*/
        pbView = findViewById(R.id.pbView);
        pbView.setMax(100);
        pbView.setProgress(43);
        BarColorUtils.setBarColor(this, "#C1FFC1", true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private pageAdapter adapter;
    private int[] images = new int[]{R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.a, R.drawable.b, R.drawable.c};

    @Override
    protected int getContentId() {
        return R.layout.main_activity;
    }

}
