package com.yaduo.common.commonModule

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.yaduo.common.applogic.AppLogicUtil
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 *
 * ### 网络监控库
 *
 *  - 零配置使用：无需代理或证书安装
 *  - 设备端实时查看：直接在设备上查看请求 / 响应详情
 *  - 开发生产隔离：通过无操作依赖自动隔离生产环境
 *  - 轻量高效：仅 100KB 左右的体积开销
 *  - Chucker 工作流程：拦截请求 → 收集数据 → 展示结果
 *
 * @see <a href="https://github.com/ChuckerTeam/chucker">Chucker GitHub</a>
 * @author YaDuo
 * @since 2025-07-18 17:50:53
 */
object Chucker : ICommonModule {

    override var isInitialized = false

    override var isCanInitialized = true

    /** 敏感头列表 **/
    private val SENSITIVE_HEADERS = listOf("Authorization", "Cookie")

    /**
     * 全局 Chucker 拦截器实例
     *
     * 此实例使用应用上下文创建，适用于整个应用生命周期
     */
    private lateinit var _chuckerInterceptor: Interceptor

    /**
     * 获取 Chucker 拦截器实例
     */
    val interceptor: Interceptor
        get() = if (isInitialized) _chuckerInterceptor else DefaultInterceptor()

    /** 全局 Chucker 收集器 **/
    private val chuckerCollector by lazy {
        ChuckerCollector(
            context = AppLogicUtil.getApp(),
            showNotification = true, // 显示通知栏入口
            retentionPeriod = RetentionManager.Period.ONE_HOUR // 数据保留时间
        )
    }

    override fun initialize(context: Context) {
        if (isInitialized && !BuglyReport.isCanInitialized) return

        _chuckerInterceptor = createChuckerInterceptor(context)
        isInitialized = true
    }

    override fun checkCanBeInitialized(context: Context) = isCanInitialized

    /**
     * 创建并配置 Chucker 网络监控拦截器
     *
     * 此拦截器用于捕获和记录应用的网络请求和响应，可在设备上直接查看网络活动详情。
     *
     * ### 配置说明：
     * - **数据大小限制**：限制捕获的请求/响应内容大小，避免内存溢出
     * - **敏感信息脱敏**：自动隐藏敏感头信息，保护用户隐私
     * - **强制读取响应体**：确保完整捕获响应内容
     *
     * ### 使用示例：
     * ```kotlin
     * val okHttpClient = OkHttpClient.Builder()
     *     .addInterceptor(Chucker.createChuckerInterceptor(context))
     *     .build()
     * ```
     *
     * @param context 应用上下文，用于访问资源
     * @return 配置好的 Chucker 拦截器实例
     */
    private fun createChuckerInterceptor(context: Context): Interceptor =
        ChuckerInterceptor.Builder(context)
            .collector(chuckerCollector)
            .maxContentLength(512_000L) // 限制捕获数据大小
            .redactHeaders(*SENSITIVE_HEADERS.toTypedArray()) // 敏感头脱敏
            .alwaysReadResponseBody(true) // 强制读取响应体
            .build()

    /**
     * 空操作拦截器
     *
     * 当 Chucker 未初始化时
     * 该拦截器仅简单传递请求而不执行任何操作
     */
    private class DefaultInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            return chain.proceed(chain.request())
        }
    }
}