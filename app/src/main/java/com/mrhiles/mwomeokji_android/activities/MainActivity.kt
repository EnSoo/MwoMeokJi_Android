package com.mrhiles.mwomeokji_android.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.gson.Gson
import com.mrhiles.mwomeokji_android.G
import com.mrhiles.mwomeokji_android.databinding.ActivityMainBinding
import com.mrhiles.mwomeokji_android.R
import com.mrhiles.mwomeokji_android.fragments.MyPageFragment

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}

    // [ Google Fused Location API 사용 : play-services-location ]
    val locationProviderClient : FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this)}

    // 현재 내 위치 정보 객체(위도, 경도)
    private var myLocation: Location?=null

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
                    getLocation()
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

        binding.wv.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // 웹 페이지가 완전히 로드된 후에 자바스크립트 함수를 호출
                binding.wv.loadUrl("javascript:setUser(${Gson().toJson(G.userAccount)})")
            }
        }
        binding.wv.webChromeClient= WebChromeClient()

        //웹뷰가 보여줄 웹페이지를 로딩하기
        binding.wv.loadUrl("http://52.79.98.24/")

        // 리액트 -> 안드로이드 카카오 url
        binding.wv.addJavascriptInterface(MyWebViewConnector(),"InfoWindow")

    }

    private fun getLocation() {
        val permissionState:Int=checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if(permissionState== PackageManager.PERMISSION_DENIED) {
            //퍼미션 요청 다이얼로그 보이고 그 결과를 받아오는 작업을 대신해주는 대행사 이용
            permissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            //위치정보수집이 허가되어 있다면.. 곧바로 위치정보 얻어오는 작업 시작
            requestMyLocation()
        }
    }

    // 퍼미션 요청 및 결과를 받아오는 작업을 대신하는 대행사 등록
    val permissionResultLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
        if(it) requestMyLocation()
        else Toast.makeText(this, "내 위치정보를 제공하지 않아서 검색기능 사용이 제한됩니다.", Toast.LENGTH_SHORT).show()
    }

    //현재 위치를 얻어오는 작업요청 코드가 있는 메소드
    private fun requestMyLocation() {
        //요청 객체 생성
        val request: LocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,3000).build()
        //실시간 위치정보 갱신 요청
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        { return }
        locationProviderClient.requestLocationUpdates(request,locationCallback, Looper.getMainLooper())

    }

    //위치정보 갱신 떄마다 발동하는 콜백 객체
    private val locationCallback=object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)

            myLocation=p0.lastLocation

            //위치 탐색이 종료되었으니 내 위치 정보 업데이트를 이제 그만
            locationProviderClient.removeLocationUpdates(this) // this는 location callback 객체

            binding.wv.loadUrl("javascript:resLocation(${myLocation?.latitude}, ${myLocation?.longitude})")
        }
    }

    // 2)웹뷰의 javascript와 통신을 담당할 연결자 객체 클래스 정의
    inner class MyWebViewConnector{

        @JavascriptInterface
        fun DetailActivity(url:String){
            val intent = Intent(this@MainActivity,PlaceDetailActivity::class.java)
            intent.putExtra("place_url",url)
            this@MainActivity.startActivity(intent)
        }

    }
}