package com.yaduo.common.applogic

import android.app.Application
import com.yaduo.common.ApplicationVersion
import okhttp3.Interceptor

/**
 * @author YaDuo
 * @since 2025-05-14 15:52:06
 */
object AppLogicUtil {

    /** 全局上下文实例 **/
    private lateinit var sApp: Application

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
     * ### 获取Application版本信息
     *  - 应用名
     *  - 版本名
     *  - 打包时间
     *  - 使用分支
     */
    fun getApplicationVersionInfo() =
        "${ApplicationVersion.APP_NAME}_${ApplicationVersion.VERSION_NAME}_${ApplicationVersion.PACKTIME}_${ApplicationVersion.BRANCH}"
}