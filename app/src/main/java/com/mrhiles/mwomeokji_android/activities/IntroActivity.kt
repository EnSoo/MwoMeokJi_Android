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
    private val binding by lazy { ActivityIntroBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Glide.with(this).load(R.drawable.logo).into(binding.iv)

        val preferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val email = preferences.getString("email", "")
        if (email == null || email.equals("")) {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }, 2000)
        } else {
            G.apply {
                userAccount= UserAccount("","","")
                userAccount?.nickname= preferences.getString("nickname","")?: ""
                userAccount?.email= preferences.getString("email","")?: ""
                userAccount?.imgfile = preferences.getString("imgfile","")?: ""


            }

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
            finish()}, 3000)
        }
    }
}