package com.hx.main

import android.Manifest
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.animation.AlphaInAnimation
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.hx.steven.activity.BaseActivity
import com.hx.steven.activity.OnPermissionListener
import com.hx.steven.component.DividerItemDecoration
import com.hx.steven.constant.Constants
import com.hx.steven.util.DeviceUtil
import com.orhanobut.logger.Logger
import com.tencent.mmkv.MMKV


@Route(path = Constants.A_MAIN)
class MainActivity : BaseActivity(), OnItemClickListener {
    private var recyclerView: RecyclerView? = null
    private var adapter: ExampleAdapter? = null
    private var exampleData: List<ExampleBean>? = null

    private lateinit var imeiTv: AppCompatTextView
    override fun getContentId(): Int {
        return R.layout.main_activity
    }

    override fun initView() {
        imeiTv = findViewById(R.id.main_imei)
        recyclerView = findViewById(R.id.example_recycle)
        recyclerView?.layoutManager = GridLayoutManager(this, 3)
        recyclerView?.addItemDecoration(DividerItemDecoration().setDrawOuterBorder(true))
        exampleData = listOf(
            ExampleBean("module1", R.drawable.module),
            ExampleBean("module2", R.drawable.wirehouse)
        )
        adapter = ExampleAdapter(R.layout.main_example_item, exampleData)
        adapter?.adapterAnimation = AlphaInAnimation()
        recyclerView?.adapter = adapter
        adapter?.setOnItemClickListener(this)

        RequestPermissions(
            object : OnPermissionListener {
                override fun onPermissionGranted() {
                    Logger.d("onPermissionGranted")
                    val imei = DeviceUtil.getDeviceImei(this@MainActivity, 0)
                    imeiTv.text = imei
                    MMKV.defaultMMKV().encode("imei", imei)
                }

                override fun onPermissionDenied(deniedList: MutableList<String>?) {
                    Logger.d(deniedList)
                    finish()
                }
            }, Manifest.permission.READ_PHONE_STATE
        )
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        var name = exampleData?.get(position)?.name
        when (name) {
            "module1" -> {
            }
            "module2" -> {
            }
        }
    }

}