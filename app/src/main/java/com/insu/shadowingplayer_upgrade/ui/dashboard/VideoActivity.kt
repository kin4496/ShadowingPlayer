package com.insu.shadowingplayer_upgrade.ui.dashboard

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.MediaController
import com.`is`.shadowingplayer.SubtData
import com.insu.shadowingplayer_upgrade.R
import kotlinx.android.synthetic.main.activity_video.*
import java.io.*
import java.util.*
import kotlin.concurrent.timer

class VideoActivity : AppCompatActivity() {


    var useSmi=false
    var useSrt=false
    var titleTask: Timer?=null
    var parsedSrt=mutableListOf<SubtData>()
    var parsedSmi = mutableListOf<SubtData>()
    lateinit var subAdapter:sublistAdapter
    lateinit var uri:Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        title=intent.getStringExtra("Title")
        var mc= MediaController(this)
        videoView.setOnPreparedListener { mp ->
            mp.setOnVideoSizeChangedListener { mp, width, height ->
                videoView.setMediaController(mc)
                mc.setAnchorView(videoView)
            }
        }
        uri= Uri.parse(intent.getStringExtra("Uri"))
        videoView.setVideoURI(uri)
        videoView.requestFocus()
        videoView.start()
        readSubTitleData(uri)
        if(useSmi){
            subAdapter=sublistAdapter(this,parsedSmi)
            subtitleListView.adapter=subAdapter
            titleSmiTask()
        }else if(useSrt){
            subAdapter=sublistAdapter(this,parsedSrt)
            subtitleListView.adapter=subAdapter
            titleSrtTask()
        }
        if(useSmi||useSrt){
            subtitleListView.setOnItemClickListener { parent, view, position, id ->
                videomove(id)
            }
        }


    }

    override fun onResume() {
        super.onResume()
        if(useSmi){
            titleSmiTask()
        }else if(useSrt){
            titleSrtTask()
        }
    }
    private fun titleSmiTask(){
        titleTask=timer(period=1000){
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
                    subAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        titleTask?.cancel()
    }
    private fun titleSrtTask(){
        titleTask=timer(period=1000){
            var cur=videoView.currentPosition
            var id=0
            while(true){
                if(id==parsedSrt.size-1)
                    break;
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
    private fun videomove(pos:Long){

        if(useSmi){
            //Toast.makeText(this,"${pos.toInt()} : ${parsedSmi[pos.toInt()].time.toInt()}",Toast.LENGTH_LONG).show()
            videoView.seekTo(parsedSmi[pos.toInt()].time.toInt())
        }
        else if(useSrt){
            //Toast.makeText(this,"${pos.toInt()} : ${parsedSrt[pos.toInt()].time.toInt()}",Toast.LENGTH_LONG).show()
            videoView.seekTo(parsedSrt[pos.toInt()].time.toInt())
        }

    }
    private fun readSubTitleData(uri:Uri){
        var path=uritopath(uri)
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
    private fun uritopath(uri:Uri):String{

        val proj = arrayOf(MediaStore.Images.Media.DATA)

        val cursor: Cursor? = uri?.let{contentResolver.query(it, proj, null, null, null)}
        cursor!!.moveToNext()
        val path: String =
            cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))


        //Log.d(FragmentActivity.TAG, "getRealPathFromURI(), path : $uri")

        cursor.close()
        return path

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
}
