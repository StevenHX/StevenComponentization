package com.hx.main

import android.Manifest
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.hx.steven.activity.BaseActivity
import com.hx.steven.component.ProgressBarView
import com.hx.steven.constant.Constants
import com.hx.steven.di.Girl
import com.hx.steven.util.BarColorUtils
import com.hx.steven.viewpageTransformer.ScaleInTransformer
import org.koin.android.ext.android.inject

@Route(path = Constants.A_MAIN)
class MainActivity : BaseActivity() {
    private val girl1 by inject<Girl>()
    lateinit var viewPager: ViewPager
    lateinit var pbView: ProgressBarView
    private var adapter: PageAdapter? = null
    private val images = intArrayOf(
        R.drawable.a,
        R.drawable.b,
        R.drawable.c,
        R.drawable.a,
        R.drawable.b,
        R.drawable.c
    )

    override fun initView() {
        RequestPermissions(
            0,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
        )
        /**viewPager变换操作 */
        viewPager = findViewById(R.id.id_viewpager)
        viewPager.setOffscreenPageLimit(3)
        adapter = PageAdapter(this, images)
        viewPager.setPageTransformer(false, ScaleInTransformer())
        viewPager.setAdapter(adapter)
        viewPager.setCurrentItem(1)
        /**自定义progressView */
        pbView = findViewById(R.id.pbView)
        pbView.setMax(100)
        pbView.setProgress(43f)
        BarColorUtils.setBarColor(this, "#C1FFC1", true)
        girl1.userName = "新垣结衣"
        girl1.age=18
        System.out.println(girl1.info())
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun getContentId(): Int {
        return R.layout.main_activity
    }

    init {
        setEnableMultiple(false)
    }
}