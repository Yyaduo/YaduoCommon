package com.yaduo.common.applogic

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import okhttp3.Interceptor

/**
 * @author YaDuo
 * @since 2025-05-14 15:52:06
 */
object AppLogicUtil {

    /** 全局上下文实例 **/
    private lateinit var sApp: Application

    var appIdForBuglyReport: String = ""

    /** 保存的[ICommonModule]列表 **/
    private val commonModuleList = mutableListOf<ICommonModule>()

    /**
     * 初始化方法，传入Application的实例
     * @param app 在Application创建时传入上下文
     */
    fun initialize(app: Application) {
        sApp = app
    }

    /**
     * 获取Application实例
     */
    fun getApp() = sApp

    /**
     * 注册单个模块方法
     */
    fun registerCommonModule(commonModule: ICommonModule): AppLogicUtil {
        commonModuleList.add(commonModule)
        return this
    }

    /**
     * 初始化全部注册模块
     */
    fun initializeCommonModule() = commonModuleList.forEach { it.initialize() }

    /**
     * 注册所有模块并初始化
     */
    fun initializeAllCommonModule() {
        registerCommonModule(AutoSizeConfig)
        registerCommonModule(BuglyReport)
        registerCommonModule(Chucker)
        initializeCommonModule()
    }

    /**
     * 获取Chucker拦截器
     */
    fun getChuckerInterceptor(): Interceptor {
        return Chucker.interceptor
    }

    /**
     * 获取应用版本代码（如 101，整数形式）
     */
    fun getVersionCode(): Long {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                sApp.packageManager.getPackageInfo(
                    sApp.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                sApp.packageManager.getPackageInfo(sApp.packageName, 0)
            }
            // Android 10+ 版本代码为 Long 类型，低版本为 Int
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    /**
     * ### 获取Application版本信息
     *  - 应用名
     *  - 版本名
     *  - 打包时间
     *  - 使用分支
     */
//    fun getApplicationVersionInfo() =
//        "${sApp.applicationInfo.name}_${BuildConfig.VERSION_NAME}_${BuildConfig.BUILD_TYPE}_${ApplicationVersion.BRANCH}"
}