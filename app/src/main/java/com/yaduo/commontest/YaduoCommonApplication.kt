package com.yaduo.commontest

import android.app.Application
import com.yaduo.common.applogic.AppLogicUtil

/**
 * @author YaDuo
 * @since 2026-02-05 18:28:13
 */
class YaduoCommonApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppLogicUtil.initialize(this)
        AppLogicUtil.initializeAllCommonModule()
    }
}