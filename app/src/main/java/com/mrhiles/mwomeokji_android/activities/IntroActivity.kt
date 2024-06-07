package com.mrhiles.mwomeokji_android.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.mrhiles.mwomeokji_android.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private val binding by lazy { ActivityIntroBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
        },3000)
    }
}