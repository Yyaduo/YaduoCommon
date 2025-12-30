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
     * 初始化方法
     * @param context Context对象, 默认下为全局上下文
     */
    fun initialize(context: Context = AppLogicUtil.getApp())
}