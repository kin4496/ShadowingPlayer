package com.insu.shadowingplayer_upgrade.ui.audio

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.insu.shadowingplayer_upgrade.R

class AudioListAdapter(val context: Context, val audioList:MutableList<AudioData>):
    BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: Viewholder
        if(convertView==null){
            view= LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_audio,parent,false)
            holder= Viewholder(view)
            view.tag=holder
        }else{
            view=convertView
            holder=view.tag as Viewholder
        }

        var audio=audioList[position]
        holder.duration.text=audio.durationForTextView
        holder.title.text=audio.title

        return view
    }

    override fun getItem(position: Int): Any {
        return audioList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return audioList.size
    }
    private class Viewholder(view: View){

        var title: TextView =view.findViewById(R.id.titleTextView)
        var duration:TextView=view.findViewById(R.id.durationTextView)
    }
}