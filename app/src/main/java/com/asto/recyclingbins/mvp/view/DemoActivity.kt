package com.asto.recyclingbins.mvp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.hardware.usb.UsbDevice
import android.net.Uri
import android.view.Surface
import androidx.recyclerview.widget.GridLayoutManager
import com.asto.recyclingbins.R
import com.asto.recyclingbins.adapter.DemoAdapter
import com.asto.recyclingbins.base.BaseActivity
import com.asto.recyclingbins.core.Common
import com.asto.recyclingbins.core.toSystemShowSetting
import kotlinx.android.synthetic.main.activity_demo.*
import java.text.DecimalFormat
import kotlin.concurrent.timer
import com.jiangdg.usbcamera.UVCCameraHelper
import com.serenegiant.usb.widget.CameraViewInterface
import android.os.Environment
import android.provider.MediaStore
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener
import com.uuzuche.lib_zxing.activity.CodeUtils
import java.io.File

/**
 * 功能汇总
 */
class DemoActivity : BaseActivity() {
    override val layoutId: Int
        get() = R.layout.activity_demo
    var adapter: DemoAdapter? = null
    override fun initData() {
        mRecyclerView.layoutManager = GridLayoutManager(this, 3)
        adapter = DemoAdapter(listOf("垃圾门", "投递门", "投递门面板", "灯", "风扇", "称重", "跳转显示设置界面", "语音播放测试", "测试定时器", "拍照","Ping","查询状态"))
        mRecyclerView.adapter = adapter
        showCamera()
        createQRCode()
    }

    override fun bindinOnClickListener() {
        var isGarbageDoorOpen = false
        var isDeliveryDoorOpen = false
        var isDeliveryDoorPanelOpen = false
        var isInsideLightOpen = false
        var isInsideFanOpen = false
        adapter?.setOnItemChildClickListener { _, _, position ->
            when (position) {
                //垃圾门
                0 -> {
                    isGarbageDoorOpen = !isGarbageDoorOpen
                    mSerialPortUtils?.doGarbageDoor(isGarbageDoorOpen)
                }
                //投递门
                1 -> {
                    isDeliveryDoorOpen = !isDeliveryDoorOpen
                    mSerialPortUtils?.doDeliveryDoor(isDeliveryDoorOpen)
                }
                //投递门面板
                2 -> {
                    isDeliveryDoorPanelOpen = !isDeliveryDoorPanelOpen
                    mSerialPortUtils?.doDeliveryDoorPanel(isDeliveryDoorPanelOpen)
                }
                //灯
                3 -> {
                    isInsideLightOpen = !isInsideLightOpen
                    mSerialPortUtils?.doInsideLight(isInsideLightOpen)
                }
                //风扇
                4 -> {
                    isInsideFanOpen = !isInsideFanOpen
                    mSerialPortUtils?.doInsideFan(isInsideFanOpen)
                }
                //称重
                5 -> {
                    mSerialPortUtils?.doWeigh()
                }
                //显示系统虚拟键盘
                6 -> {
                    toSystemShowSetting()
                }
                //语音播放
                7 -> {
                    mSpeechSynthesizer?.speak("箱门正在打开")
                }
                //定时器测试
                8 -> {
                    var timer = 10
                    timer("测试定时器", false, 0, 1000) {
                        runOnUiThread {
                            mResultTv.text = "倒计时:" + timer
                        }
                        timer--
                        if (timer == 0) this.cancel()
                    }
                }
                //打开摄像头
                9 -> {
                    capturePicture()
                }
                //PING（返回信息：投递门状态、取垃圾门状态、上面电磁锁状态、箱满状态、重量、箱内温度）
                10 -> {
                    mSerialPortUtils?.doInfo()
                }
                //查询状态
                11 -> {
                    mSerialPortUtils?.doStatus()
                }
            }
        }
    }

    var isPreview = false //是否已显示预览
    var isRequest = false //是否已连接USB
    var mCameraHelper: UVCCameraHelper? = null
    fun showCamera() {
        //监听SurfaceView创建或销毁
        mUVCCameraView.setCallback(object : CameraViewInterface.Callback {
            override fun onSurfaceChanged(view: CameraViewInterface?, surface: Surface?, width: Int, height: Int) {

            }

            override fun onSurfaceCreated(view: CameraViewInterface?, surface: Surface?) {
                if (!isPreview && mCameraHelper?.isCameraOpened == true) {
                    mCameraHelper?.startPreview(mUVCCameraView)
                    isPreview = true
                }
            }

            override fun onSurfaceDestroy(view: CameraViewInterface?, surface: Surface?) {
                if (isPreview && mCameraHelper?.isCameraOpened == true) {
                    mCameraHelper?.stopPreview()
                    isPreview = false
                }
            }

        })
        mCameraHelper = UVCCameraHelper.getInstance()
        //设置默认的预览大小
        mCameraHelper?.setDefaultPreviewSize(1280, 720)
        //设置默认帧格式，默认为UVCCameraHelper.Frame_FORMAT_MPEG
        //如果使用mpeg无法录制mp4，请尝试yuv
        // // mCameraHelper.setDefaultFrameFormat（UVCCameraHelper.FRAME_FORMAT_YUYV）;
        //监听检测和连接USB设备
        mCameraHelper?.initUSBMonitor(this, mUVCCameraView, object : UVCCameraHelper.OnMyDevConnectListener {
            // 与USB设备断开连接
            override fun onDisConnectDev(device: UsbDevice?) {
                showToast("USB设备断开连接")
            }

            // 插入USB设备
            @SuppressLint("NewApi")
            override fun onAttachDev(device: UsbDevice?) {
                // 请求打开摄像头
                if (!isRequest) {
                    isRequest = true
                    if (mCameraHelper != null) {
                        var index = 0
                        mCameraHelper?.usbDeviceList?.forEach {
                            if(it.productName == "USB Camera"){
                                mCameraHelper?.requestPermission(index)
                                return
                            }
                            index ++
                        }
                        showToast("未检测到USB摄像头")
                    }else{
                        showToast("未检测到USB设备，请重新插入")
                    }
                }
            }

            // 连接USB设备成功
            override fun onConnectDev(device: UsbDevice?, isConnected: Boolean) {
                if (!isConnected) {
                    showToast("连接失败，请检查分辨率参数是否正确")
                    isPreview = false
                } else {
                    isPreview = true
                }
            }

            // 拔出USB设备
            @SuppressLint("NewApi")
            override fun onDettachDev(device: UsbDevice?) {
                if (isRequest) {
                    // 关闭摄像头
                    isRequest = false
                    mCameraHelper?.closeCamera()
                    showToast(device?.deviceName + "已拨出")
                }
            }

        })
        mCameraHelper?.registerUSB()
    }

    fun capturePicture() {
        if (mCameraHelper == null || !mCameraHelper?.isCameraOpened!!) {
            showToast("抓拍异常，摄像头未开启")
            return
        }

        val picPath = Environment.getExternalStorageDirectory().path + "/DCIM/" + System.currentTimeMillis() + ".jpeg"
        mCameraHelper?.capturePicture(picPath) {
//            // 插入图库
            val file = File(it)
            MediaStore.Images.Media.insertImage(contentResolver, file.absolutePath, it, null)
            file.delete()
            // 发送广播，通知刷新图库的显示
            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://$it")))
            runOnUiThread {
                showToast("抓拍成功")
            }
        }
    }

    /**
     * 生成二维码
     */
    fun createQRCode(){
        val mBitmap = CodeUtils.createImage("真乖", 400, 400, BitmapFactory.decodeResource(resources, R.drawable.icon_91))
        mImageView.setImageBitmap(mBitmap)
    }

    override fun onResume() {
        super.onResume()
        // 恢复Camera预览
        if(mUVCCameraView != null){
            mUVCCameraView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        // 暂停Camera预览
        if(mUVCCameraView != null){
            mUVCCameraView.onPause()
        }
    }

    override fun onDestroy() {
        super .onDestroy()
        mSerialPortUtils?.mSerialPortManager?.closeSerialPort()
        if(mCameraHelper != null){
            mCameraHelper?.unregisterUSB()
        }
    }
}
