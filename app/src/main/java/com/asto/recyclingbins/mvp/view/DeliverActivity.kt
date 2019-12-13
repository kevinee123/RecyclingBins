package com.asto.recyclingbins.mvp.view

import android.content.Intent
import android.hardware.usb.UsbDevice
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.Surface
import com.asto.recyclingbins.R
import com.asto.recyclingbins.base.BaseActivity
import com.asto.recyclingbins.bean.EventMessage
import com.asto.recyclingbins.bean.RecyclingTypeBean
import com.asto.recyclingbins.core.Common
import com.asto.recyclingbins.mvp.contract.DeliverContract
import com.asto.recyclingbins.mvp.presenter.DeliverPresenter
import com.jiangdg.usbcamera.UVCCameraHelper
import com.serenegiant.usb.widget.CameraViewInterface
import kotlinx.android.synthetic.main.activity_deliver.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.text.DecimalFormat
import java.util.*

/**
 * 作者 ：nxk
 * 日期 ：2019/12/9
 * 备注 ：投递页
 */
class DeliverActivity : BaseActivity(), DeliverContract.View {

    override val layoutId = R.layout.activity_deliver
    override val presenter = DeliverPresenter(this)

    var timerDate = 90
    var timer: Timer? = null
    var initWeight: Double? = null //初始化重量
    var lastWeight = 0.0

    var mCameraHelper: UVCCameraHelper? = null //箱内USB摄像头
    var isPreview = false //是否已显示预览

    override fun initData() {
        EventBus.getDefault().register(this)
        openCamera()
        presenter.showView()
    }

    override fun bindinOnClickListener() {
        mNextTv.setOnClickListener {
            //TODO 投递完成
            capturePicture() //先抓拍箱内照片 再执行其他操作
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun capturePictureSuccess(eventMessage: EventMessage<String>) {
        if (eventMessage.code == Common.EB_CODE_CAPTURE_PICTURE_SUCCESS) {
            presenter.deliverFinish(intent.extras?.getSerializable("bean") as RecyclingTypeBean)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        if (timerDate > 0) {
            timer?.cancel()
        }
//        if (mCameraHelper != null) {
//            // 释放资源
//            mCameraHelper?.release()
//            // 注销USB事件广播监听器
//            mCameraHelper?.unregisterUSB()
//            mCameraHelper = null
//        }
    }

    override fun showTimer(time: Int) {
        timerDate = time
        timer = kotlin.concurrent.timer("投递页定时器", false, 0, 1000) {
            runOnUiThread {
                mMsgTv.text = timerDate.toString() + "S"
            }
            timerDate--
            if (timerDate <= 0) {
                mSerialPortUtils?.doDeliveryDoor(false) //关闭投递门
                mSerialPortUtils?.doInsideLight(false) //关闭箱内灯
                mSpeechSynthesizer?.speak(getString(R.string.speak_msg_door_close))
                this.cancel()
                loginOut()
            }
        }
    }

    /**
     * 读取重量
     */
    @Subscribe
    fun getWeight(eventMessage: EventMessage<String>) {
        if (eventMessage.code == Common.SERIAL_PORT_TYPE_WEIGH) {
            val defaultDf = DecimalFormat("#0.00")
            var weigh = Integer.parseInt(eventMessage.data ?: "0", 16)
            //如果初始重量还没赋值，则进行赋值
            if (initWeight == null) initWeight = defaultDf.format(weigh.toDouble() / 100).toDouble()
            //记录最后重量
            lastWeight = defaultDf.format(weigh / 100).toDouble()
        }
    }


    /**
     * 打开摄像头
     */
    fun openCamera() {
        if (mCameraHelper != null) return
        try {
            mCameraHelper = UVCCameraHelper.getInstance()
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
            if (!isRequest) {
            //设置默认帧格式，默认为UVCCameraHelper.Frame_FORMAT_MPEG
            //如果使用mpeg无法录制mp4，请尝试yuv
            // // mCameraHelper.setDefaultFrameFormat（UVCCameraHelper.FRAME_FORMAT_YUYV）;
            //监听检测和连接USB设备
                //设置默认的预览大小
                mCameraHelper?.setDefaultPreviewSize(1280, 720)
                mCameraHelper?.initUSBMonitor(this, mUVCCameraView, object : UVCCameraHelper.OnMyDevConnectListener {
                    // 与USB设备断开连接
                    override fun onDisConnectDev(device: UsbDevice?) {
                        runOnUiThread {
                            showToast("USB设备断开连接")
                        }
                    }

                    // 插入USB设备
                    override fun onAttachDev(device: UsbDevice?) {
                        // 请求打开摄像头
                        if (!isRequest) {
                            isRequest = true
                            if (mCameraHelper != null) {
                                var index = 0
                                mCameraHelper?.usbDeviceList?.forEach {
                                    if (it.productName == "USB Camera") {
                                        mCameraHelper?.requestPermission(index)
                                        return
                                    }
                                    index++
                                }
                                runOnUiThread {
                                    showToast("未检测到USB摄像头")
                                }
                            } else {
                                runOnUiThread {
                                    showToast("未检测到USB设备，请重新插入")
                                }
                            }
                        }
                    }

                    // 连接USB设备成功
                    override fun onConnectDev(device: UsbDevice?, isConnected: Boolean) {
                        if (!isConnected) {
                            runOnUiThread {
                                showToast("连接失败，请检查分辨率参数是否正确")
                            }
                            isPreview = false
                        } else {
                            isPreview = true
                        }
                    }

                    // 拔出USB设备
                    override fun onDettachDev(device: UsbDevice?) {
//                        if (isRequest) {
////                            // 关闭摄像头
//                            isRequest = false
//                            mCameraHelper?.closeCamera()
//                            runOnUiThread {
//                                showToast(device?.deviceName + "已拨出")
//                            }
//                        }
                    }

                })
                mCameraHelper?.registerUSB()
            }
        } catch (e: Exception) {
            showToast(e.toString())
        }
    }

    /**
     * 抓拍箱内照片
     */
    override fun capturePicture() {
        if (!isPreview or !isRequest or !mCameraHelper?.isCameraOpened!!) {
            showToast("抓拍异常，请联系管理员")
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
                EventBus.getDefault()
                    .post(EventMessage<String>(Common.EB_CODE_CAPTURE_PICTURE_SUCCESS))
            }
        }
    }
}