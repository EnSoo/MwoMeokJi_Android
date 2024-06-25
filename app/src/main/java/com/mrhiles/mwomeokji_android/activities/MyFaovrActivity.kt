package com.mrhiles.mwomeokji_android.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mrhiles.mwomeokji_android.R
import com.mrhiles.mwomeokji_android.databinding.ActivityMyFaovrBinding

class MyFaovrActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMyFaovrBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        applySelectorToViews()

        preSelectButtons()
    }

    private fun applySelectorToViews() {
        val radioButtons = listOf(
            binding.raNoeat1,
            binding.raNoeat2,
            binding.raSo,
            binding.raGood,
            binding.raGood2,
            binding.reMe,
            binding.raVe,
            binding.raNop,
            binding.rowkal,
            binding.sokal,
            binding.gokal,
            binding.m15,
            binding.m15M30,
            binding.m30M60,
            binding.m60M120,
            binding.m120
        )

        val checkBoxes = listOf(
            binding.ko,
            binding.ch,
            binding.ja,
            binding.kita,
            binding.ChMain,
            binding.side,
            binding.dessert,
            binding.soup,
            binding.sauce
        )

        for (radioButton in radioButtons) {
            radioButton.setBackgroundResource(R.drawable.choice)
        }

        for (checkBox in checkBoxes) {
            checkBox.setBackgroundResource(R.drawable.choice)
        }
    }
    private fun preSelectButtons() {
        binding.raSo.isChecked = true
        binding.ChMain.isChecked = true
        binding.m15.isChecked = true
        binding.ko.isChecked = true
        binding.raNop.isChecked = true
    }
}