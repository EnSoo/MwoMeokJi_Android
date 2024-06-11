package com.mrhiles.mwomeokji_android.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mrhiles.mwomeokji_android.G
import com.mrhiles.mwomeokji_android.UserLoginData
import com.mrhiles.mwomeokji_android.UserLoginResponse
import com.mrhiles.mwomeokji_android.databinding.ActivityLoginBinding
import com.mrhiles.mwomeokji_android.network.RetrofitHelper
import com.mrhiles.mwomeokji_android.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnSignup.setOnClickListener { startActivity(Intent(this,SignActivity::class.java)) }
        binding.btnLogin.setOnClickListener { clickLogin() }


        // 로그인 페이지 다 구현 되면 삭제 될 부분
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
        },1000)
    }
    private fun clickLogin(){
        var email= binding.loginInputEmail.editText!!.text.toString()
        var password= binding.loginInputPassword.editText!!.text.toString()

        val retrofit= RetrofitHelper.getRetrofitInstance("https://52.79.98.24")
        val retrofitService=retrofit.create(RetrofitService::class.java)
        val loginData= UserLoginData(email, password)
        retrofitService.userLoginToServer(loginData).enqueue(object : Callback<UserLoginResponse> {
            override fun onResponse(
                call: Call<UserLoginResponse>,
                response: Response<UserLoginResponse>
            ) {
               val userResponse= response.body()
                
                G.userAccount?.imgfile= userResponse?.user?.imgfile?:""
                
                
            }

            override fun onFailure(call: Call<UserLoginResponse>, response: Throwable) {
                Toast.makeText(this@LoginActivity, "관리자에게 문의하세요", Toast.LENGTH_SHORT).show()
            }

        })
    }

    //자동로그인 체크시 SharedPreference 저장
    private fun saveSharedPreferences(){
        val preferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("user_email", G.user_email)
        editor.putString("user_nickname",G.user_nickname)
        editor.putString("user_imageUrl",G.user_imageUrl)
        editor.putString("loginType",G.loginType)
        editor.apply()
    }

}