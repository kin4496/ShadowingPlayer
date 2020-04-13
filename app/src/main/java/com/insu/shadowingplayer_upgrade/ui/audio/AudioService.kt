package com.insu.shadowingplayer_upgrade.ui.audio

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder

class AudioService: Service() {
    val mBinder=AudioServiceBinder()
    val mediaPlayer=MediaPlayer()
    var title=""


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
        mediaPlayer.pause()
    }
    fun play(){
        mediaPlayer.start()
    }
    fun seekTo(pos:Int){
        mediaPlayer.seekTo(pos)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }
}