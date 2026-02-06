package com.yaduo.commontest

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yaduo.common.log.LogUtil
import com.yaduo.commontest.screen.CommonModuleScreen
import com.yaduo.commontest.screen.DeviceScreen
import com.yaduo.commontest.screen.ExtraScreen
import com.yaduo.commontest.screen.LogScreen
import com.yaduo.commontest.screen.TtsScreen

object Routes {

    /** 公共模块类测试节点 */
    const val ROUTE_COMMON_MODULE = "routes_common_module"

    /** 设备工具类测试节点 */
    const val ROUTE_DEVICE = "routes_devices"

    /** 拓展工具类测试节点 */
    const val ROUTE_EXTRA = "routes_extra"

    /** 日志工具类测试节点 */
    const val ROUTE_LOG = "routes_log"

    /** TTS工具类测试节点 */
    const val ROUTE_TTS = "routes_tts"
}

@Composable
fun CommonTestNavGraph(targetRoute: String) {
    LogUtil.i(content = "目标路由： $targetRoute")
    val navController = rememberNavController()

    // 监听外部路由变化并执行导航逻辑
    LaunchedEffect(targetRoute) {
        // 只有当 NavController 的 Graph 已经建立，且目标路由不同时才进行操作
        if (navController.graph.findNode(targetRoute) != null &&
            navController.currentBackStackEntry?.destination?.route != targetRoute
        ) {
            navController.navigate(targetRoute) {
                // 弹出至起始页，避免返回栈无限堆积
                popUpTo(Routes.ROUTE_COMMON_MODULE) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.ROUTE_COMMON_MODULE,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Routes.ROUTE_COMMON_MODULE) { CommonModuleScreen() }
            composable(Routes.ROUTE_DEVICE) { DeviceScreen() }
            composable(Routes.ROUTE_EXTRA) { ExtraScreen() }
            composable(Routes.ROUTE_LOG) { LogScreen() }
            composable(Routes.ROUTE_TTS) { TtsScreen() }
        }
    }
}
