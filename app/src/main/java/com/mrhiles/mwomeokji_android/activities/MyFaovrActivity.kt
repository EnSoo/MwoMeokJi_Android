package com.mrhiles.mwomeokji_android.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.mrhiles.mwomeokji_android.G
import com.mrhiles.mwomeokji_android.R
import com.mrhiles.mwomeokji_android.databinding.ActivityMainBinding
import com.mrhiles.mwomeokji_android.databinding.ActivityMyFaovrBinding
import org.json.JSONArray
import org.json.JSONObject


class MyFaovrActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMyFaovrBinding.inflate(layoutInflater) }
    private val radioButtons by lazy { listOf(
        binding.raNoeat1,
        binding.raNoeat2,
        binding.raSo,
        binding.raGood,
        binding.raGood2,
        binding.raMe,
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
    )}

    private val checkBoxes by lazy { listOf(
        binding.ko,
        binding.ch,
        binding.ja,
        binding.kita,
        binding.ChMain,
        binding.side,
        binding.dessert,
        binding.soup,
        binding.sauce
    )}

    private var originJson = JSONObject()
    private var modifyJson = JSONObject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.modifyBtn.setOnClickListener {
            saveSelections()
        }
        binding.resetBtn.setOnClickListener {
            resetSelections()
        }

        // SharedPreferences에서 값 가져오기
        val preferences = getSharedPreferences("userPreferences", MODE_PRIVATE)
        val userSelectData = preferences.getString("userSelectData", "")

        // JSON 문자열을 JSONObject로 변환
        if (!userSelectData.isNullOrEmpty()) {
            originJson = JSONObject(userSelectData)
            // jsonObject를 사용하여 초기 상태 설정
            initializeSelections(originJson)
        }
    }
        private fun initializeSelections(originJson: JSONObject) {
            // 분류
            val categories=originJson.getJSONArray("categories")
            for (i in 0 until categories.length()) {
                categories.getString(i).let{
                    when (it) {
                        "한식" -> checkBoxes.get(0).isChecked = true // binding.ko
                        "중식" -> checkBoxes.get(1).isChecked = true // binding.ch
                        "일식" -> checkBoxes.get(2).isChecked = true // binding.ja
                        "기타" -> checkBoxes.get(3).isChecked = true // binding.kita
                        // 필요한 경우 다른 케이스도 추가
                    }
                }
            }

            // 매운정도
            val spiciness=originJson.getString("spiciness")
            when (spiciness) {
                "안 매움" ->    radioButtons.get(0).isChecked = true       // binding.raNoeat1
                "약간 매움" ->  radioButtons.get(1).isChecked = true       // binding.raNoeat2
                "보통" ->      radioButtons.get(2).isChecked = true       // inding.raSo
                "매움" ->      radioButtons.get(3).isChecked = true       // binding.raGood
                "엄청 매움" ->  radioButtons.get(4).isChecked = true       // binding.raGood2
            }

            // 식습관 유형
            val dietType=originJson.getString("dietType")
            when (dietType) {
                "육식" ->      radioButtons.get(5).isChecked = true      // binding.reMe
                "채식(비건)" -> radioButtons.get(6).isChecked = true      // binding.raVe
                "상관없음" ->   radioButtons.get(7).isChecked = true      // binding.raNop
            }

            // 칼로리
            val calories=originJson.getString("calories")
            when (calories) {
                "low" ->      radioButtons.get(8).isChecked = true       // binding.rowkal
                "medium" ->   radioButtons.get(9).isChecked = true       // binding.sokal
                "high" ->     radioButtons.get(10).isChecked = true      // binding.gokal
            }

            // 조리시간
            val cookingTime=originJson.getString("cookingTime")
            when (cookingTime) {
                "veryShort" -> radioButtons.get(11).isChecked = true      // binding.m15
                "short" ->     radioButtons.get(12).isChecked = true      // binding.m15M30
                "medium" ->    radioButtons.get(13).isChecked = true      // binding.m30M60
                "long" ->      radioButtons.get(14).isChecked = true      // binding.m60M120
                "veryLong" ->  radioButtons.get(15).isChecked = true      // binding.m120
            }

            // 만들고 싶은 음식
            val dishType=originJson.getJSONArray("dishType")
            for (i in 0 until dishType.length()) {
                dishType.getString(i).let{
                    when (it) {
                        "메인요리" ->   checkBoxes.get(4).isChecked = true      // binding.ChMain
                        "반찬" ->      checkBoxes.get(5).isChecked = true      // binding.side
                        "간식" ->      checkBoxes.get(6).isChecked = true      // binding.dessert
                        "국물요리" ->   checkBoxes.get(7).isChecked = true      // binding.soup
                        "소스" ->      checkBoxes.get(8).isChecked = true      // binding.sauce
                    }
                }
            }
        }
    private fun isSpicinessSelected(): Boolean {
        val spicinessButtons = arrayOf(binding.raNoeat1, binding.raNoeat2, binding.raSo, binding.raGood, binding.raGood2)
        return spicinessButtons.any { it.isChecked }
    }
    private fun isDietTypeSelected(): Boolean {
        val dietTypeButtons = arrayOf(binding.raMe, binding.raVe, binding.raNop)
        return dietTypeButtons.any { it.isChecked }
    }
    private fun isCaloriesSelected(): Boolean {
        val caloriesButtons = arrayOf(binding.rowkal, binding.sokal, binding.gokal)
        return caloriesButtons.any { it.isChecked }
    }private fun isCookingTimeSelected(): Boolean {
        val cookingTimeButtons = arrayOf(binding.m15, binding.m15M30, binding.m30M60, binding.m60M120, binding.m120)
        return cookingTimeButtons.any { it.isChecked }
    }
    private fun isDishTypeSelected(): Boolean {
        val dishTypeCheckBoxes = arrayOf(binding.ChMain, binding.side, binding.dessert, binding.soup, binding.sauce)
        return dishTypeCheckBoxes.any { it.isChecked }
    }private fun isCategoriesSelected(): Boolean {
        val categoriesCheckBoxes = arrayOf(binding.ko, binding.ch, binding.ja, binding.kita)
        return categoriesCheckBoxes.any { it.isChecked }
    }private fun isAllSelectionsMade(): Boolean {
        return isSpicinessSelected() && isDietTypeSelected() && isCaloriesSelected() &&
                isCookingTimeSelected() && isDishTypeSelected() && isCategoriesSelected()
    }private fun showIncompleteSelectionDialog() {
        if(!isFinishing && !isDestroyed) {
            AlertDialog.Builder(this)
                .setTitle("선택 오류")
                .setMessage("모든 항목을 선택해 주세요.")
                .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }


    private fun saveSelections() {
        if (!isAllSelectionsMade()) {
            showIncompleteSelectionDialog()
            return
        }
        if (originJson.length() == 0) {
            modifyJson.put("ingredients",JSONArray())
            modifyJson.put("vegan",false)
            modifyJson.put("meat",false)
            modifyJson.put("warm",false)
            modifyJson.put("cold",false)
            modifyJson.put("soup",false)
        } else {
            modifyJson.put("ingredients",originJson.getJSONArray("ingredients"))
            modifyJson.put("vegan",originJson.getBoolean("vegan"))
            modifyJson.put("meat",originJson.getBoolean("meat"))
            modifyJson.put("warm",originJson.getBoolean("warm"))
            modifyJson.put("cold",originJson.getBoolean("cold"))
            modifyJson.put("soup",originJson.getBoolean("soup"))
        }

        // 매운정도
        for (i in 0 until 5) {
            if(radioButtons.get(i).isChecked){
                when(i){
                    0 -> modifyJson.put("spiciness","안 매움")
                    1 -> modifyJson.put("spiciness","약간 매움")
                    2 -> modifyJson.put("spiciness","보통")
                    3 -> modifyJson.put("spiciness","매움")
                    4 -> modifyJson.put("spiciness","엄청 매움")
                }
            }
        }

        // 칼로리
        for (i in 8 until 11) {
            if(radioButtons.get(i).isChecked){
                when(i){
                    8 -> modifyJson.put("calories","low")
                    9 -> modifyJson.put("calories","medium")
                    10 -> modifyJson.put("calories","high")
                }
            }
        }

        // 조리시간
        for (i in 11 until 16) {
            if(radioButtons.get(i).isChecked){
                when(i){
                    11 -> modifyJson.put("cookingTime","veryShort")
                    12 -> modifyJson.put("cookingTime","short")
                    13 -> modifyJson.put("cookingTime","medium")
                    14 -> modifyJson.put("cookingTime","long")
                    15 -> modifyJson.put("cookingTime","veryLong")
                }
            }
        }
        // 분류
        val categories=JSONArray()
        for (i in 0 until 4) {
            if(checkBoxes.get(i).isChecked){
                when(i){
                    0 -> categories.put("한식")
                    1 -> categories.put("중식")
                    2 -> categories.put("일식")
                    3 -> categories.put("기타")
                }
            }
        }
        modifyJson.put("categories",categories)

        // 만들고 싶은 음식
        val dishType=JSONArray()
        for (i in 4 until 9) {
            if(checkBoxes.get(i).isChecked){
                when(i){
                    4 -> dishType.put("메인요리")
                    5 -> dishType.put("반찬")
                    6 -> dishType.put("간식")
                    7 -> dishType.put("국물요리")
                    8 ->dishType.put("소스")
                }
            }
        }
        modifyJson.put("dishType",dishType)
        // 만들고 싶은 음식 끝 //

        // 식습관 유형
        for (i in 5 until 8) {
            if(radioButtons.get(i).isChecked){
                when(i){
                    5 -> modifyJson.put("dietType","육식")
                    6 -> modifyJson.put("dietType","채식(비건)")
                    7 -> modifyJson.put("dietType","상관없음")
                }
            }
        }
        // JSON 변환
        val jsonString = modifyJson.toString()

        // SharedPreferences에 저장
        val preferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("userSelectData", jsonString)
        editor.apply()

        finish()
    }

    private fun resetSelections() {
        checkBoxes.forEach{it.isChecked=false}
        radioButtons.forEach{it.isChecked=false}

        binding.raSo.isChecked = true // 기본값 medium
        binding.sokal.isChecked = true // 기본값 medium
        binding.m30M60.isChecked = true // 기본값 medium

        binding.raVe.isChecked = false
        binding.raMe.isChecked = false
        binding.soup.isChecked = false

        // SharedPreference 삭제
        val preferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.remove("userSelectData")
        editor.apply()
    }
}
