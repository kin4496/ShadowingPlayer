package com.insu.shadowingplayer_upgrade.ui.video

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.insu.shadowingplayer_upgrade.MainActivity
import com.insu.shadowingplayer_upgrade.R
import kotlinx.android.synthetic.main.activity_folder.*

class FolderActivity : AppCompatActivity() {

    private var videos=mutableListOf<VideoData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)
        //TODO("videos에 폴더 명에 맞는 비디오 넣어 주기")
        val folder=intent.getStringExtra("FolderName")
        for(i in MainActivity.videos){
            if(i.foldername==folder)
                videos.add(i)
        }
        val thumbnailAdapter=thumbnailListAdapter(this,videos)
        videoListView.adapter=thumbnailAdapter
        videoListView.requestFocusFromTouch()
        videoListView.setOnItemClickListener { parent, view, position, id ->
            clickVideo(id.toInt())
        }
    }
    private fun clickVideo(id:Int){
        val intent= Intent(this,VideoActivity::class.java)
        intent.putExtra("Title",videos[id].title)
        intent.putExtra("Uri",videos[id].uri.toString())
        startActivity(intent)
    }
}
