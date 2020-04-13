package com.insu.shadowingplayer_upgrade.ui.word

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.insu.shadowingplayer_upgrade.IOnBackPressed
import com.insu.shadowingplayer_upgrade.R

private const val TAG="WebView"
class NotificationsFragment : Fragment(),IOnBackPressed{


    lateinit var webView:WebView
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        webView=root.findViewById(R.id.webView)
        val urlEditText:EditText=root.findViewById(R.id.urlEditText)
        webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    urlEditText.setText(url)
                }
            }
        }

        webView.loadUrl("https://en.dict.naver.com/#/main")

        urlEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                webView.loadUrl(urlEditText.text.toString())
                true
            } else {
                false
            }
        }

        return root
    }

    override fun onBackPressed(){
       if(webView.canGoBack()){
           webView.goBack()
       }
    }

}
