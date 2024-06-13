package com.mrhiles.mwomeokji_android.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mrhiles.mwomeokji_android.G
import com.mrhiles.mwomeokji_android.L
import com.mrhiles.mwomeokji_android.UserLoginData
import com.mrhiles.mwomeokji_android.UserLoginResponse
import com.mrhiles.mwomeokji_android.databinding.ActivityLoginBinding
import com.mrhiles.mwomeokji_android.network.RetrofitHelper
import com.mrhiles.mwomeokji_android.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnSignup.setOnClickListener { startActivity(Intent(this, SignActivity::class.java)) }
        binding.btnLogin.setOnClickListener { clickLogin() }
    }

    private fun clickLogin() {
        val email = binding.loginInputEmail.editText?.text.toString()
        val password = binding.loginInputPassword.editText?.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            AlertDialog.Builder(this).setMessage("이메일과 비밀번호를 입력해주세요").create().show()
            return
        }

        val retrofit = RetrofitHelper.getunsafeRetrofitInstance("https://52.79.98.24")
        val retrofitService = retrofit.create(RetrofitService::class.java)
        val loginData = UserLoginData(email, password)
        retrofitService.userLoginToServer(loginData).enqueue(object : Callback<UserLoginResponse> {
            override fun onResponse(call: Call<UserLoginResponse>, response: Response<UserLoginResponse>) {
                val userResponse = response.body()

                if (userResponse != null && userResponse.user != null) {
                    G.userAccount = userResponse.user
                    saveSharedPreferences()
                    L.login = true
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    AlertDialog.Builder(this@LoginActivity).setMessage("이메일과 비밀번호를 확인하세요").create().show()
                }
            }

            override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "관리자에게 문의하세요", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 자동로그인 체크시 SharedPreference 저장
    private fun saveSharedPreferences() {
        val preferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("nickname", G.userAccount?.nickname)
        editor.putString("email", G.userAccount?.email)
        editor.putString("password", G.userAccount?.password)
        editor.putString("imgfile", G.userAccount?.imgfile)
        editor.apply()
    }
}