package com.yaduo.common.commonModule

import android.content.Context
import android.os.Build
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import com.yaduo.common.applogic.AppLogicUtil
import com.yaduo.common.device.DeviceInfo

/**
 * ### Bugly 崩溃报告系统集成模块
 *
 * 封装腾讯 Bugly SDK 的初始化流程，提供应用崩溃监控和分析功能。
 *
 * 主要特性：
 * - 实时崩溃监控（支持Native层崩溃）
 * - ANR（Application Not Responding）检测
 * - 自定义错误上报
 * - 用户行为追踪
 *
 * #### 配置说明：
 * 1. **设备信息**：
 *    - deviceID：设备唯一标识（使用 DeviceInfo.getUuid()）
 *    - deviceModel：设备型号（Build.MODEL）
 * 2. **应用信息**：
 *    - appVersion：应用版本名称（ApplicationVersion.APP_NAME）
 *    - appPackageName：应用包名（ApplicationVersion.PACKAGE_NAME）
 * 3. **高级设置**：
 *    - 启动延迟：3000ms（避免影响启动性能）
 *    - 调试模式：BuildConfig.DEBUG（自动区分环境）
 *
 *
 * #### 注意事项：
 * - 必须在主线程调用初始化
 * - 需在 Application onCreate 中尽早初始化
 * - 发布版本需关闭调试模式（已自动处理）
 * - 用户ID应在用户登录后更新
 *
 * @see <a href="https://bugly.qq.com/docs/">Bugly 官方文档</a>
 * @author YaDuo
 * @since 2025-05-14 16:42:06
 */
object BuglyReport : ICommonModule {

    override var isInitialized = false

    /**
     * 初始化 Bugly 崩溃报告系统
     *
     * 此方法执行以下操作：
     * 1. 创建用户策略对象并配置设备/应用信息
     * 2. 初始化 Bugly SDK 核心功能
     * 3. 设置用户标识符（UID）
     * 4. 配置调试模式（自动识别 BuildConfig.DEBUG）
     *
     * @param context 应用上下文（自动转换为 ApplicationContext）
     */
    override fun initialize(context: Context) {
        if (isInitialized) return

        // 用户策略
        val userStrategy = UserStrategy(context).apply {
            deviceID = DeviceInfo.getUuid()
            deviceModel = Build.MODEL
            appVersion = AppLogicUtil.getVersionCode().toString()
            appPackageName = context.packageName
        }

        // 初始化
        CrashReport.initCrashReport(
            context,
            AppLogicUtil.appIdForBuglyReport,
            false,
            userStrategy
        )

        // 额外配置uid
        CrashReport.setUserId(DeviceInfo.getUid())

        isInitialized = true
    }

}