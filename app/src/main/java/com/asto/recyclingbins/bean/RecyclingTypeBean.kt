package com.asto.recyclingbins.bean

import java.io.Serializable

/**
 * 作者 ：nxk
 * 日期 ：2019/12/5
 * 备注 ：主页回收类型Bean
 */
//TODO 修改成服务器对应Bean
/**
 * @param name : 回收类型名称
 * @param img : 回收类型图标
 * @param integral : 对应积分
 * @param weight : 已回收重量
 */
data class RecyclingTypeBean(val name : String, val  img : String, val integral : Int = 0, var weight : Double = 0.0) : Serializable