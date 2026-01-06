package com.yaduo.common.commonModule

import android.content.Context
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.yaduo.common.log.LogUtil

/**
 * ### MMKV 高效键值对存储工具类
 *
 * 封装腾讯 MMKV 框架的核心操作，提供简洁的键值对存储能力
 * MMKV 是基于 mmap 内存映射的 key-value 组件，具有以下优势：
 * - 读写性能远超 SharedPreferences
 * - 支持多进程访问
 * - 自动加密数据（可选）
 * - 更小的内存占用和更快的启动速度
 *
 * #### 支持的数据类型：
 * - 基本类型：Int、Long、Float、Double、Boolean
 * - 字符串类型：String
 * - 泛型对象：通过 Gson 转换为 JsonObject 存储任意对象
 *
 * #### 核心功能：
 * 1. 数据存储：针对不同类型提供专用存储方法
 * 2. 数据读取：支持设置默认值，避免空指针异常
 * 3. 数据管理：包含键、清除指定键、清空所有数据等操作
 *
 * #### 使用说明：
 * 1. 必须在 Application 初始化时调用 MMKV.initialize(context)
 * 2. 所有操作通过单例 MMKV 直接调用，无需实例化
 * 3. 键名建议使用常量管理，避免硬编码（推荐在 Constants 类中定义）
 *
 * #### 注意事项：
 * - 不建议存储大量数据（超过100KB），大数据建议使用数据库
 * - 多进程场景需在初始化时指定 MULTI_PROCESS_MODE 模式
 * - 数据默认存储在应用私有目录，卸载应用会自动清除
 *
 * @see <a href="https://github.com/Tencent/MMKV">MMKV 官方文档</a>
 * @author YaDuo
 * @since 2025-10-13 12:04:36
 */
object MMKV : ICommonModule {

    override var isInitialized = false

    override var isCanInitialized = true

    override fun initialize(context: Context) {
        MMKV.initialize(context)
        isInitialized = true
    }

    override fun checkCanBeInitialized(context: Context) = Chucker.isCanInitialized

    /** 获取默认的 MMKV 实例 **/
    private val mmkv: MMKV by lazy { MMKV.defaultMMKV() }

    /** Gson 实例，用于对象与 JSON 转换 **/
    private val gson: Gson by lazy { Gson() }

    fun putInt(key: String, value: Int) = mmkv.encode(key, value)

    fun getInt(key: String, defaultValue: Int = 0) = mmkv.decodeInt(key, defaultValue)

    fun putLong(key: String, value: Long) = mmkv.encode(key, value)


    fun getLong(key: String, defaultValue: Long = 0L) = mmkv.decodeLong(key, defaultValue)

    fun putFloat(key: String, value: Float) = mmkv.encode(key, value)


    fun getFloat(key: String, defaultValue: Float = 0f) = mmkv.decodeFloat(key, defaultValue)

    fun putDouble(key: String, value: Double) = mmkv.encode(key, value)


    fun getDouble(key: String, defaultValue: Double = 0.0) = mmkv.decodeDouble(key, defaultValue)

    fun putBoolean(key: String, value: Boolean) = mmkv.encode(key, value)


    fun getBoolean(key: String, defaultValue: Boolean = false) = mmkv.decodeBool(key, defaultValue)

    fun putString(key: String, value: String) = mmkv.encode(key, value)


    fun getString(key: String, defaultValue: String = "") =
        mmkv.decodeString(key, defaultValue) ?: defaultValue

    /**
     * 将泛型对象转换为 JsonObject 并存储
     * @param key 存储键名
     * @param data 要存储的泛型对象
     * @param T 泛型类型，支持任意实现默认构造函数的类
     */
    fun <T> putObjectAsJsonObject(key: String, data: T) {
        val jsonObject = gson.toJsonTree(data).asJsonObject
        mmkv.encode(key, jsonObject.toString())
    }

    /**
     * 读取 JsonObject 并转换为指定类型的泛型对象
     * @param key 存储键名
     * @param clazz 目标对象的 Class
     * @param defaultValue 键不存在或转换失败时的默认值
     * @param T 泛型类型
     * @return 转换后的泛型对象或默认值
     */
    fun <T> getObjectFromJsonObject(key: String, clazz: Class<T>, defaultValue: T? = null): T? {
        return runCatching {
            val jsonString = mmkv.decodeString(key)
            if (jsonString.isNullOrEmpty()) {
                defaultValue
            } else {
                gson.fromJson(jsonString, clazz)
            }
        }.getOrElse {
            LogUtil.e(content = "getObjectFromJsonObject failed", throwable = it)
            defaultValue
        }
    }

    fun containsKey(key: String) = mmkv.containsKey(key)

    fun removeKey(key: String) = mmkv.remove(key)

    fun clearAll() = mmkv.clearAll()
}