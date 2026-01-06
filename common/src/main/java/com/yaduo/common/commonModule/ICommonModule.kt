package com.yaduo.common.commonModule

import android.content.Context
import com.yaduo.common.applogic.AppLogicUtil

/**
 * ### 公共模块接口
 * - 提供接口规范
 * - 是否初始化过
 * - 初始化方法
 *
 * @author YaDuo
 * @since 2025-07-19 11:30:26
 */
interface ICommonModule {

    /**
     * 是否初始化过了
     */
    var isInitialized: Boolean

    /**
     * 是否可以初始化
     */
    var isCanInitialized: Boolean

    /**
     * 初始化方法
     * @param context Context对象, 默认下为全局上下文
     */
    fun initialize(context: Context = AppLogicUtil.getApp())

    /**
     * 检查是否可以调用初始化方法
     *
     * 在某些类中，如果可调用的话则直接初始化
     * @param context Context对象, 默认下为全局上下文
     */
    fun checkCanBeInitialized(context: Context = AppLogicUtil.getApp()): Boolean
}