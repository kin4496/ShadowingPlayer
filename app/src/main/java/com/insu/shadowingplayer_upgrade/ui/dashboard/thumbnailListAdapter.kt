package com.insu.shadowingplayer_upgrade.ui.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.insu.shadowingplayer_upgrade.R

class thumbnailListAdapter(val context: Context, val thumbnaillist:MutableList<VideoData>):
BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: Viewholder
        if(convertView==null){
            view= LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_thumbnail,parent,false)
            holder= Viewholder(view)
            view.tag=holder
        }else{
            view=convertView
            holder=view.tag as Viewholder
        }

        var thumbnail=thumbnaillist[position]

        holder.text.text=thumbnail.title

        holder.image.setImageBitmap(thumbnail.img)

        return view
    }

    override fun getItem(position: Int): Any {
        return thumbnaillist[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return thumbnaillist.size
    }
    private class Viewholder(view: View){
        var image: ImageView =view.findViewById(R.id.thumbnail)
        var text: TextView =view.findViewById(R.id.titleTextView)
    }
}
