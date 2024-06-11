package com.mrhiles.mwomeokji_android.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.mrhiles.mwomeokji_android.G
import com.mrhiles.mwomeokji_android.R
import com.mrhiles.mwomeokji_android.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private val binding by lazy { ActivityIntroBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Glide.with(this).load(R.drawable.logo).into(binding.iv)

        val preferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val email = preferences.getString("user_email", "")
        if (email == null || email.equals("")) {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }, 2000)
        } else {
            G.apply {
                user_email = preferences.getString("user_email", "")!!
                user_nickname = preferences.getString("user_nickname", "")!!
                user_imageUrl = preferences.getString("user_imageUrl", "")!!
                loginType = preferences.getString("loginType", "")!!
                user_providerId = ""

            }

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
            }, 3000)
        }
    }
}