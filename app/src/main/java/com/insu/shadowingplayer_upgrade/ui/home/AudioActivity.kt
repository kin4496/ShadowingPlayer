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
import java.util.*
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask

class AudioActivity : AppCompatActivity() {

    lateinit var uri:Uri
    lateinit var path:String
    var titleTask: Timer?=null
    var seekBarTask:Timer?=null
    var useSmi=false
    var useSrt=false
    var parsedSrt=mutableListOf<SubtData>()
    var parsedSmi = mutableListOf<SubtData>()
    lateinit var subAdapter:sublistAdapter
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


        startService(intent)
        playBt.setOnClickListener{
            if(MainActivity.mService?.isPlaying()==false){
                playBt.setImageResource(R.drawable.ic_pause_black_24dp)
                MainActivity.mService?.play()
            }else{
                playBt.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                MainActivity.mService?.pause()
            }
        }
        readSubTitleData()
        if(useSmi){
            subAdapter=sublistAdapter(this,parsedSmi)
            audioSubtitleListView.adapter=subAdapter

        }else if(useSrt){

            subAdapter=sublistAdapter(this,parsedSrt)
            audioSubtitleListView.adapter=subAdapter

        }
        audioSubtitleListView.requestFocusFromTouch()
        if(useSmi||useSrt){
            audioSubtitleListView.setOnItemClickListener { parent, view, position, id ->
                audioMove(id.toInt())
            }
        }
    }

    override fun onResume() {
        super.onResume()

        seekBar.progress = 0
        seekBarTask=timer(period=500){
            var cur=if(MainActivity.mService==null){
                0
            }else{
                MainActivity.mService!!.currentPlaying()
            }
            //seekBar.progress=0
            seekBar.progress=cur
            startTextView.text=durationTextConvert(cur)
        }

        if(useSmi){
            titleSmiTask()
        }
        else if(useSrt){

            titleSrtTask()
        }
    }
    private fun audioMove(pos:Int){

        if(useSmi){
            //Toast.makeText(this,"${pos.toInt()} : ${parsedSmi[pos.toInt()].time.toInt()}",Toast.LENGTH_LONG).show()
            MainActivity.mService?.seekTo(parsedSmi[pos.toInt()].time.toInt())
        }
        else if(useSrt){
            //Toast.makeText(this,"${pos.toInt()} : ${parsedSrt[pos.toInt()].time.toInt()}",Toast.LENGTH_LONG).show()
            MainActivity.mService?.seekTo(parsedSrt[pos.toInt()].time.toInt())
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
        menuInflater.inflate(R.menu.audio,menu)
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
            if(t.time>MainActivity.mService!!.currentPlaying())
                break
            id++
        }
        if(id!=0)
            id-=1
        subtitleListView.setSelection(id)
        parsedSmi[id].isUse=true
    }
    override fun getSupportParentActivityIntent(): Intent? {
        val intent=Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return intent
    }
    private fun srtSearch(){
        var id=0
        for(t in parsedSrt){
            if(t.time>MainActivity.mService!!.currentPlaying())
                break
            id++
        }
        if(id!=0)
            id-=1
        subtitleListView.setSelection(id)
        parsedSrt[id].isUse=true
    }
    override fun onPause() {
        super.onPause()

        titleTask?.cancel()
        seekBarTask?.cancel()
    }
    private fun titleSmiTask(){
        titleTask=timer(period=1000){
            if(MainActivity.mService!=null){
                var cur=MainActivity.mService!!.currentPlaying()
                var id=0
                while(true){
                    if(id==parsedSmi.size-1)
                        break
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
                        subAdapter.notifyDataSetChanged()
                    }
                }
            }

        }
    }
    private fun titleSrtTask(){
        titleTask=timer(period=1000){

            if(MainActivity.mService!=null){

                var cur=MainActivity.mService!!.currentPlaying()
                var id=0
                while(true){
                    if(id==parsedSrt.size-1)
                        break
                    if(cur>=parsedSrt[id].time&&cur<parsedSrt[id+1].time)
                        break
                    parsedSrt[id].isUse=false
                    id++
                }
                runOnUiThread {
                    if(!parsedSrt[id].isUse&&cur>=parsedSrt[0].time){
                        parsedSrt[id].isUse=true
                        while(true){
                            id++
                            if(id>=parsedSrt.size-1)
                                break
                            parsedSrt[id].isUse=false
                        }
                        subAdapter.notifyDataSetChanged()
                    }
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
