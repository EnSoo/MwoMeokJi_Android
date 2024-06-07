package com.mrhiles.mwomeokji_android.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mrhiles.mwomeokji_android.R
import com.mrhiles.mwomeokji_android.databinding.ActivitySignBinding

class SignActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySignBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}