package com.insu.shadowingplayer_upgrade.ui.video

import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.MediaController
import com.insu.shadowingplayer_upgrade.ui.video.data.SubtData
import com.insu.shadowingplayer_upgrade.R
import com.insu.shadowingplayer_upgrade.viewmodel.Subtitle
import com.insu.shadowingplayer_upgrade.viewmodel.ViewModel
import kotlinx.android.synthetic.main.activity_video.*
import java.io.*
import java.util.*
import kotlin.concurrent.timer

class VideoActivity : AppCompatActivity() {
    var timerTask:Timer?=null

    var viewModel= ViewModel.getViewModelInstance()
    var parsedSubt=viewModel.getSubtData()
    var currentVideo=viewModel.currentVideo

    lateinit var subAdapter:SublistAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        var mc= MediaController(this)
        videoView.setOnPreparedListener { mp ->
            mp.setOnVideoSizeChangedListener { mp, width, height ->
                videoView.setMediaController(mc)
                mc.setAnchorView(videoView)
            }
        }

        videoView.setVideoURI(currentVideo.uri)
        videoView.requestFocus()
        videoView.start()

        subAdapter=SublistAdapter(this,parsedSubt)
        subtitleListView.adapter=subAdapter



        subtitleListView.setOnItemClickListener { _, _, _, id ->
                 videoView.seekTo(parsedSubt[id.toInt()].time.toInt()) }


    }
    override fun onResume() {
        super.onResume()
        startTimerTask()
        subtSearch()
    }
    override fun onPause() {
        super.onPause()
        timerTask?.cancel()
    }
    private fun startTimerTask(){
        timerTask=timer(period=1000){
            var cur=videoView.currentPosition
            var id=0
            while(true){
                if(id==parsedSubt.size-1)
                    break;
                if(cur>=parsedSubt[id].time&&cur<parsedSubt[id+1].time)
                    break
                parsedSubt[id].isUse=false
                id++
            }
            runOnUiThread {
                if(!parsedSubt[id].isUse&&cur>=parsedSubt[0].time){
                    parsedSubt[id].isUse=true
                    while(true){
                        id++
                        if(id>=parsedSubt.size-1)
                            break
                        parsedSubt[id].isUse=false
                    }
                    subAdapter.notifyDataSetChanged()
                }
            }

        }
    }
    private fun subtSearch(){
        var id=0
        for(t in parsedSubt){
            if(t.time>videoView.currentPosition)
                break
            id++
        }
        if(id!=0)
            id-=1
        subtitleListView.setSelection(id)
        parsedSubt[id].isUse=true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.video,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.search->{
                if(viewModel.getSubtitleType()!=Subtitle.NOSUBTITLE)
                    subtSearch()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
