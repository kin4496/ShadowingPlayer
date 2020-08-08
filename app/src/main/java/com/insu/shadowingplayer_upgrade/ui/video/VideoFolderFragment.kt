package com.insu.shadowingplayer_upgrade.ui.video

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.insu.shadowingplayer_upgrade.DEBUG_TAG
import com.insu.shadowingplayer_upgrade.MainActivity
import com.insu.shadowingplayer_upgrade.R
import com.insu.shadowingplayer_upgrade.viewmodel.ViewModel

class VideoFolderFragment : Fragment() {
    private val viewModel:ViewModel by lazy{
        ViewModel.getViewModelInstance()
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        Log.d(DEBUG_TAG,"onCreateView")
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val folderListView: ListView =root.findViewById(R.id.folderListView)
        var folderSet=mutableSetOf<String>()
        val adBanner: AdView =root.findViewById(R.id.videoBanner)
        val adRequest = AdRequest.Builder().build()
        adBanner.loadAd(adRequest)
        viewModel.let{
            for(i in it.videoList){
                folderSet.add(i.foldername)
            }
        }
        var folderList=mutableListOf<String>()
        for(i in folderSet){
            folderList.add(i)
        }
        val folderAdapter=FolderListAdapter(activity!!,folderList)
        folderListView.adapter=folderAdapter
        folderListView.requestFocusFromTouch()
        folderListView.setOnItemClickListener { _, _, _, id ->
            viewModel?.setCurrentVideo(folderList[id.toInt()])
            clickFolder()
        }
        return root
    }
    private fun clickFolder(){
        val navController = activity!!.findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.navigation_videoList)
    }

}
