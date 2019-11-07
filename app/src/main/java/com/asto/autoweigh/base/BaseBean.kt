package com.asto.autoweigh.base

import com.asto.autoweigh.core.Common


data class BaseBean<T>(val code : Int, val message: String,val data: T) {

    //成功
    val isSuccess: Boolean
        get() = code == Common.HTTP_SUCCESS

    //登录失效
    val isLoginOut: Boolean
        get() = code == Common.HTTP_LOGIN_OUT


}
