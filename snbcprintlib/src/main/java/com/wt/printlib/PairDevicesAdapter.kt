package com.wt.printlib

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView

class PairDevicesAdapter(arrayData: MutableList<PairDevicesBean>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listData: List<PairDevicesBean> = arrayData

    private var listener: ConnectClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.pair_device_item, parent, false)
        return PairViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PairViewHolder) {
            holder.nameTv?.text = listData[position].name
            holder.ipTv?.text = listData[position].ip
            holder.connectBtn?.setOnClickListener {
                holder.connectBtn?.isEnabled = false
                holder.connectBtn?.setImageResource(R.drawable.loading)
                listener?.onConnectClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    class PairViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameTv: AppCompatTextView? = null
        var ipTv: AppCompatTextView? = null
        var connectBtn: AppCompatImageView? = null

        init {
            nameTv = itemView.findViewById(R.id.pair_devices_name)
            ipTv = itemView.findViewById(R.id.pair_devices_ip)
            connectBtn = itemView.findViewById(R.id.pair_devices_btn)
            connectBtn?.setImageResource(R.drawable.connect)
        }
    }

    interface ConnectClickListener {
        fun onConnectClick(position: Int)
    }

    fun setOnConnectClickListener(listener1: ConnectClickListener) {
        listener = listener1
    }


    fun setData(devicesData: MutableList<PairDevicesBean>) {
        listData = devicesData
        notifyDataSetChanged()
    }
}