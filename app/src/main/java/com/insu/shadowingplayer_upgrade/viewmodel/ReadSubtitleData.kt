package com.insu.shadowingplayer_upgrade.viewmodel

import android.net.Uri
import com.insu.shadowingplayer_upgrade.ui.video.data.SubtData
import com.insu.shadowingplayer_upgrade.ui.video.data.VideoData
import kotlinx.android.synthetic.main.activity_video.*
import java.io.*
import java.util.*
import kotlin.concurrent.timer


class ReadSubtitleData{
    var parsedSubt=mutableListOf<SubtData>()
    var item=Subtitle.NOSUBTITLE
    private fun cleanSubtitle(str:String):String
    {
        var temp:String=str.replace("<br>","\n")

        var i=0;var j=0
        try
        {
            while((temp.indexOf('<').also{i=it})!=-1)
            {
                i=temp.indexOf('<')
                j=str.indexOf('>')
                temp=temp.removeRange(i,j-i+1)
            }
            return temp;
        }catch(e:Exception ){
            return temp
        }

    }
    fun readSubTitleData(currentVideo: VideoData){
        parsedSubt.clear()
        var path=currentVideo.path
        val smiPath: String =
            path.substring(0, path.lastIndexOf(".")) + ".smi"
        val srtPath: String =
            path.substring(0,path.lastIndexOf("."))+".srt"
        val smiFile = File(smiPath)
        val srtFile= File(srtPath)
        if (smiFile.isFile && smiFile.canRead()) {
            item=Subtitle.SMI
            readSmiData(smiFile)
        }
        else if(srtFile.isFile&&srtFile.canRead()){
            item=Subtitle.SRT
            readSrtData(srtFile)
        }else{
            item=Subtitle.NOSUBTITLE
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
                            parsedSubt.add(SubtData(time,cleanSubtitle(text)))
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
            item=Subtitle.NOSUBTITLE
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
                        parsedSubt.add(SubtData(time,text))
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
            item=Subtitle.NOSUBTITLE
            //Toast.makeText(this,"hi",Toast.LENGTH_LONG).show()
        }
    }
}
enum class Subtitle{
    SRT,SMI,NOSUBTITLE
}