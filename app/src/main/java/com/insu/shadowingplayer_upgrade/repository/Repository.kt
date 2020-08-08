package com.insu.shadowingplayer_upgrade.repository

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.insu.shadowingplayer_upgrade.DEBUG_TAG
import com.insu.shadowingplayer_upgrade.ui.audio.AudioData
import com.insu.shadowingplayer_upgrade.ui.video.data.VideoData

class Repository {
    val videoList=mutableListOf<VideoData>()
    val audioList=mutableListOf<AudioData>()
    companion object{
        private var instance:Repository?=null
        fun getRepositoryInstance():Repository?{
            return if(instance==null){
                instance= Repository()
                instance
            }else
                instance
        }
    }
    fun initData(context: Context){
        val resolver=context.contentResolver

        getAllAudios(resolver)

        getAllVideos(resolver)
    }
    private fun getAllAudios(resolver:ContentResolver){
        val projection=arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION)
        val sortOrder="${MediaStore.Audio.Media.TITLE} DESC"
        val cursor=resolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder)
        if(cursor!=null){
            while(cursor.moveToNext()){
                var title=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                var id:Long=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                var duration:String=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                var uri = Uri.withAppendedPath(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                var path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                audioList.add(AudioData(title,duration,uri,path))
            }
        }
        cursor?.close()
    }
    private fun getAllVideos(resolver:ContentResolver){
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME
        )
        val cursor=resolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null)
        if(cursor!=null){
            while(cursor.moveToNext()){
                var title:String=cursor.getString(1)
                var id:Long=cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                /*
                api 29부터 resolver로 썸네일 가져오는 것이 좋다고 개발자 문서에 있음
                This class was deprecated in API level 29.
                Callers should migrate to using ContentResolver#loadThumbnail,
                since it offers richer control over requested thumbnail sizes and cancellation behavior.
                */
                var bitmap: Bitmap = MediaStore.Video.Thumbnails.getThumbnail(resolver,
                    id, MediaStore.Video.Thumbnails.MINI_KIND,null)
                bitmap= Bitmap.createScaledBitmap(bitmap,300,200,true)

                var uri = Uri.withAppendedPath(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                var path=getPath(uri,resolver)
                var temp=
                    VideoData(
                        title,
                        bitmap,
                        uri,
                        path,
                        getFolderName(path)
                    )
                videoList.add(temp)
            }
        }
        cursor?.close()
    }
    private fun getPath(uri:Uri,resolver:ContentResolver):String{
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = uri?.let{resolver.query(it, proj, null, null, null)}
        cursor!!.moveToNext()
        return cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
    }
    private fun getFolderName(path:String):String{
        var pos=path.lastIndexOf('/')
        var temp=path.slice(0 until pos)
        pos=temp.lastIndexOf('/')

        var folderName=if(pos==0||pos==-1){
            "."
        }else{
            temp.slice(pos until temp.length)
        }
        return folderName
    }
}