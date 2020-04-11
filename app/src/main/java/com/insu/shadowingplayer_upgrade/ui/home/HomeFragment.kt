package com.insu.shadowingplayer_upgrade.ui.home

import android.content.Intent
import android.os.Bundle
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

class HomeFragment : Fragment() {


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val audioListView: ListView =root.findViewById(R.id.audioListView)
        val audioAdapter=AudioListAdapter(activity!!,MainActivity.audios)
        audioListView.adapter=audioAdapter
        audioListView.requestFocusFromTouch()
        audioListView.setOnItemClickListener { parent, view, position, id ->
            clickAudio(id.toInt())
        }
        return root
    }
    private fun clickAudio(id:Int){
        val intent= Intent(activity!!,AudioActivity::class.java)
        intent.putExtra("id",id)
        startActivity(intent)
    }
}
