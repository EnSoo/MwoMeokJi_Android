package com.mrhiles.mwomeokji_android.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.mrhiles.mwomeokji_android.R
import com.mrhiles.mwomeokji_android.databinding.ActivitySignBinding

class SignActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySignBinding.inflate(layoutInflater)}

    var nickname=""
    var chechNickname = false // 닉네임 변경시 무조건 중복체크 하게끔
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.setOnClickListener{finish()}
        binding.btnCheckNickname.setOnClickListener{checkNickname()}
        binding.btnSignupSave.setOnClickListener{signup()}

        binding.inputNickname.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (chechNickname && nickname != s.toString()) chechNickname = false
                // 중복확인 누르고 닉네임 변경하고 중복확인을 무조건 다시 누르고 회원가입 버튼이 눌리게끔
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun checkNickname(){

        nickname = binding.inputNickname.editText!!.text.toString()

    }

    private fun signup(){

        var email = binding.inputEmail.editText!!.text.toString()
        nickname = binding.inputNickname.editText!!.text.toString()
        var password = binding.inputPassword.editText!!.text.toString()
        var passwordConfirm = binding.inputPasswordCon.editText!!.text.toString()

    }
}