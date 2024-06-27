package com.mrhiles.mwomeokji_android.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.mrhiles.mwomeokji_android.G
import com.mrhiles.mwomeokji_android.R
import com.mrhiles.mwomeokji_android.UserAccount
import com.mrhiles.mwomeokji_android.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private val binding by lazy { ActivityIntroBinding.inflate(layoutInflater) }
    private val introDelay: Long = 2000 // 인트로 화면을 보여주는 시간

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Glide.with(this).load(R.drawable.logo).into(binding.iv)

        Handler(Looper.getMainLooper()).postDelayed({
            checkAutoLogin()
        }, introDelay)
    }

    private fun checkAutoLogin() {
        val preferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val isAutoLogin = preferences.getBoolean("autoLogin", false) // 자동 로그인 여부 확인
        val email = preferences.getString("email", "")

        if (!isAutoLogin || email.isNullOrEmpty()) {
            navigateToLogin()
        } else {
            G.userAccount = UserAccount(
                preferences.getString("nickname", "") ?: "",
                preferences.getString("email", "") ?: "",
                preferences.getString("imgfile", "") ?: ""
            )
            navigateToMain()
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}