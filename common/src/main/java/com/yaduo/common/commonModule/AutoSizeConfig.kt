package com.yaduo.common.commonModule

import android.app.Activity
import android.content.Context
import com.yaduo.common.log.LogUtil
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.onAdaptListener
import java.util.Locale


/**
 * ### 屏幕适配配置模块
 *
 * 封装 AndroidAutoSize 库的配置逻辑，提供全局屏幕适配解决方案。
 *
 * 核心功能：
 * 1. 自动根据屏幕尺寸调整 UI 布局
 * 2. 提供适配前后的回调监听
 * 3. 支持自定义适配策略
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
 *
 * @see <a href="https://github.com/JessYanCoding/AndroidAutoSize">AndroidAutoSize GitHub</a>
 * @author YaDuo
 * @since 2025-07-19 11:24:35
 */
object AutoSizeConfig : ICommonModule {

    override var isInitialized = false


    /**
     * 初始化 AndroidAutoSize 崩溃报告系统
     * 在这里设置了监听适配前和适配后，并打上日志
     * @param context 应用上下文（自动转换为 ApplicationContext）
     */
    override fun initialize(context: Context) {
        if (isInitialized) return

        AutoSizeConfig.getInstance().setOnAdaptListener(object : onAdaptListener {
            override fun onAdaptBefore(target: Any?, activity: Activity?) {
                LogUtil.d(
                    content = String.format(
                        Locale.ENGLISH,
                        "%s onAdaptBefore",
                        target?.javaClass?.name
                    )
                )
            }

            override fun onAdaptAfter(target: Any?, activity: Activity?) {
                LogUtil.d(
                    content = String.format(
                        Locale.ENGLISH,
                        "%s onAdaptAfter",
                        target?.javaClass?.name
                    )
                )
            }

        })

        isInitialized = true
    }

}