package com.asto.recyclingbins.core

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import com.asto.recyclingbins.base.BaseActivity
import com.kongqw.serialportlibrary.Device
import com.kongqw.serialportlibrary.SerialPortFinder
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.umeng.commonsdk.UMConfigure

@SuppressLint("Registered")
class MyApplication : Application() {

    var activityList: ArrayList<BaseActivity> = ArrayList()

    override fun onCreate() {
        super.onCreate()
        //初始化Logger
        Logger.addLogAdapter(AndroidLogAdapter())
        //初始化友盟
        UMConfigure.init(this, "5db8f6910cafb2744c000e6b", getSerialNumber(), UMConfigure.DEVICE_TYPE_PHONE, null)
    }


    fun addActivity(activity: BaseActivity) {
        activityList.add(activity)
    }

    fun removeActivity(activity: BaseActivity) {
        activityList.remove(activity)
    }


    fun getActivityList(): MutableList<BaseActivity> {
        return activityList
    }

}
