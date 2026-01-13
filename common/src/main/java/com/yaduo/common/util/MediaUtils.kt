package com.yaduo.common.util

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.yaduo.common.log.LogUtil

/**
 * ### 媒体工具类
 *
 * @author YaDuo
 * @since 2026-01-13 14:50:01
 */
object MediaUtils {
    private const val TAG = "MediaUtils"

    /**
     * 播放指定URL的媒体文件
     *
     * @param url 媒体文件的URL地址，如果为null则直接返回
     * @param onLoading 加载中回调
     * @param onPlaying 开始播放回调
     * @param onFinished 播放完成回调
     * @param onError 错误回调
     */
    fun playMedia(
        url: String?,
        onLoading: () -> Unit,
        onPlaying: () -> Unit,
        onFinished: () -> Unit,
        onError: () -> Unit
    ) {
        if (url == null) return
        runCatching {
            onLoading()
            MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener {
                    onPlaying()
                    start()
                }
                setOnCompletionListener {
                    release()
                    onFinished()
                }
                setOnErrorListener { _, _, _ ->
                    onError()
                    false
                }
                prepareAsync()
            }
        }.onFailure {
            LogUtil.e(TAG, "play failed:", it)
            onError()
        }
    }
}