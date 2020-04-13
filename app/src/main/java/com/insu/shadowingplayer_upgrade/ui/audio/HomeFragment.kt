package com.insu.shadowingplayer_upgrade.ui.audio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.insu.shadowingplayer_upgrade.MainActivity
import com.insu.shadowingplayer_upgrade.R
import kotlinx.android.synthetic.main.activity_audio.*
import kotlinx.android.synthetic.main.fragment_home.*

private const val TAG="HomeFragment"
class HomeFragment : Fragment() {

    lateinit var audioTitleTextView: TextView
    lateinit var playBt:ImageButton
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val audioListView: ListView =root.findViewById(R.id.audioListView)
        val audioAdapter=AudioListAdapter(activity!!,MainActivity.audios)
        val currentLayout:LinearLayout=root.findViewById(R.id.currentLayout)
        audioTitleTextView=root.findViewById(R.id.audioTitleTextView)
        playBt=root.findViewById(R.id.playOrPauseBt)
        val adBanner: AdView =root.findViewById(R.id.audioBanner)
        val adRequest = AdRequest.Builder().build()
        adBanner.loadAd(adRequest)
        audioListView.adapter=audioAdapter
        audioListView.requestFocusFromTouch()
        playBt.setOnClickListener{
            if(MainActivity.mService?.isPlaying()==false){
                if(MainActivity.mService!=null){
                    if(MainActivity!!.mService!!.title!=""){
                        playBt.setImageResource(R.drawable.ic_pause_black_24dp)
                        MainActivity.mService?.play()
                    }
                }
            }else{
                playBt.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                MainActivity.mService?.pause()
            }
        }
        currentLayout.setOnClickListener {
            if(MainActivity.mService!=null){
                if(MainActivity!!.mService!!.title!=""){
                    for(i in MainActivity.audios.indices){
                        if(MainActivity!!.mService!!.title==MainActivity.audios[i].title){
                            val intent= Intent(activity!!,AudioActivity::class.java)
                            intent.putExtra("id",i)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
        audioListView.setOnItemClickListener { parent, view, position, id ->
            clickAudio(id.toInt())
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        if(MainActivity.mService!=null){
            if(MainActivity!!.mService!!.title!=""){
                audioTitleTextView.text=MainActivity!!.mService!!.title
            }
        }
        if(MainActivity.mService?.isPlaying()==false){
            playBt.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        }else{
            playBt.setImageResource(R.drawable.ic_pause_black_24dp)
        }
    }
    private fun clickAudio(id:Int){
        if(MainActivity.mService!=null){
            if(MainActivity.mService!!.title!=MainActivity.audios[id].title){
                MainActivity.mService?.initMediaPlayer(MainActivity.audios[id].uri,MainActivity.audios[id].title)
            }
        }
        val intent= Intent(activity!!,AudioActivity::class.java)
        intent.putExtra("id",id)
        startActivity(intent)
    }
}
