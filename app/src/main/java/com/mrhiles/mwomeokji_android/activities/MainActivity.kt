package com.mrhiles.mwomeokji_android.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.mrhiles.mwomeokji_android.G
import com.mrhiles.mwomeokji_android.databinding.ActivityMainBinding
import com.mrhiles.mwomeokji_android.R
import com.mrhiles.mwomeokji_android.fragments.MyPageFragment
import java.net.URI

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    // [ Google Fused Location API 사용 : play-services-location ]
    val locationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            this
        )
    }

    // 현재 내 위치 정보 객체(위도, 경도)
    private var myLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.statusBarColor)

        // [바텀네비 별로 프래그먼트 보이도록 설정]
        supportFragmentManager.beginTransaction().add((R.id.container_fragment), MyPageFragment())
            .commit()
        binding.bnvView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    // 'Home' 메뉴 선택 시 containerFragment를 숨김
                    binding.containerFragment.visibility = View.GONE
                    binding.wv.visibility = View.VISIBLE
                    // 라우팅 /home 이동
                    binding.wv.loadUrl("javascript:bnvRoute('/')")
                }

                R.id.menu_map -> {
                    // 'map' 메뉴 선택 시 containerFragment를 숨김
                    binding.containerFragment.visibility = View.GONE
                    binding.wv.visibility = View.VISIBLE
                    // 라우팅 /map 이동
                    binding.wv.loadUrl("javascript:bnvRoute('/map')")
                    getLocation()
                }

                R.id.menu_recipe -> {
                    // '레시피' 메뉴 선택 시 containerFragment를 숨기기
                    binding.containerFragment.visibility = View.GONE
                    binding.wv.visibility = View.VISIBLE
                    // 라우팅 /레시피 이동
                    binding.wv.loadUrl("javascript:bnvRoute('/recipe')")
                }

                R.id.menu_mypage -> {
                    // 'My Page' 메뉴 선택 시 containerFragment를 표시
                    binding.containerFragment.visibility = View.VISIBLE
                    binding.wv.visibility = View.GONE
                }
            }
            true
        }

        //웹뷰 설정
        binding.wv.settings.javaScriptEnabled = true
        binding.wv.settings.allowFileAccess = true  // file:// 에서도 ajax 기술 사용 허용
        binding.wv.settings.builtInZoomControls = true
        binding.wv.settings.displayZoomControls = false
        binding.wv.settings.domStorageEnabled = true

        binding.wv.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // 웹 페이지가 완전히 로드된 후에 자바스크립트 함수를 호출
                binding.wv.loadUrl("javascript:setUser(${Gson().toJson(G.userAccount)})")
                // 안드로이드 구분자
                binding.wv.loadUrl("javascript:setIsAndroid(true)")
            }
        }
        binding.wv.webChromeClient = MyWebChromeClient(this)
        //웹뷰가 보여줄 웹페이지를 로딩하기
        binding.wv.loadUrl("http://${G.baseUrl}/")

        // 리액트 -> 안드로이드 카카오 url
        binding.wv.addJavascriptInterface(MyWebViewConnector(), "Droid")

    }

    private fun getLocation() {
        val permissionState: Int = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            //퍼미션 요청 다이얼로그 보이고 그 결과를 받아오는 작업을 대신해주는 대행사 이용
            permissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            //위치정보수집이 허가되어 있다면.. 곧바로 위치정보 얻어오는 작업 시작
            requestMyLocation()
        }
    }

    // 퍼미션 요청 및 결과를 받아오는 작업을 대신하는 대행사 등록
    val permissionResultLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) requestMyLocation()
        else Toast.makeText(this, "내 위치정보를 제공하지 않아서 검색기능 사용이 제한됩니다.", Toast.LENGTH_SHORT).show()
    }

    //현재 위치를 얻어오는 작업요청 코드가 있는 메소드
    private fun requestMyLocation() {
        //요청 객체 생성
        val request: LocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000).build()
        //실시간 위치정보 갱신 요청
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationProviderClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )

    }

    //위치정보 갱신 떄마다 발동하는 콜백 객체
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)

            myLocation = p0.lastLocation

            //위치 탐색이 종료되었으니 내 위치 정보 업데이트를 이제 그만
            locationProviderClient.removeLocationUpdates(this) // this는 location callback 객체

            binding.wv.loadUrl("javascript:resLocation(${myLocation?.latitude}, ${myLocation?.longitude})")
        }
    }

    // 2)웹뷰의 javascript와 통신을 담당할 연결자 객체 클래스 정의
    inner class MyWebViewConnector {

        // /map 페이지 : 마커 -> 인포윈도우 -> 클릭시 activity(카카오 장소 url로 웹뷰 오픈)
        @JavascriptInterface
        fun DetailActivity(url: String) {
            val intent = Intent(this@MainActivity, PlaceDetailActivity::class.java)
            intent.putExtra("place_url", url)
            this@MainActivity.startActivity(intent)
        }
        // /recipe_recommender 페이지에서 선호도 조사 완료 후 localStorage 저장하는 JsonString을 Preference에 저장
        @JavascriptInterface
        fun setUserPreferences(jsonString : String) {
            val preferences = getSharedPreferences("userPreferences", MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString("userSelectData", jsonString)
            editor.apply()
        }
    }

    // 3) file 업로드를 위한 객체 클래스 정의
    var mFilePathCallback: ValueCallback<Array<Uri>>? = null

    inner class MyWebChromeClient(val context: Context) : WebChromeClient() {

        //웹뷰의 input type=file 요소를 선택했을 때 반응하기
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
//            Toast.makeText(context, "파일 선택 클릭 이벤트 발생", Toast.LENGTH_SHORT).show()

            // 사진 선택 화면으로 이동하여 선택결과 받기
            val intent =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Intent(MediaStore.ACTION_PICK_IMAGES).putExtra(
                    MediaStore.EXTRA_PICK_IMAGES_MAX,
                    10
                )
                else Intent(Intent.ACTION_OPEN_DOCUMENT).setType("image/*")
                    .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            resultLauncher.launch(intent)

            //이 메소드의 2번째 파라미터 filePathCallback : 파일선택의 결과를 JS쪽으로 돌려주는 콜백객체
            mFilePathCallback = filePathCallback

            return true
            //return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
        }

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_CANCELED) {
                    Toast.makeText(context, "파일 선택을 취소하셨습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    val imgs: MutableList<Uri> = mutableListOf()
                    //  1개를 선택하면 URI 정보를 data로 받아옴
                    // 2개 이상을 선택하면 ClipData로 전달되어 옴
                    if (it.data?.data != null) imgs.add(it.data!!.data!!)
                    else {
                        val cnt = it.data?.clipData?.itemCount!!
                        for (n in 0 until cnt) imgs.add(it.data!!.clipData!!.getItemAt(n).uri)

                        Toast.makeText(context, "선택한 파일 개수 ${imgs.size}", Toast.LENGTH_SHORT).show()

                        // 네이티브앱에서 대신 선택한 파일 uri 정보들을 웹뷰의 JS에 다시 전달..
                        val uris: Array<Uri> = imgs.toTypedArray()
                        mFilePathCallback!!.onReceiveValue(uris)
                    }
                }
            }
    }

    override fun onBackPressed() {
        if(binding.wv.visibility == View.VISIBLE) {
            val url = binding.wv.url ?: ""
            val currentPath = try {
                URI(url).path ?: ""
            } catch (e: Exception) {
                ""
            }
            if (currentPath == "/" || currentPath == "/recipe" || currentPath == "/map") {
                // 웹뷰
            } else {
                // /detail, /recipe/detail, /recipe/modify, /recipe/add, 페이지일경우 리액트의 navigate(-1) 동작 유도
                binding.wv.loadUrl("javascript:backPath()")
            }
        } else {
            super.onBackPressed()
        }
    }

}