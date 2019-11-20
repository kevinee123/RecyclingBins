package com.asto.recyclingbins.util

import android.annotation.SuppressLint
import com.asto.recyclingbins.R
import com.asto.recyclingbins.base.BaseActivity
import com.asto.recyclingbins.core.Common
import com.asto.recyclingbins.listener.OnSerialPortListener
import com.kongqw.serialportlibrary.Device
import com.kongqw.serialportlibrary.SerialPortFinder
import com.kongqw.serialportlibrary.SerialPortManager
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener
import java.io.File

/**
 *
 * 开始位	长度	地址	序列号	命令类型	超时时间1	超时时间2	数据位[可选]	         CRC	                 结束位
 * AA	     08 	01	     66	     02	           27	       10	    01	01	                 **	                       DD
 * <-----------固定格式--------->             <-----固定格式------->    根据操作来变    通过[长度,数据位] 来生成       <--固定-->
 *
 * 回收箱指令工具类
 */
class SerialPortUtils private constructor(
    val activity: BaseActivity,
    val onSerialPortListener: OnSerialPortListener
) {

    private var device: Device? = null
    var mSerialPortManager: SerialPortManager
    var mFullData = ""//完整数据

    companion object {
        private var instance: SerialPortUtils? = null

        fun get(
            activity: BaseActivity,
            onSerialPortListener: OnSerialPortListener
        ): SerialPortUtils {
            if (instance == null) {
                instance =
                    SerialPortUtils(activity, onSerialPortListener)
            }
            return instance!!
        }
    }

    init {
        mSerialPortManager = SerialPortManager()
        //打开串口监听
        mSerialPortManager.setOnOpenSerialPortListener(
            object : OnOpenSerialPortListener {
                override fun onSuccess(device: File?) {
                    activity.runOnUiThread {
                        activity.showToast(R.string.open_serial_port_success)
                    }
                }

                override fun onFail(device: File?, status: OnOpenSerialPortListener.Status?) {
                    activity.runOnUiThread {
                        if (status == OnOpenSerialPortListener.Status.NO_READ_WRITE_PERMISSION) {
                            activity.showToast(
                                activity.getString(R.string.open_serial_port_fail) + " : " + activity.getString(
                                    R.string.mo_read_write_permissions
                                )
                            )
                        } else {
                            activity.showToast(R.string.open_serial_port_fail)
                        }
                    }
                }
            })
        mSerialPortManager.setOnSerialPortDataListener( //串口发送/接受数据监听
            object : OnSerialPortDataListener {
                override fun onDataReceived(byteArray: ByteArray) {
                    //byteArray ： 接收的数据转16进制
                    val receivedData = byteToHex(byteArray)//全小写
                    //验证数据完整性
                    if (checkReceivedData(receivedData)) {
                        activity.runOnUiThread {
                            onSerialPortListener.onResult(mFullData)
                            mFullData = ""
                        }
                    }
                }

                override fun onDataSent(byteArray: ByteArray) {
                    //byteArray ： 发送的数据
                    val sendData = byteToHex(byteArray)
                    activity.runOnUiThread {
                        onSerialPortListener.onSend(sendData)
                    }
                }
            })
        initSerialportCommunication()
    }

    /**
     * 验证接收数据完整性
     */
    fun checkReceivedData(receivedData: String): Boolean {
        //验证 起始符 && 结尾符 && crc码
        var isFull = receivedData.substring(0, 2) == "aa"
                && receivedData.substring(receivedData.length - 2) == "dd"
                && Crc8.getInstance().compute(receivedData.substring(2,receivedData.length-4)) == receivedData.substring(receivedData.length -4,receivedData.length -2)
        if (isFull) {
            mFullData = receivedData
        } else {
            mFullData += receivedData
            //验证 起始符 && 结尾符 && crc码
            isFull = mFullData.substring(0, 2) == "aa"
                    && mFullData.substring(mFullData.length - 2) == "dd"
                    && Crc8.getInstance().compute(mFullData.substring(2,mFullData.length-4)) == mFullData.substring(mFullData.length -4,mFullData.length -2)
        }
        return isFull
    }

    //初始化串口通信
    private fun initSerialportCommunication() {
        //获取device
        val serialPortFinder = SerialPortFinder()
        val devices = serialPortFinder.devices
        for (d in devices) {
            if (d.name == "ttymxc1") {
                device = d
            }
        }
        if (device == null) {
            activity.showToast(R.string.device_is_null)
            return
        }
        //打开串口
        val b = mSerialPortManager.openSerialPort(device?.file, 115200)
        if (!b) {
            activity.showToast(activity.getString(R.string.open_serial_port_fail))
        }
    }

    /**
     * 生成完整指令
     * @param commandType : 命令类型
     * @param data : 数据
     */
    private fun builderInstruction(commandType: String, data: String): String {
        val dataLength = 6 + (data.length / 2)
        return send(
            "AA0" + dataLength + "0166" + commandType + "2710" + data + Crc8.getInstance().compute(
                "0" + dataLength + "0166" + commandType + "2710" + data
            ) + "DD"
        )
    }

    private fun builderInstruction(commandType: String): String {
        return builderInstruction(commandType, "")
    }

    /**
     * 发送指令
     * @param instruction : 完整指令
     */
    private fun send(instruction: String): String {
        mSerialPortManager.sendBytes(hexToByte(instruction))
        return instruction
    }


    /**
     * 底部垃圾门操作
     * @param isOpenOrClose : 打开或者关闭 true 打开 false 关闭
     */
    fun doGarbageDoor(isOpenOrClose: Boolean): String {
        if (isOpenOrClose) {
            return builderInstruction(
                Common.SERIAL_PORT_TYPE_DOOR,
                Common.SERIAL_PORT_DATA_DOOR_GARBAGE + Common.SERIAL_PORT_OPEN
            )
        } else {
            return builderInstruction(
                Common.SERIAL_PORT_TYPE_DOOR,
                Common.SERIAL_PORT_DATA_DOOR_GARBAGE + Common.SERIAL_PORT_CLOSE
            )
        }
    }

    /**
     * 投递门操作
     * @param isOpenOrClose : 打开或者关闭 true 打开 false 关闭
     */
    fun doDeliveryDoor(isOpenOrClose: Boolean): String {
        if (isOpenOrClose) {
            return builderInstruction(
                Common.SERIAL_PORT_TYPE_DOOR,
                Common.SERIAL_PORT_DATA_DOOR_DELIVERY + Common.SERIAL_PORT_OPEN
            )
        } else {
            return builderInstruction(
                Common.SERIAL_PORT_TYPE_DOOR,
                Common.SERIAL_PORT_DATA_DOOR_DELIVERY + Common.SERIAL_PORT_CLOSE
            )
        }
    }

    /**
     * 投递门面板操作
     * @param isOpenOrClose : 打开或者关闭 true 打开 false 关闭
     */
    fun doDeliveryDoorPanel(isOpenOrClose: Boolean): String {
        if (isOpenOrClose) {
            return builderInstruction(
                Common.SERIAL_PORT_TYPE_DOOR,
                Common.SERIAL_PORT_DATA_DOOR_DELIVERY_PANEL + Common.SERIAL_PORT_OPEN
            )
        } else {
            return builderInstruction(
                Common.SERIAL_PORT_TYPE_DOOR,
                Common.SERIAL_PORT_DATA_DOOR_DELIVERY_PANEL + Common.SERIAL_PORT_CLOSE
            )
        }
    }


    /**
     * 垃圾箱内拍照指示灯操作
     * @param isOpenOrClose : 打开或者关闭 true 打开 false 关闭
     */
    fun doInsideLight(isOpenOrClose: Boolean): String {
        if (isOpenOrClose) {
            return builderInstruction(
                Common.SERIAL_PORT_TYPE_LIGHT,
                Common.SERIAL_PORT_DATA_LIGHT_INSIDE + Common.SERIAL_PORT_OPEN
            )
        } else {
            return builderInstruction(
                Common.SERIAL_PORT_TYPE_LIGHT,
                Common.SERIAL_PORT_DATA_LIGHT_INSIDE + Common.SERIAL_PORT_CLOSE
            )
        }
    }

    /**
     * 垃圾箱内风扇操作
     * @param isOpenOrClose : 打开或者关闭 true 打开 false 关闭
     */
    fun doInsideFan(isOpenOrClose: Boolean): String {
        if (isOpenOrClose) {
            return builderInstruction(
                Common.SERIAL_PORT_TYPE_FAN,
                Common.SERIAL_PORT_DATA_FAN_INSIDE + Common.SERIAL_PORT_OPEN
            )
        } else {
            return builderInstruction(
                Common.SERIAL_PORT_TYPE_FAN,
                Common.SERIAL_PORT_DATA_FAN_INSIDE + Common.SERIAL_PORT_CLOSE
            )
        }
    }

    /**
     * 获取垃圾回收箱信息
     */
    fun doInfo() {
        builderInstruction(Common.SERIAL_PORT_TYPE_INFO)
    }

    /**
     * 称重
     */
    fun doWeigh() {
        builderInstruction(Common.SERIAL_PORT_TYPE_WEIGH)
    }

    /**
     * 重启控制面板
     */
    fun doRestartControlPanel() {
        builderInstruction(Common.SERIAL_PORT_TYPE_RESTART_CONTROL_PANEL)
    }

    /**
     * 查询状态
     */
    fun doStatus() {
        builderInstruction(Common.SERIAL_PORT_TYPE_STATUS)
    }

    /**
     * 获取Device版本
     */
    fun doDeviceVersion() {
        builderInstruction(Common.SERIAL_PORT_TYPE_DEVICE_VERSION)
    }

    /**
     * 初始化毛重
     */
    fun doInitWeigh() {
        builderInstruction(Common.SERIAL_PORT_TYPE_INIT_WEIGH)
    }

    /**
     * 校准称重系数
     */
    fun doCalwWeigh() {
        builderInstruction(Common.SERIAL_PORT_TYPE_CALW_WEIGH)
    }

    /**
     * 保存机器Id
     */
    fun doSaveMachineId() {
        builderInstruction(Common.SERIAL_PORT_TYPE_SAVE_ID)
    }

    /**
     * 获取机器Id
     */
    fun doGetMachineId() {
        builderInstruction(Common.SERIAL_PORT_TYPE_GET_ID)
    }

    /**
     * 设置设备时间
     */
    fun doSetDate() {
        builderInstruction(Common.SERIAL_PORT_TYPE_SET_DATE)
    }

    /**
     * 查询机器错误
     */
    fun doQueryError() {
        builderInstruction(Common.SERIAL_PORT_TYPE_QUERY_ERROR)
    }

    /**
     * 清除机器错误
     */
    fun doClearError() {
        builderInstruction(Common.SERIAL_PORT_TYPE_CLEAR_ERROR)
    }

    /**
     * 切换Log开关
     */
    fun doChangeLog() {
        builderInstruction(Common.SERIAL_PORT_TYPE_CHANGE_LOG)
    }

    /**
     * 配置机器参数
     */
    fun doSetting() {
        builderInstruction(Common.SERIAL_PORT_TYPE_SETTING)
    }

    /**
     * 控制面板上LED灯
     */
    fun doLED() {
        builderInstruction(Common.SERIAL_PORT_TYPE_LED)
    }

    /**
     * 控制板红外测距
     */
    fun doInfrared() {
        builderInstruction(Common.SERIAL_PORT_TYPE_INFRARED)
    }

    /**
     * hex转byte数组
     * @param hex
     * @return
     */
    fun hexToByte(hex: String): ByteArray {
        var m = 0
        var n = 0
        val byteLen = hex.length / 2 // 每两个字符描述一个字节
        val ret = ByteArray(byteLen)
        for (i in 0 until byteLen) {
            m = i * 2 + 1
            n = m + 1
//        val intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n))
//        ret[i] = java.lang.Byte.valueOf(intVal.toByte())
            ret[i] = Integer.parseInt(hex.substring(i * 2, m) + hex.substring(m, n), 16).toByte()
        }
        return ret
    }


    /**
     * byte数组转hex
     * @return
     */
    @SuppressLint("DefaultLocale")
    fun byteToHex(bytes: ByteArray): String {
        var strHex: String
        val sb = StringBuilder("")
        for (n in bytes.indices) {
            strHex = Integer.toHexString(bytes[n].toInt() and 0xff)
            sb.append(if (strHex.length == 1) "0$strHex" else strHex) // 每个字节由两个字符表示，位数不够，高位补0
        }
        return sb.toString().trim { it <= ' ' }.toLowerCase()
    }
}