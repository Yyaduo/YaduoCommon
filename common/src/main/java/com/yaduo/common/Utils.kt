package com.yaduo.common

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.yaduo.common.extra.getOrNull
import com.yaduo.common.log.LogUtil
import java.math.BigInteger
import java.security.MessageDigest

/**
 * 公共工具类
 *
 * @author YaDuo
 * @since 2025-05-14 16:21:22
 */
object Utils {

    /**
     * 根据uuid生成uid
     * 通过SHA-256哈希计算
     * 对这个整数取绝对值后模'Yaduo'的ASCII码
     */
    fun genUid(uuid: String): String {
        LogUtil.i(content = "genUid: uuid = $uuid")
        val hashSHA = MessageDigest.getInstance("SHA-256").run {
            update(uuid.toByteArray(Charsets.UTF_8))
            digest()
        }

        val hashNumber = BigInteger(1, hashSHA)

        val modByYaduo = "Yaduo".let { str ->
            val asciiCodes = str.map { it.code.toString().padStart(3, '0') }
            BigInteger(asciiCodes.joinToString(""))
        }

        val uidNumber = hashNumber.mod(modByYaduo).toString().padStart(13, '0')
        LogUtil.i(content = "genUid: Uid = $uidNumber")
        return uidNumber
    }

    /**
     * 注册应用进程生命周期事件观察者，监听应用整体前后台状态变化
     *
     * 通过 [ProcessLifecycleOwner] 监听应用进程的生命周期，替代传统的ActivityLifeCycleCallback实现
     */
    fun registerLifecycleEventObserver() {
        val lifecycleOwner = ProcessLifecycleOwner.getOrNull() ?: run {
            LogUtil.e(content = "ProcessLifecycleOwner获取失败，无法注册生命周期观察者")
            return
        }

        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                LogUtil.i(content = "onProcessLifecycleChanged:  $event")
            }
        })
    }
}