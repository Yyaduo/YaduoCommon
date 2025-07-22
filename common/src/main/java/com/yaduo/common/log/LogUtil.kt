package com.yaduo.common.log

import android.os.Process
import android.util.Log
import com.yaduo.common.applogic.AppLogicUtil

/**
 * @author YaDuo
 * @since 2025-05-15 00:46:26
 * // FIXME: 打印的文件名和层级可能出错
 */
object LogUtil {

    /** 默认日志的TAG **/
    private val DEFAULT_LOG_TAG = AppLogicUtil.getApp().applicationInfo.packageName

    /** 默认日志文件名 **/
    private val DEFAULT_LOG_FILE_NAME = "${AppLogicUtil.getApp().applicationInfo.name}_log"

    /** 是否需要记录调用栈信息，在Debug包下为true **/
    private var isNeedPrintStack: Boolean = true

    /** 当前日志调用栈信息，用于记录日志的类名、方法名、文件名和行号 **/
    private var stackTraceElement: StackTraceElement? = null

    /**
     * 打印DEBUG级别日志
     */
    fun d(
        tag: String = DEFAULT_LOG_TAG,
        content: String
    ) {
        printLog(Log.DEBUG, tag, content)
    }

    /**
     * 打印INFO级别日志
     */
    fun i(
        tag: String = DEFAULT_LOG_TAG,
        content: String
    ) {
        printLog(Log.INFO, tag, content)
    }

    /**
     * 打印WARN级别日志
     */
    fun w(
        tag: String = DEFAULT_LOG_TAG,
        content: String
    ) {
        printLog(Log.WARN, tag, content)
    }

    /**
     * 打印ERROR级别日志
     */
    fun e(
        tag: String = DEFAULT_LOG_TAG,
        content: String
    ) {
        printLog(Log.ERROR, tag, content)
    }

    /**
     * 打印日志方法
     */
    private fun printLog(level: Int, tag: String, content: String) {
        if (stackTraceElement == null) {
            recordLogStack()
        }
        when (level) {
            Log.DEBUG -> {
                Log.d(tag, buildLogContent(tag, content))
            }

            Log.INFO -> {
                Log.i(tag, buildLogContent(tag, content))
            }

            Log.WARN -> {
                Log.w(tag, buildLogContent(tag, content))
            }

            Log.ERROR -> {
                Log.e(tag, buildLogContent(tag, content))
            }
        }
        stackTraceElement = null
    }

    /**
     * 记录日志栈
     */
    private fun recordLogStack() {
        if (isNeedPrintStack) {
            stackTraceElement = Throwable().stackTrace[3]
        }
    }

    /**
     * 创建日志内容，会传入Tag和Content
     * 将按照Tag + 进程号 + 线程号 + 方法名 + 具体内容 + 目标行号打印
     * @param tag 日志TAG
     * @param content 日志具体内容
     */
    private fun buildLogContent(tag: String, content: String): String {
        val methodName = "[${stackTraceElement?.methodName}]"
        val targetPosition = "[${stackTraceElement?.fileName}:${stackTraceElement?.lineNumber}]"
        return "[$tag]:: [${Process.myPid()}/${Process.myTid()}] $methodName -> $content$targetPosition"
    }
}