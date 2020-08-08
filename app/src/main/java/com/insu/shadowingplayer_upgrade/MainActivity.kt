package com.insu.shadowingplayer_upgrade

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.MobileAds
import com.insu.shadowingplayer_upgrade.ui.video.data.VideoData
import com.insu.shadowingplayer_upgrade.ui.audio.AudioData
import com.insu.shadowingplayer_upgrade.ui.audio.AudioService
import com.insu.shadowingplayer_upgrade.viewmodel.ViewModel
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
private const val REQUEST_READ_EXTERNAL_STORAGE=1000
class MainActivity : AppCompatActivity() {
    private val viewModel:ViewModel? by lazy{
        ViewModel.getViewModelInstance()
    }
    companion object{
        var videos=mutableListOf<VideoData>()
        var audios=mutableListOf<AudioData>()
        var mService: AudioService?=null
        var mBound=false
        var mConnection=object: ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                mService=null
                mBound=false
            }
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder=service as AudioService.AudioServiceBinder
                mService=binder.getService()
                mBound=true
            }

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        Log.d(DEBUG_TAG,"onCreate")
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_video,R.id.navigation_audio, R.id.navigation_word))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val intent= Intent(this,AudioService::class.java)
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE)
        MobileAds.initialize(this,"ca-app-pub-3940256099942544~3347511713")

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // 권한이 허용되지 않음 ②
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // 이전에 이미 권한이 거부되었을 때 설명 ③
                alert("동영상 정보를 얻기 위해서는 외부 저장소 권한이 필수로 필요합니다", "권한이 필요한 이유") {
                    yesButton {
                        // 권한 요청
                        ActivityCompat.requestPermissions(this@MainActivity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_READ_EXTERNAL_STORAGE)
                    }
                    noButton {
                        finish()
                    }
                }.show()
            } else {
                // 권한 요청 ④
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_EXTERNAL_STORAGE)
            }
        }else{
            viewModel?.getDataFromRepository(this)
        }
    }

    override fun onBackPressed() {
        val fragments=supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        when (val fragment= fragments?.childFragmentManager?.fragments?.get(0)) {
            null -> {
                super.onBackPressed()
            }
            is IOnBackPressed -> {
                if(!fragment.onBackPressed())
                    super.onBackPressed()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if(mService!=null)
            mBound=true
        Log.d(DEBUG_TAG,"onResume: $mBound")
    }
    override fun onDestroy() {
        super.onDestroy()
        audios.clear()
        videos.clear()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
         when(requestCode){
            REQUEST_READ_EXTERNAL_STORAGE->{
                if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    viewModel?.getDataFromRepository(this)
                }
            }
        }
    }
}
