package com.insu.shadowingplayer_upgrade.viewmodel

import android.content.Context
import android.view.View
import com.insu.shadowingplayer_upgrade.repository.Repository
import com.insu.shadowingplayer_upgrade.ui.audio.AudioData
import com.insu.shadowingplayer_upgrade.ui.video.data.SubtData
import com.insu.shadowingplayer_upgrade.ui.video.data.VideoData
import java.util.*

class ViewModel() {
    companion object{
        private var instance:ViewModel?=null
        fun getViewModelInstance():ViewModel{
            return if(instance==null){
                instance= ViewModel()
                instance!!
            }else
                instance!!
        }
    }
    var currentVideos=mutableListOf<VideoData>()

    var videoList= mutableListOf<VideoData>()
    var audioList= mutableListOf<AudioData>()

    lateinit var currentVideo: VideoData

    private var readSubtitleData=ReadSubtitleData()

    fun readSubtitle(currentVideo:VideoData){
        this.currentVideo=currentVideo
        readSubtitleData.readSubTitleData(currentVideo)
    }
    fun getSubtitleType()=readSubtitleData.item
    fun getSubtData()=readSubtitleData.parsedSubt
    fun setCurrentVideo(path:String){
        currentVideos.clear()
        for(i in videoList){
            if(i.foldername==path)
                currentVideos.add(i)
        }
    }
    fun getDataFromRepository(context:Context){
        videoList.clear()
        audioList.clear()
        val mRepo:Repository?=Repository.getRepositoryInstance()
        mRepo?.initData(context)
        mRepo?.let {
            videoList=it.videoList
            audioList=it.audioList
        }
    }
}