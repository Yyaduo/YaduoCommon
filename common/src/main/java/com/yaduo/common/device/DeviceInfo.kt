package com.yaduo.common.device

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import com.yaduo.common.Utils
import com.yaduo.common.applogic.AppLogicUtil
import com.yaduo.common.log.LogUtil


/**
 * ### 设备工具类
 *  - 获取所需设备信息
 *
 * @author YaDuo
 * @since 2025-05-14 16:11:38
 */
object DeviceInfo {

    /**
     * 打印设备信息
     */
    fun printDeviceInfo() = LogUtil.i(
        content = "serial = ${getUuid()}, " +
                "uid = ${getUid()}," +
                "network connect = ${isNetworkConnected()}"
    )

    /**
     * 获取设备序列号
     */
    fun getUuid(): String = Build.SERIAL

    /**
     * 获取UserId
     */
    fun getUid(): String = Utils.genUid(getUuid())

    /**
     * 网络状态是否有链接
     */
    fun isNetworkConnected(): Boolean {
        val connectivityManager = AppLogicUtil.getApp()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}