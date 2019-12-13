package com.asto.recyclingbins.bean

/**
 * 作者 ：nxk
 * 日期 ：2019/12/12
 * 备注 ：EventBus数据收发
 */
data class EventMessage<T>(val code: String, val data: T? = null)