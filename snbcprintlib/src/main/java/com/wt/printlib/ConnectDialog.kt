package com.wt.printlib

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.util.*
import kotlin.concurrent.schedule


class ConnectDialog : DialogFragment() {

    private lateinit var pairDevicesRefresh: SwipeRefreshLayout
    private lateinit var pairDevicesRecycle: RecyclerView
    private lateinit var pairDevicesAdapter: PairDevicesAdapter
    var devicesData: MutableList<PairDevicesBean> = mutableListOf()

    private var listener: ConnectListener? = null

    interface ConnectListener {
        fun onConnect()
    }

    fun setOnConnectListener(listener1: ConnectListener) {
        listener = listener1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.connect_dialog, container, false)
        pairDevicesRefresh = view.findViewById(R.id.pair_devices_refresh)
        pairDevicesRecycle = view.findViewById(R.id.pair_devices_recycle)
        pairDevicesRecycle.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        pairDevicesRecycle.addItemDecoration(DividerItemDecoration().setDrawOuterBorder(true))
        pairDevicesAdapter = PairDevicesAdapter(devicesData)
        pairDevicesRecycle.adapter = pairDevicesAdapter

        pairDevicesAdapter.setOnConnectClickListener(object :
            PairDevicesAdapter.ConnectClickListener {
            override fun onConnectClick(position: Int) {
                try {
                    ConnectUtils.ConnectDevice(devicesData[position].ip, devicesData[position].name)
                    Toast.makeText(context,resources.getText(R.string.pps_link_ok),Toast.LENGTH_LONG).show()
                    listener?.onConnect()
                } catch (e: Exception) {
                    Toast.makeText(context,resources.getText(R.string.pps_link_error),Toast.LENGTH_LONG).show()
                }
            }
        })

        pairDevicesRefresh.setOnRefreshListener {
            disCoverDevices()
            pairDevicesRefresh.isRefreshing = false
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timer().schedule(500) {
            view.post {
                disCoverDevices()
            }
        }
    }

    fun disCoverDevices() {
        val devices = ConnectUtils.DiscoverDevice()
        if (devices.isNotEmpty()) {
            devicesData = mutableListOf()
            for (device in devices) {
                devicesData.add(PairDevicesBean(device.name, device.address))
            }
            pairDevicesAdapter.setData(devicesData)
        } else {
            return
        }
    }

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val dm = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(dm)
            dialog?.window?.setLayout(
                (dm.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}
