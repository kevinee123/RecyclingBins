package com.asto.recyclingbins.listener

interface OnSerialPortListener {
    fun onSend(text : String)
    fun onResult(text : String)
}