package com.asto.recyclingbins.base

import com.asto.recyclingbins.core.Common


data class BaseBean<T>(val code : Int, val message: String,val data: T) {

    //成功
    val isSuccess: Boolean
        get() = code == Common.HTTP_SUCCESS

    //登录失效
    val isLoginOut: Boolean
        get() = code == Common.HTTP_LOGIN_OUT


}
