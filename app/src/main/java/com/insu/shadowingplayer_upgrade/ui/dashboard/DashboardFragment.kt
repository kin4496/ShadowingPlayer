package com.insu.shadowingplayer_upgrade.ui.dashboard

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.insu.shadowingplayer_upgrade.MainActivity
import com.insu.shadowingplayer_upgrade.R
import java.net.URI

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
