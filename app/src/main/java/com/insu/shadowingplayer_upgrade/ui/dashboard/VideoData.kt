package com.insu.shadowingplayer_upgrade.ui.dashboard

import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore

class VideoData(
    var title:String,
    var img: Bitmap,
    var uri: Uri,
    var foldername:String
){

}