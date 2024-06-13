package com.mrhiles.mwomeokji_android.activities

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mrhiles.mwomeokji_android.R
import com.mrhiles.mwomeokji_android.databinding.ActivityPlaceDetailBinding

class PlaceDetailActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPlaceDetailBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val url: String? = intent.getStringExtra("place_url")

        //웹뷰 설정
        binding.wv.settings.javaScriptEnabled= true
        binding.wv.settings.allowFileAccess= true  // file:// 에서도 ajax 기술 사용 허용
        binding.wv.settings.builtInZoomControls= true
        binding.wv.settings.displayZoomControls= false
        binding.wv.settings.domStorageEnabled= true

        binding.wv.webViewClient= WebViewClient()
        binding.wv.webChromeClient= WebChromeClient()
        binding.wv.loadUrl(url!!)
    }
}