package com.insu.shadowingplayer_upgrade.ui.audio

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager

class AudioService: Service() {
    val mBinder=AudioServiceBinder()
    val mediaPlayer=MediaPlayer()
    var title=""
    var isPrepared=false
    override fun onCreate(){
        super.onCreate()
        mediaPlayer.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        mediaPlayer.setOnPreparedListener { mp ->
            isPrepared=true
            mp!!.start()
        }
        mediaPlayer.setOnCompletionListener {
            isPrepared=false
        }
        mediaPlayer.setOnErrorListener { mp, what, extra ->
            isPrepared=false
            false
        }
        mediaPlayer.setOnSeekCompleteListener {

        }
    }
    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }
    inner class AudioServiceBinder: Binder() {
        fun getService():AudioService=this@AudioService
    }
    fun initMediaPlayer(uri:Uri,title:String){
        this.title=title
        mediaPlayer.reset()
        mediaPlayer.setDataSource(applicationContext,uri)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }
    fun isPlaying():Boolean=mediaPlayer.isPlaying
    fun currentPlaying():Int=mediaPlayer.currentPosition
    fun pause(){
        if(isPrepared)
            mediaPlayer.pause()
    }
    fun play(){
        if(isPrepared)
            mediaPlayer.start()
    }
    fun seekTo(pos:Int){
        if(isPrepared)
            mediaPlayer.seekTo(pos)
    }
    override fun onDestroy() {
        super.onDestroy()
        if(mediaPlayer!=null){
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

}