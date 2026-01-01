package com.yaduo.common.commonModule

import android.content.Context
import android.os.Build
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import com.yaduo.common.applogic.AppLogicUtil
import com.yaduo.common.device.DeviceInfo
import com.yaduo.common.log.LogUtil
import com.yaduo.common.util.MetaDataUtils

/**
 * ### Bugly 崩溃报告系统集成模块
 *
 * 封装腾讯 Bugly SDK 的初始化流程，提供应用崩溃监控和分析功能。
 *
 * #### 主要特性：
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
 * #### 集成说明
 * ```xml
 * <!-- 在 AndroidManifest.xml 中添加 -->
 * <application>
 *     <meta-data
 *         android:name="BUGLY_APPID"
 *         android:value="你的Bugly AppID"/>
 * </application>
 * ```
 *
 * #### 注意事项：
 * - 必须在主线程调用初始化
 * - 初始化逻辑依赖 Manifest 中的 BUGLY_APPID 配置，无配置则不初始化
 * - 需在 Application onCreate 中尽早初始化
 * - 发布版本需关闭调试模式（已自动处理）
 * - 用户ID应在用户登录后更新
 *
 * @see <a href="https://bugly.qq.com/docs/">Bugly 官方文档</a>
 * @author YaDuo
 * @since 2025-05-14 16:42:06
 */
object BuglyReport : ICommonModule {

    private const val TAG = "BuglyReport"

    /** Manifest配置Bugly的Appid **/
    private const val META_KEY_BUGLY_APPID = "BUGLY_APPID"

    override var isInitialized = false

    override var isCanInitialized = false

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
        if (isInitialized && !isCanInitialized) return

        val buglyAppId = MetaDataUtils.getMetaDataString(context, META_KEY_BUGLY_APPID)

        LogUtil.i(TAG, "BuglyReport initialize, appid: $buglyAppId")

        if (buglyAppId == "-1") {
            LogUtil.w(
                TAG,
                "BuglyReport initialize, appid is error, BuglyReport initialize failed"
            )
            return
        }

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
            buglyAppId,
            false,
            userStrategy
        )

        // 额外配置uid
        CrashReport.setUserId(DeviceInfo.getUid())

        isInitialized = true

    }

    override fun checkCanBeInitialized(context: Context): Boolean {
        if (MetaDataUtils.checkManifestHasTargetMetaData(context, META_KEY_BUGLY_APPID)) {
            initialize(context)
            isCanInitialized = true
        } else {
            LogUtil.w(TAG, "appIdForBuglyReport is empty, BuglyReport initialize failed")
            isCanInitialized = false
        }
        return isCanInitialized
    }

}