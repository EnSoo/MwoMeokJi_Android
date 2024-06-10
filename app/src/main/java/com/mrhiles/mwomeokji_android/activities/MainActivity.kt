package com.mrhiles.mwomeokji_android.activities

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mrhiles.mwomeokji_android.databinding.ActivityMainBinding
import com.mrhiles.mwomeokji_android.R
import com.mrhiles.mwomeokji_android.fragments.MyPageFragment

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // [바텀네비 별로 프래그먼트 보이도록 설정]
        supportFragmentManager.beginTransaction().add((R.id.container_fragment),MyPageFragment()).commit()
        binding.bnvView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    // 'Home' 메뉴 선택 시 containerFragment를 숨김
                    binding.containerFragment.visibility = View.GONE
                    binding.wv.visibility=View.VISIBLE
                    // 라우팅 /home 이동
                    binding.wv.loadUrl("javascript:bnvRoute('/')")
                }
                R.id.menu_map -> {
                    // 'map' 메뉴 선택 시 containerFragment를 숨김
                    binding.containerFragment.visibility = View.GONE
                    binding.wv.visibility=View.VISIBLE
                    // 라우팅 /map 이동
                    binding.wv.loadUrl("javascript:bnvRoute('/map')")
                }
                R.id.menu_recipe -> {
                    // '레시피' 메뉴 선택 시 containerFragment를 숨기기
                    binding.containerFragment.visibility = View.GONE
                    binding.wv.visibility=View.VISIBLE
                    // 라우팅 /레시피 이동
                    binding.wv.loadUrl("javascript:bnvRoute('/recipe')")
                }
                R.id.menu_mypage -> {
                    // 'My Page' 메뉴 선택 시 containerFragment를 표시
                    binding.containerFragment.visibility = View.VISIBLE
                    binding.wv.visibility=View.GONE
                    Toast.makeText(this, "마이페이지", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        //웹뷰 설정
        binding.wv.settings.javaScriptEnabled= true
        binding.wv.settings.allowFileAccess= true  // file:// 에서도 ajax 기술 사용 허용
        binding.wv.settings.builtInZoomControls= true
        binding.wv.settings.displayZoomControls= false
        binding.wv.settings.domStorageEnabled= true

        binding.wv.webViewClient= WebViewClient()
        binding.wv.webChromeClient= WebChromeClient()

        //웹뷰가 보여줄 웹페이지를 로딩하기
        binding.wv.loadUrl("https://52.79.98.24/")

    }
}