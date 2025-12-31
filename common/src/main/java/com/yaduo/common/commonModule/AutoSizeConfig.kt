package com.yaduo.common.commonModule

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import com.yaduo.common.log.LogUtil
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.onAdaptListener


/**
 * ### 屏幕适配配置模块
 *
 * 封装 AndroidAutoSize 库的配置逻辑，提供全局屏幕适配解决方案。
 *
 * 核心功能：
 * 1. 自动根据屏幕尺寸调整 UI 布局
 * 2. 提供适配前后的回调监听
 * 3. 支持自定义适配策略
 * 4. 自动检测Manifest中是否有design_width/height配置，有则自动启用适配，无则禁用
 * 5. 主动调用initialize()优先级高于自动检测
 * 6. 支持手动禁用适配（disableAutoSizeAdapt）
 *
 * #### 集成说明
 * ```xml
 * <!-- 在 AndroidManifest.xml 中添加 -->
 * <application>
 *     <meta-data
 *         android:name="design_width_in_dp"
 *         android:value="360"/> <!-- 设计稿宽度 -->
 *     <meta-data
 *         android:name="design_height_in_dp"
 *         android:value="640"/> <!-- 设计稿高度 -->
 * </application>
 * ```
 *
 * #### 注意事项：
 * - 需在 Application onCreate 中初始化
 * - 适配单位推荐使用 dp 或 pt
 * - 适配策略可根据业务需求自定义
 * - *** 当且仅当主动调用 initialize() 方法时才启用适配，否则完全禁用 ***
 *
 * @see <a href="https://github.com/JessYanCoding/AndroidAutoSize">AndroidAutoSize GitHub</a>
 * @author YaDuo
 * @since 2025-07-19 11:24:35
 */
object AutoSizeConfig : ICommonModule {

    private const val TAG = "AutoSizeConfig"

    /** Manifest配置设计稿宽度常量 **/
    private const val META_KEY_WIDTH = "design_width_in_dp"

    /** Manifest配置设计稿高度常量 **/
    private const val META_KEY_HEIGHT = "design_height_in_dp"

    override var isInitialized = false

    /**
     * 初始化 AndroidAutoSize 崩溃报告系统
     * 在这里设置了监听适配前和适配后，并打上日志
     * @param context 应用上下文（自动转换为 ApplicationContext）
     */
    override fun initialize(context: Context) {
        if (isInitialized) return

        LogUtil.i(TAG, "AutoSizeConfig 主动初始化：启用屏幕适配")
        enableAutoSizeAdapt(context)
        isInitialized = true
    }

    /**
     * 自动检测Manifest中是否有适配配置
     *
     * 有则启用，无则禁用
     */
    fun autoCheckAndAdapt(context: Context) {
        if (isInitialized) {
            LogUtil.i(TAG, "已主动初始化适配，跳过自动检测")
            return
        }

        val hasAdaptConfig = checkManifestHasAdaptConfig(context)
        if (hasAdaptConfig) {
            LogUtil.i(TAG, "检测到Manifest中有适配配置，自动启用屏幕适配")
            enableAutoSizeAdapt(context)
        } else {
            LogUtil.i(TAG, "未检测到Manifest适配配置，自动禁用屏幕适配")
            disableAutoSizeAdapt()
        }
    }

    /**
     * 启用 AndroidAutoSize 适配逻辑
     */
    private fun enableAutoSizeAdapt(context: Context) {
        AutoSizeConfig.getInstance().apply {
            isBaseOnWidth = true
            setExcludeFontScale(false)
            // 读取Manifest中的设计稿尺寸（可选，让适配更精准）
            val designWidth = getMetaDataInt(context, META_KEY_WIDTH, 360)
            val designHeight = getMetaDataInt(context, META_KEY_HEIGHT, 640)
            LogUtil.i(TAG, "适配设计稿尺寸：宽 = $designWidth dp，高 = $designHeight dp")

            setOnAdaptListener(object : onAdaptListener {
                override fun onAdaptBefore(target: Any?, activity: Activity?) {
                    LogUtil.d(content = "${target?.javaClass?.name} onAdaptBefore")
                }

                override fun onAdaptAfter(target: Any?, activity: Activity?) {
                    LogUtil.d(content = "${target?.javaClass?.name} onAdaptAfter")
                }
            })
        }
        isInitialized = true
    }


    /**
     * 禁用 AndroidAutoSize 所有适配逻辑
     * 抵消 ContentProvider 自动初始化带来的隐式适配
     */
    fun disableAutoSizeAdapt() {
        LogUtil.i(TAG, "禁用屏幕适配")
        AutoSizeConfig.getInstance().apply {
            isBaseOnWidth = false
            setExcludeFontScale(true)
        }
        isInitialized = false
    }

    /**
     * 检测Manifest中是否存在适配所需的meta-data配置
     * @return true=存在（design_width + design_height都有），false=不存在
     */
    private fun checkManifestHasAdaptConfig(context: Context): Boolean {
        return try {
            val hasWidth = getMetaDataInt(context, META_KEY_WIDTH, -1) != -1
            val hasHeight = getMetaDataInt(context, META_KEY_HEIGHT, -1) != -1
            hasWidth && hasHeight
        } catch (e: Exception) {
            LogUtil.e(TAG, "检测Manifest适配配置失败", e)
            false
        }
    }

    /**
     * 读取Manifest中application节点下的meta-data整数值
     * @param key meta-data的name
     * @param defaultValue 默认值
     */
    private fun getMetaDataInt(context: Context, key: String, defaultValue: Int): Int {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            appInfo.metaData?.getInt(key, defaultValue) ?: defaultValue
        } catch (e: PackageManager.NameNotFoundException) {
            LogUtil.e(TAG, "读取Manifest meta-data失败：包名不存在", e)
            defaultValue
        } catch (e: Exception) {
            LogUtil.e(TAG, "读取Manifest meta-data失败：key = $key", e)
            defaultValue
        }
    }

}