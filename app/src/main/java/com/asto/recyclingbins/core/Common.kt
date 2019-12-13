package com.asto.recyclingbins.core

class Common {
    //常量字段定义在里面
    companion object {
        const val APP_NAME = "RecyclingBins"
        const val BASE_URL = "http://test.money.golden-navi.com"//测试地址
        const val HTTP_SUCCESS = 0 //网络请求成功
        const val HTTP_LOGIN_OUT = 120//网络请求失败


        /**
         * 百度语音
         */
        const val BD_APP_ID = "17770013"
        const val BD_APP_KEY = "NAez1zEXm0mbSiDBth6AosFs"
        const val BD_APP_SECRET = "lHS6X7X9TZkztf6zUjvTYpHh3vyAu50P"

        /**
         * EventBus
         */
        const val EB_CODE_MAIN_SELECT_TYPE = "EB_CODE_MAIN_SELECT_TYPE"// 已登录 跳转主页 选择类型
        const val EB_CODE_MAIN_FINISH = "EB_CODE_MAIN_FINISH"//用户操作完成 退出登录
        const val EB_CODE_CAPTURE_PICTURE_SUCCESS = "capturePictureSuccess"//抓拍成功

        /**
         * 回收箱控制指令
         */
        const val SERIAL_PORT_OPEN= "01"//打开
        const val SERIAL_PORT_CLOSE= "00"//关闭

        const val SERIAL_PORT_TYPE_INFO = "01"//获取垃圾回收箱信息
        const val SERIAL_PORT_TYPE_DOOR = "02"//门指令
        const val SERIAL_PORT_TYPE_LIGHT = "03"//灯指令
        const val SERIAL_PORT_TYPE_FAN = "04"//风扇指令
        const val SERIAL_PORT_TYPE_WEIGH = "05"//称重
        const val SERIAL_PORT_TYPE_RESTART_CONTROL_PANEL = "06"//重启控制面板
        const val SERIAL_PORT_TYPE_STATUS = "07"//查询状态
        const val SERIAL_PORT_TYPE_DEVICE_VERSION = "08"//获取Device版本
        const val SERIAL_PORT_TYPE_INIT_WEIGH = "09"//初始化毛重
        const val SERIAL_PORT_TYPE_CALW_WEIGH = "0A"//校准称重系数
        const val SERIAL_PORT_TYPE_SAVE_ID = "0C"//保存机器Id
        const val SERIAL_PORT_TYPE_GET_ID = "0D"//获取机器Id
        const val SERIAL_PORT_TYPE_SET_DATE = "0E"//设置设备时间
        const val SERIAL_PORT_TYPE_QUERY_ERROR = "0F"//查询机器错误
        const val SERIAL_PORT_TYPE_CLEAR_ERROR = "10"//清除机器错误
        const val SERIAL_PORT_TYPE_CHANGE_LOG = "11"//切换Log开关
        const val SERIAL_PORT_TYPE_SETTING = "14"//配置机器参数
        const val SERIAL_PORT_TYPE_LED = "15"//控制面板上LED灯
        const val SERIAL_PORT_TYPE_INFRARED = "16"//控制板红外测距

        const val SERIAL_PORT_DATA_DOOR_DELIVERY = "01"//投递门
        const val SERIAL_PORT_DATA_DOOR_GARBAGE = "02"//垃圾门
        const val SERIAL_PORT_DATA_DOOR_DELIVERY_PANEL = "03"//投递门面板
        const val SERIAL_PORT_DATA_LIGHT_INSIDE = "01"//垃圾箱内拍照指示灯
        const val SERIAL_PORT_DATA_FAN_INSIDE = "01"//垃圾箱内拍照指示灯
    }
}