package com.asto.recyclingbins.listener

interface OnSerialPortDataListener {
    /**
     * 成功返回数据
     * @param commandType : 命令类型
     * @param data : 数据
     */
    fun onSuccess(commandType: String, data: String)
}