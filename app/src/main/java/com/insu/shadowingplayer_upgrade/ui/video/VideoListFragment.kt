package com.insu.shadowingplayer_upgrade.ui.video

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.insu.shadowingplayer_upgrade.MainActivity
import com.insu.shadowingplayer_upgrade.R
import com.insu.shadowingplayer_upgrade.ui.video.data.VideoData
import com.insu.shadowingplayer_upgrade.viewmodel.ViewModel

class VideoListFragment:Fragment() {
    private val viewModel: ViewModel by lazy{
        ViewModel.getViewModelInstance()
    }
    private var currentVideos=mutableListOf<VideoData>()
        get()=viewModel.currentVideos
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val folderListView: ListView =root.findViewById(R.id.folderListView)

        setUpAd(root)

        val thumbnailListAdapter=ThumbnailListAdapter(activity!!,currentVideos)
        folderListView.adapter=thumbnailListAdapter
        folderListView.requestFocusFromTouch()
        folderListView.setOnItemClickListener { _, _, _, id ->
            clickVideo(id.toInt())
        }

        return root
    }
    private fun setUpAd(root:View){
        val adBanner: AdView =root.findViewById(R.id.videoBanner)
        val adRequest = AdRequest.Builder().build()
        adBanner.loadAd(adRequest)
    }
    private fun clickVideo(id:Int){
        val intent= Intent(activity!!,VideoActivity::class.java)
        viewModel?.let {
            it.readSubtitle(currentVideos[id])
        }
        startActivity(intent)
    }
}