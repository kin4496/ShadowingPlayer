package com.insu.shadowingplayer_upgrade.ui.audio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.insu.shadowingplayer_upgrade.MainActivity
import com.insu.shadowingplayer_upgrade.R
private const val TAG="HomeFragment"
class HomeFragment : Fragment() {


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val audioListView: ListView =root.findViewById(R.id.audioListView)
        val audioAdapter=AudioListAdapter(activity!!,MainActivity.audios)
        val adBanner: AdView =root.findViewById(R.id.audioBanner)
        val adRequest = AdRequest.Builder().build()
        adBanner.loadAd(adRequest)
        audioListView.adapter=audioAdapter
        audioListView.requestFocusFromTouch()
        audioListView.setOnItemClickListener { parent, view, position, id ->
            clickAudio(id.toInt())
        }
        return root
    }
    private fun clickAudio(id:Int){
        if(MainActivity.mService!=null){
            if(MainActivity.mService!!.title!=MainActivity.audios[id].title){
                Log.d(TAG,"new start")
                MainActivity.mService?.initMediaPlayer(MainActivity.audios[id].uri,MainActivity.audios[id].title)
            }
        }
        val intent= Intent(activity!!,AudioActivity::class.java)
        intent.putExtra("id",id)
        startActivity(intent)
    }
}
