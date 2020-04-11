package com.insu.shadowingplayer_upgrade.ui.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.insu.shadowingplayer_upgrade.R

class folderListAdapter(val context: Context, val folderList:MutableList<String>):
    BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: Viewholder
        if(convertView==null){
            view= LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_folder,parent,false)
            holder= Viewholder(view)
            view.tag=holder
        }else{
            view=convertView
            holder=view.tag as Viewholder
        }

        var folder=folderList[position]

        holder.text.text=folder

        return view
    }

    override fun getItem(position: Int): Any {
        return folderList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return folderList.size
    }
    private class Viewholder(view: View){

        var text: TextView =view.findViewById(R.id.Name)
    }
}
