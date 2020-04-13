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

class DashboardFragment : Fragment() {

    var folderList=mutableListOf<String>()
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)


        val folderListView: ListView =root.findViewById(R.id.folderListView)
        var folderSet=mutableSetOf<String>()
        val adBanner: AdView =root.findViewById(R.id.videoBanner)
        val adRequest = AdRequest.Builder().build()
        adBanner.loadAd(adRequest)
        for(i in MainActivity.videos){
            folderSet.add(i.foldername)
        }
        for(i in folderSet){
            folderList.add(i)
        }
        val folderAdapter=folderListAdapter(activity!!,folderList)
        folderListView.adapter=folderAdapter
        folderListView.requestFocusFromTouch()
        folderListView.setOnItemClickListener { parent, view, position, id ->
            clickFolder(id.toInt())
        }
        return root
    }
    private fun clickFolder(id:Int){
        val intent= Intent(activity,FolderActivity::class.java)
        intent.putExtra("FolderName",folderList[id])
        startActivity(intent)
    }

}
