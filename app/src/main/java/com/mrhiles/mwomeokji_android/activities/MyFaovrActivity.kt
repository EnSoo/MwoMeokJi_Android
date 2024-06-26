package com.mrhiles.mwomeokji_android.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.mrhiles.mwomeokji_android.R
import com.mrhiles.mwomeokji_android.databinding.ActivityMyFaovrBinding
import org.json.JSONObject

class MyFaovrActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMyFaovrBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.modifyBtn.setOnClickListener { saveSelections()
        finish()}
        binding.resetBtn.setOnClickListener {
            resetSelections()
            saveSelections()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("action", "setLocalStorage")
            startActivity(intent)
        }

        applySelectorToViews()
        resetSelections()  // 초기값 모든 값 false 적용

        val preferences = getSharedPreferences("userPreferences", MODE_PRIVATE)
        val userSelectData = preferences.getString("userSelectData", "")

        // JSON 문자열을 JSONObject로 변환
        if (!userSelectData.isNullOrEmpty()) {
            val jsonObject = JSONObject(userSelectData)

            // jsonObject를 사용하여 초기 상태 설정
            initializeSelections(jsonObject)

//        val preferences = getSharedPreferences("userPreferences", MODE_PRIVATE)
//        val userSelectData = preferences.getString("userSelectData", "")
//        val jsonObject = JSONObject(userSelectData)
//        jsonObject.getString("key값")
        }
    }
        private fun initializeSelections(jsonObject: JSONObject) {
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

            // JSON 객체에서 값을 가져와서 버튼 상태 설정
            radioButtons.forEach { radioButton ->
                val key = resources.getResourceEntryName(radioButton.id)
                radioButton.isChecked = jsonObject.optString(key) == "true"
            }

            checkBoxes.forEach { checkBox ->
                val key = resources.getResourceEntryName(checkBox.id)
                checkBox.isChecked = jsonObject.optString(key) == "true"
            }
        }

    private fun saveSelections() {
        val selections = mutableMapOf<String, String >()

        // 저장할 항목들
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

        radioButtons.forEach { radioButton ->
            selections[resources.getResourceEntryName(radioButton.id)] = radioButton.isChecked.toString()
        }

        checkBoxes.forEach { checkBox ->
            selections[resources.getResourceEntryName(checkBox.id)] = checkBox.isChecked.toString()
        }

        // JSON 변환
        val jsonString = Gson().toJson(selections)

        // SharedPreferences에 저장
        val preferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("userSelectData", jsonString)
        editor.apply()
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

        radioButtons.forEach { radioButton ->
            radioButton.setBackgroundResource(R.drawable.choice)
        }

        checkBoxes.forEach { checkBox ->
            checkBox.setBackgroundResource(R.drawable.choice)
        }
    }

    private fun resetSelections() {
        binding.raNoeat1.isChecked = false
        binding.raNoeat2.isChecked = false
        binding.raSo.isChecked = true
        binding.raGood.isChecked = false
        binding.raGood2.isChecked = false
        binding.reMe.isChecked = false
        binding.raVe.isChecked = false
        binding.raNop.isChecked = true
        binding.rowkal.isChecked = false
        binding.sokal.isChecked = false
        binding.gokal.isChecked = false
        binding.m15.isChecked = true
        binding.m15M30.isChecked = false
        binding.m30M60.isChecked = false
        binding.m60M120.isChecked = false
        binding.m120.isChecked = false
        binding.ko.isChecked = true
        binding.ch.isChecked = false
        binding.ja.isChecked = false
        binding.kita.isChecked = false
        binding.ChMain.isChecked = true
        binding.side.isChecked = false
        binding.dessert.isChecked = false
        binding.soup.isChecked = false
        binding.sauce.isChecked = false
    }
}
