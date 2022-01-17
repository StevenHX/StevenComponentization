package com.hx.steven.manager

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import java.lang.Exception

/**
 * 音频管理类
 */
class AudioManager {
    /**
     * 播放通过音频
     */
   fun playPassAudio(context: Context) {
        try {
            val fd:AssetFileDescriptor = context.assets.openFd("Pass.mp3")
            val mMediaPlayer = MediaPlayer()
            mMediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
            mMediaPlayer.prepare()
            mMediaPlayer.start()
        }catch (except: Exception) {
        }
   }
    /**
     * 播放不通过音频
     */
    fun playNgAudio(context: Context) {
        try {
            val fd:AssetFileDescriptor = context.assets.openFd("NGmusic.mp3")
            val mMediaPlayer = MediaPlayer()
            mMediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
            mMediaPlayer.prepare()
            mMediaPlayer.start()
        }catch (except: Exception) {
        }
    }
}