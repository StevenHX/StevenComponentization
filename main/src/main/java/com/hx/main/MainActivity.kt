package com.hx.main

import android.Manifest
import android.widget.Button
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hx.steven.activity.BaseActivity
import com.hx.steven.activity.OnPermissionListener
import com.hx.steven.component.ProgressBarView
import com.hx.steven.constant.Constants
import com.hx.steven.util.BarColorUtils
import com.hx.steven.viewpageTransformer.ScaleInTransformer
import com.orhanobut.logger.Logger


@Route(path = Constants.A_MAIN)
class MainActivity : BaseActivity() {
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
    private var sheetDialogFragment: MySheetDialog? = null

    override fun initView() {
        RequestPermissions(
            object : OnPermissionListener {
                override fun onPermissionGranted() {
                    Logger.d("onPermissionGranted")
                }

                override fun onPermissionDenied(deniedList: MutableList<String>?) {
                    Logger.d(deniedList)
                }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
//        pbView = findViewById(R.id.pbView)
//        pbView.setMax(100)
//        pbView.setProgress(43f)
        BarColorUtils.setBarColor(this, "#ffffffff", true)

//        OkGo.get<String>("https://www.wanandroid.com/article/list/0/json")
//            .tag(this)
//            .execute(object : StringCallback() {
//                override fun onSuccess(response: Response<String>?) {
//                   Logger.d(response)
//                }
//
//                override fun onError(response: Response<String>?) {
//                    super.onError(response)
//                    Logger.e(response?.message())
//                }
//            })

        var btn1: Button = findViewById(R.id.hello)
        btn1.setOnClickListener {
//            if (null == sheetDialogFragment) sheetDialogFragment = MySheetDialog()
//            sheetDialogFragment?.show(supportFragmentManager, "dialog")
            ARouter.getInstance().build(Constants.A_LOGIN).navigation()
        }
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