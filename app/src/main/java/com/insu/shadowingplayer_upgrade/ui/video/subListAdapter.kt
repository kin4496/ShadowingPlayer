package com.insu.shadowingplayer_upgrade.ui.video

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.`is`.shadowingplayer.SubtData
import com.insu.shadowingplayer_upgrade.R
import kotlinx.android.synthetic.main.item_subtitle.view.*

class sublistAdapter(val context: Context, val sublist:MutableList<SubtData>): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder
        if(convertView==null){
            view= LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_subtitle,parent,false)
            holder= ViewHolder(view)
            view.tag=holder
        }else{
            view=convertView
            holder=view.tag as ViewHolder
        }

        var sub=sublist[position]

        if(sub.isUse){
            view.speech.setTextColor(Color.BLUE)
            view.timeview.setTextColor(Color.BLUE)
        }else{
            view.speech.setTextColor(Color.BLACK)
            view.timeview.setTextColor(Color.BLACK)
        }
        holder.text.text=sub.text
        holder.time.text=convetMilli(sub.time)
        //Toast.makeText(context,holder.text.text, Toast.LENGTH_LONG).show()
        return view
    }

    override fun getItem(position: Int): Any {
        return sublist[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return sublist.size
    }
    private fun convetMilli(milli:Long):String{
        var second = (milli / 1000) % 60;
        var minute = (milli / (1000 * 60)) % 60;
        var hour = (milli / (1000 * 60 * 60)) % 24;

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
    private class ViewHolder(view: View){
        var time: TextView =view.findViewById(R.id.timeview)
        var text: TextView =view.findViewById(R.id.speech)
    }
}