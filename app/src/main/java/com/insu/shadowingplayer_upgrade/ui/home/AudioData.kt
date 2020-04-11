package com.insu.shadowingplayer_upgrade.ui.home

import android.net.Uri

class AudioData(
    var title:String,
    var durationText:String,
    var uri: Uri,
    var path:String
){
    var duration=durationText.toInt()
    var durationForTextView=durationTextConvert()
    private fun durationTextConvert():String{
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