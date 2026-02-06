package com.yaduo.commontest.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yaduo.commontest.CommonTestNavGraph
import com.yaduo.commontest.Routes

val tabList = listOf(
    Routes.ROUTE_COMMON_MODULE to "公共",
    Routes.ROUTE_DEVICE to "设备",
    Routes.ROUTE_LOG to "日志",
    Routes.ROUTE_TTS to "TTS",
    Routes.ROUTE_EXTRA to "拓展",
)

/**
 * ### 主页面
 *
 * 管理测试页面的 Tab 切换与导航图显示。 解决了导航崩溃、状态同步及响应式高度适配问题。
 *
 * @author YaDuo
 * @since 2026-02-05 18:55:12
 */
@Composable
fun MainScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            // 当前选中的页面路由
            var currentPage by remember { mutableStateOf(Routes.ROUTE_COMMON_MODULE) }

            TabRowLayout(
                tabList = tabList,
                selectedRoute = currentPage,
                onTabClick = { currentPage = it }
            )

            // 导航图显示区域，分配权重占据剩余空间
            Box(modifier = Modifier.weight(1f)) { CommonTestNavGraph(currentPage) }
        }
    }
}

/**
 * 响应式 Tab 布局
 *
 * @param tabList 路由与显示名称的配对列表
 * @param selectedRoute 当前选中的路由
 * @param onTabClick 点击回调
 */
@Composable
fun TabRowLayout(
    tabList: List<Pair<String, String>>,
    selectedRoute: String,
    onTabClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabList.forEach { (route, name) ->
            TabItem(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .clickable { onTabClick(route) },
                isSelected = selectedRoute == route,
                name = name
            )
        }
    }
}

@Composable
fun TabItem(
    modifier: Modifier,
    isSelected: Boolean,
    name: String
) {
    Surface(
        modifier = modifier,
        color = if (isSelected)
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        else Color.Transparent,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
                else Color.Gray
            )
            // 增加下划线指示器
            if (isSelected) {
                HorizontalDivider(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    thickness = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
