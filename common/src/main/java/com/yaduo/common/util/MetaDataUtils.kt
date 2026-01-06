package com.yaduo.common.util

import android.content.Context
import android.content.pm.PackageManager
import com.yaduo.common.log.LogUtil

/**
 * @author YaDuo
 * @since 2026-01-02 03:50:50
 */
object MetaDataUtils {

    private const val TAG = "MetaDataUtils"

    /**
     * 获取当前应用的元数据（MetaData）。
     *
     * @param context 上下文对象，用于获取包管理器。
     * @return 返回包含应用元数据的 Bundle 对象。如果未设置元数据，则返回 null。
     */
    private fun getMetaData(context: Context) = context.packageManager.getApplicationInfo(
        context.packageName,
        PackageManager.GET_META_DATA
    ).metaData

    /**
     * 检测Manifest中是否存在目标meta-data配置
     * @param context 应用上下文
     * @param keys 要检测的meta-data的name属性值（可变参数，支持传入多个）
     * @return true=存在，false=不存在
     */
    fun checkManifestHasTargetMetaData(context: Context, vararg keys: String): Boolean {
        if (keys.isEmpty()) {
            LogUtil.w(TAG, "检测的meta-data key列表为空")
            return false
        }

        return try {
            keys.forEach { key ->
                val metaValue = getMetaDataString(context, key, "-1")
                if (metaValue == "-1") {
                    LogUtil.d(TAG, "Manifest中未检测到有效配置：key = $key，value = $metaValue")
                    return false
                }
            }
            // 所有key都校验通过
            LogUtil.i(
                TAG,
                "Manifest中所有传入的meta-data配置都存在：keys = ${keys.joinToString(",")}"
            )
            true
        } catch (e: Exception) {
            LogUtil.e(TAG, "检测Manifest配置失败", e)
            false
        }
    }

    /**
     * 读取Manifest中application节点下的meta-data整数值
     * @param key meta-data的name
     * @param defaultValue 默认值
     */
    fun getMetaDataInt(context: Context, key: String, defaultValue: Int = -1): Int {
        return runCatching {
            getMetaData(context)?.getInt(key, defaultValue) ?: defaultValue
        }.onFailure {
            LogUtil.e(TAG, "读取Manifest meta-data失败：key = $key", it)
        }.getOrDefault(defaultValue)
    }

    /**
     * 从AndroidManifest.xml读取meta-data的值
     * @param context 应用上下文
     * @param key meta-data的name属性值
     * @param defaultValue 默认值
     * @return 对应的值，若不存在/读取失败返回默认值
     */
    fun getMetaDataString(
        context: Context,
        key: String,
        defaultValue: String = "-1"
    ): String {
        return runCatching {
            getMetaData(context)?.getString(key, defaultValue) ?: defaultValue
        }.onFailure {
            LogUtil.e(TAG, "读取Manifest meta-data失败：key = $key", it)
        }.getOrDefault(defaultValue)
    }
}