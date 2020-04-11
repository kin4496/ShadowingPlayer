package com.insu.shadowingplayer_upgrade.ui.home

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.`is`.shadowingplayer.SubtData
import com.insu.shadowingplayer_upgrade.MainActivity
import com.insu.shadowingplayer_upgrade.R
import com.insu.shadowingplayer_upgrade.ui.dashboard.sublistAdapter
import kotlinx.android.synthetic.main.activity_audio.*
import kotlinx.android.synthetic.main.activity_audio.view.*
import kotlinx.android.synthetic.main.activity_video.*
import org.jetbrains.anko.image
import java.io.*
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask

class AudioActivity : AppCompatActivity() {

    lateinit var uri:Uri
    lateinit var path:String
    var useSmi=false
    var useSrt=false
    var parsedSrt=mutableListOf<SubtData>()
    var parsedSmi = mutableListOf<SubtData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)
        val id=intent.getIntExtra("id",0)
        val audio= MainActivity.audios[id]
        uri=audio.uri
        path=audio.path
        title=audio.title
        seekBar.max=audio.duration
        endTextView.text=audio.durationForTextView
        val intent= Intent(this,AudioService::class.java)
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE)
        //intent.putExtra("path",path)
        startService(intent)
        playBt.setOnClickListener{
            if(mService?.isPlaying()==false){
                mService?.play()
            }else{
                mService?.pause()
            }
        }
        seekBar.progress = 0
        timer(period=500){
            var cur=if(mService==null){
                0
            }else{
                mService!!.currentPlaying()
            }
            seekBar.progress=0
            seekBar.progress=cur
            startTextView.text=durationTextConvert(cur)
        }
        readSubTitleData()
        if(useSmi){
            val subAdapter=sublistAdapter(this,parsedSmi)
            audioSubtitleListView.adapter=subAdapter
            titleTask(subAdapter)
        }else if(useSrt){
            val subAdapter=sublistAdapter(this,parsedSrt)
            audioSubtitleListView.adapter=subAdapter
            titleTask(subAdapter)
        }
        if(useSmi||useSrt){
            subtitleListView.setOnItemClickListener { parent, view, position, id ->
                audioMove(id.toInt())
            }
        }
    }
    private fun audioMove(pos:Int){

        if(useSmi){
            //Toast.makeText(this,"${pos.toInt()} : ${parsedSmi[pos.toInt()].time.toInt()}",Toast.LENGTH_LONG).show()
            mService?.seekTo(parsedSmi[pos.toInt()].time.toInt())
        }
        else if(useSrt){
            //Toast.makeText(this,"${pos.toInt()} : ${parsedSrt[pos.toInt()].time.toInt()}",Toast.LENGTH_LONG).show()
            mService?.seekTo(parsedSrt[pos.toInt()].time.toInt())
        }

    }
    var mService:AudioService?=null
    var mBound=false
    var mConnection=object:ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            mService=null
            mBound=false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder=service as AudioService.AudioServiceBinder
            mService=binder.getService()
            mBound=true
            mService!!.initMediaPlayer(uri)

        }

    }
    override fun onStop() {
        super.onStop()
        if(mBound){
            unbindService(mConnection)
            mBound=false
        }
    }
    private fun readSubTitleData(){

        val smiPath: String =
            path.substring(0, path.lastIndexOf(".")) + ".smi"
        val srtPath: String =
            path.substring(0,path.lastIndexOf("."))+".srt"
        val smiFile = File(smiPath)
        val srtFile= File(srtPath)
        if (smiFile.isFile && smiFile.canRead()) {
            useSmi = true
            readSmiData(smiFile)
        } else {
            useSmi = false
            if(srtFile.isFile&&srtFile.canRead()){
                useSrt=true
                readSrtData(srtFile)
            }
        }
    }
    private fun readSmiData(smiFile: File){
        try {
            val buf = BufferedReader(InputStreamReader(FileInputStream(smiFile),"MS949"))
            var s:String?=null

            var time: Long = -1
            var text: String =""
            var smistart = false
            while (true) {
                s=buf.readLine()
                if(s==null)
                    break
                if (s.contains("<SYNC")) {
                    smistart = true
                    if (time != -1L) {
                        if(!text.contains("&nbsp")){
                            parsedSmi.add(SubtData(time,text,useSmi))
                        }

                    }
                    try{
                        time = s.substring(s.indexOf("=") + 1, s.indexOf(">")).toInt().toLong()
                        text = s.substring(s.indexOf(">") + 1, s.length)
                        text = text.substring(text.indexOf(">") + 1, text.length)
                    }catch(e:Exception){time=0L;text=""}
                } else {
                    if (smistart) {
                        text += s
                    }
                }

            }
            //Toast.makeText(this,parsedSmi[0].text, Toast.LENGTH_LONG).show()
            buf.close()
        } catch (e: IOException) {
            useSmi=false
            //Toast.makeText(this,"hi",Toast.LENGTH_LONG).show()
        }
    }
    private fun readSrtData(srtFile: File){
        try {
            val buf = BufferedReader(InputStreamReader(FileInputStream(srtFile),"MS949"))
            var s:String?=null
            var num=1
            var time: Long = -1
            var text=""
            var srtstart=true
            while (true) {
                s=buf.readLine()
                if(s==null)
                    break
                if (s==num.toString()) {
                    srtstart=true
                    if (time != -1L) {
                        parsedSrt.add(SubtData(time,text,useSmi))
                    }
                    s=buf.readLine()
                    try{
                        time=s.substring(0,2).toLong()*1000*60*60
                        time+=s.substring(3,5).toLong()*1000*60
                        time+=s.substring(6,s.indexOf(" ")).replace(",","").toLong()
                    }catch(e:Exception){time=0L}

                    num++
                } else {
                    if (srtstart) {
                        text = s
                        srtstart=false
                    }
                    else {
                        if(s=="")
                            continue
                        text+='\n'+s
                    }
                }

            }
            //Toast.makeText(this,parsedSmi[0].text, Toast.LENGTH_LONG).show()
            buf.close()
        } catch (e: IOException) {
            useSrt=false
            //Toast.makeText(this,"hi",Toast.LENGTH_LONG).show()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.video,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.search->{
                if(useSmi)
                    smiSearch()
                else if(useSrt)
                    srtSearch()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun smiSearch(){
        var id=0
        for(t in parsedSmi){
            if(t.time>videoView.currentPosition)
                break
            id++
        }
        if(id!=0)
            id-=1
        subtitleListView.setSelection(id)
        parsedSmi[id].isUse=true
    }
    private fun srtSearch(){
        var id=0
        for(t in parsedSrt){
            if(t.time>videoView.currentPosition)
                break
            id++
        }
        if(id!=0)
            id-=1
        subtitleListView.setSelection(id)
        parsedSrt[id].isUse=true
    }
    private fun titleTask(adapter: sublistAdapter){
        timer(period=1000){
            var cur=videoView.currentPosition
            var id=0
            while(true){
                if(id==parsedSmi.size-1)
                    break;
                if(cur>=parsedSmi[id].time&&cur<parsedSmi[id+1].time)
                    break
                parsedSmi[id].isUse=false
                id++
            }
            runOnUiThread {
                if(!parsedSmi[id].isUse&&cur>=parsedSmi[0].time){
                    parsedSmi[id].isUse=true
                    while(true){
                        id++
                        if(id>=parsedSmi.size-1)
                            break
                        parsedSmi[id].isUse=false
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
    private fun durationTextConvert(duration:Int):String{
        var second = (duration / 1000) % 60;
        var minute = (duration / (1000 * 60)) % 60;
        var hour = (duration / (1000 * 60 * 60)) % 24;

        var str="["
        if(hour<10){
            str+="0$hour:"
        }else{
            str+="$hour:"
        }
        if(minute<10){
            str+="0$minute:"
        }else{
            str+="$minute:"
        }
        if(second<10){
            str+="0$second]"
        }else{
            str+="$second]"
        }
        return str
    }
}
