package com.yaduo.common.util

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX
import com.yaduo.common.log.LogUtil
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * ### PermissionX 极简封装工具类
 *
 * #### 核心功能：
 * - 单/多权限申请
 * - 权限检查
 * - 版本自动适配
 * - 防重复申请
 *
 * #### 💡快速使用示例
 * 1. 检查权限是否授予
 * ``` kotlin
 * // 检查单个权限
 * val isNotificationGranted = PermissionUtils.isPermissionGranted(
 *     context = this,
 *     permission = Manifest.permission.POST_NOTIFICATIONS
 * )
 *
 * // 检查多个权限
 * val permissions = listOf(
 *     Manifest.permission.POST_NOTIFICATIONS,
 *     Manifest.permission.READ_EXTERNAL_STORAGE
 * )
 * val isAllGranted = PermissionUtils.areAllPermissionsGranted(this, permissions)
 * ```
 *
 * 2. 回调方式申请权限
 * ``` kotlin
 * // 申请单个权限
 * PermissionUtils.requestPermission(
 *     activity = this, // 需继承 AppCompatActivity
 *     permission = Manifest.permission.POST_NOTIFICATIONS,
 *     reason = "需要通知权限查看网络请求日志"
 * ) { result ->
 *     if (result.allGranted) {
 *         // 权限授予，执行业务逻辑
 *         LogUtil.i("Permission", "通知权限申请成功")
 *     } else {
 *         // 权限拒绝，提示用户
 *         LogUtil.w("Permission", "通知权限申请失败：${result.deniedList}")
 *     }
 * }
 *
 * // 申请多个权限
 * PermissionUtils.requestPermissions(
 *     activity = this,
 *     permissions = listOf(
 *         Manifest.permission.POST_NOTIFICATIONS,
 *         Manifest.permission.READ_EXTERNAL_STORAGE
 *     ),
 *     reason = "需要通知和存储权限以提供完整功能"
 * ) { result ->
 *     if (result.allGranted) {
 *         // 所有权限授予
 *     } else {
 *         // 部分/全部权限拒绝
 *     }
 * }
 * ```
 *
 * 3. 协程方式申请权限（推荐，适配 Kotlin/Compose）
 * ``` kotlin
 * // Activity 中使用
 * lifecycleScope.launch {
 *     val result = PermissionUtils.requestPermissionByCoroutine(
 *         activity = this@MainActivity,
 *         permission = Manifest.permission.POST_NOTIFICATIONS,
 *         reason = "需要通知权限查看网络请求日志"
 *     )
 *
 *     if (result.allGranted) {
 *         // 权限授予
 *     } else {
 *         // 权限拒绝
 *     }
 * }
 *
 * // Compose 中使用
 * val scope = rememberCoroutineScope()
 * Button(onClick = {
 *     scope.launch {
 *         val result = PermissionUtils.requestPermissionsByCoroutine(
 *             activity = LocalContext.current as AppCompatActivity,
 *             permissions = listOf(Manifest.permission.POST_NOTIFICATIONS),
 *             reason = "需要通知权限查看网络请求日志"
 *         )
 *         // 处理结果
 *     }
 * }) {
 *     Text("申请通知权限")
 * }
 * ```
 *
 * 4. 防内存泄漏
 * ``` kotlin
 * override fun onDestroy() {
 *     super.onDestroy()
 *     // 清空当前页面的权限申请标记
 *     PermissionUtils.clearRequestFlag(this)
 * }
 * ```
 * @see <a href="https://github.com/guolindev/PermissionX">PermissionX GitHub</a>
 * @author YaDuo
 * @since 2026-01-06 17:41:13
 */
object PermissionUtils {
    private const val TAG = "PermissionUtils"

    /** 权限申请说明文案 */
    private const val REASON_REQUEST = "需要相关权限以使用完整功能"

    /** 永久拒绝引导文案 */
    private const val REASON_SETTING_GUIDE = "请在设置中开启权限，否则相关功能无法正常使用"

    /** 弹窗确认按钮文案 */
    private const val BTN_CONFIRM = "确定"

    /** 弹窗取消按钮文案 */
    private const val BTN_CANCEL = "取消"

    /** 设置页引导按钮文案 */
    private const val BTN_GO_TO_SETTING = "去设置"

    /** 防重复申请标记 key: Activity.hashCode()**/
    private val requestingFlagMap = mutableMapOf<Int, Boolean>()

    /**
     * 权限申请结果数据类
     *
     * 用于封装权限申请的结果信息，提供权限授予状态的完整视图。
     *
     * @param allGranted 布尔值，表示所有请求的权限是否已全部授予
     * @param grantedList 字符串列表，包含所有已授予的权限名称
     * @param deniedList 字符串列表，包含所有被拒绝的权限名称
     */
    data class PermissionResult(
        val allGranted: Boolean,
        val grantedList: List<String>,
        val deniedList: List<String>
    )

    /**
     * 检查单个权限是否已授予
     */
    fun isPermissionGranted(context: Context, permission: String) =
        PermissionX.isGranted(context, permission)

    /**
     * 检查多个权限是否都已授予
     */
    fun areAllPermissionsGranted(context: Context, permissions: List<String>) =
        permissions.all { isPermissionGranted(context, it) }

    /**
     * 申请单个权限（回调方式）
     * @param activity 必须传Activity（PermissionX依赖其生命周期）
     * @param permission 目标权限
     * @param reason 权限申请说明（给用户看的理由）
     * @param callback 结果回调
     */
    fun requestPermission(
        activity: FragmentActivity,
        permission: String,
        reason: String = REASON_REQUEST,
        callback: (PermissionResult) -> Unit
    ) {
        requestPermissions(activity, listOf(permission), reason, callback)
    }

    /**
     * 申请单个权限（协程方式）
     */
    suspend fun requestPermissionByCoroutine(
        activity: FragmentActivity,
        permission: String,
        reason: String = REASON_REQUEST
    ) = requestPermissionsByCoroutine(activity, listOf(permission), reason)

    /**
     * 申请多个权限
     */
    suspend fun requestPermissionsByCoroutine(
        activity: FragmentActivity,
        permissions: List<String>,
        reason: String = REASON_REQUEST
    ) = suspendCancellableCoroutine { continuation ->
        requestPermissions(activity, permissions, reason) { result ->
            continuation.resume(result)
        }
    }

    /**
     * 申请多个权限（回调方式）
     * @param activity 必须传Activity（PermissionX依赖其生命周期）
     * @param permissions 权限列表
     * @param reason 权限申请说明（给用户看的理由）
     * @param callback 结果回调
     */
    fun requestPermissions(
        activity: FragmentActivity,
        permissions: List<String>,
        reason: String = REASON_REQUEST,
        callback: (PermissionResult) -> Unit
    ) {
        val activityKey = activity.hashCode()
        // 防重复申请
        if (requestingFlagMap[activityKey] == true) {
            LogUtil.w(TAG, "当前页面正在申请权限，请勿重复调用")
            callback(PermissionResult(false, emptyList(), permissions))
            return
        }
        requestingFlagMap[activityKey] = true

        // PermissionX 申请逻辑
        PermissionX.init(activity)
            .permissions(permissions)
            .onExplainRequestReason { scope, deniedList ->
                // 权限被拒绝时，展示申请理由
                scope.showRequestReasonDialog(deniedList, reason, BTN_CONFIRM, BTN_CANCEL)
            }
            .onForwardToSettings { scope, deniedList ->
                // 权限永久拒绝时，引导到设置页
                scope.showForwardToSettingsDialog(
                    deniedList,
                    REASON_SETTING_GUIDE,
                    BTN_GO_TO_SETTING,
                    BTN_CANCEL
                )
            }
            .request { allGranted, grantedList, deniedList ->
                requestingFlagMap.remove(activityKey)
                callback(PermissionResult(allGranted, grantedList, deniedList))
            }
    }

    /**
     * 清空当前页面的申请标记（建议在Activity onDestroy调用，避免内存泄漏）
     */
    fun clearRequestFlag(activity: Activity) = requestingFlagMap.remove(activity.hashCode())
}