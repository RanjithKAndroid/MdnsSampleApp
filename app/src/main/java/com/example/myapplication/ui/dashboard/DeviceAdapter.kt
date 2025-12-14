package com.example.myapplication.ui.dashboard

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.local.DeviceEntity

class DeviceAdapter(private val deviceEntityList: MutableList<DeviceEntity>, private val onClick:
(DeviceEntity)-> Unit): RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeviceViewHolder {
        val  view = LayoutInflater.from(parent.context).inflate(R.layout.simple_list_item_1,parent,false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: DeviceViewHolder,
        position: Int
    ) {
        val deviceEntity = deviceEntityList[position]
        holder.deviceDetail.text = " Name : ${deviceEntity.deviceName} IP Address : ${deviceEntity.deviceIPAddress}" +
                " Device Status: ${deviceEntity.deviceStatus} "
        holder.deviceDetail.setOnClickListener { onClick(deviceEntity) }
    }

    override fun getItemCount(): Int {
        return deviceEntityList.size
    }

    class DeviceViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val deviceDetail: TextView = view.findViewById(R.id.text1)
    }

    fun updateList(updatedDeviceEntityList: List<DeviceEntity>){
        deviceEntityList.clear()
        deviceEntityList.addAll(updatedDeviceEntityList)
        notifyDataSetChanged()
    }

}