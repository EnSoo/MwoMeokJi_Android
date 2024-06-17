package com.mrhiles.mwomeokji_android.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mrhiles.mwomeokji_android.G
import com.mrhiles.mwomeokji_android.UserSignupData
import com.mrhiles.mwomeokji_android.databinding.ActivitySignBinding
import com.mrhiles.mwomeokji_android.network.RetrofitHelper
import com.mrhiles.mwomeokji_android.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySignBinding.inflate(layoutInflater)}

    var nickname:String=""
    var email:String=""
    var password:String=""
    var passwordConfirm:String=""


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

    private fun checkNickname(): Boolean {

        nickname = binding.inputNickname.editText!!.text.toString()

        if (nickname.contains(" ")) {
            AlertDialog.Builder(this).setMessage("띄어쓰기는 사용할 수 없습니다").create().show()
        } else if (nickname.length < 2 || nickname.length >= 9) {
            AlertDialog.Builder(this).setMessage("2~8자 내로 입력하세요").create().show()

        } else {
            val retrofit = RetrofitHelper.getunsafeRetrofitInstance("https://${G.baseUrl}")
            val retrofitService = retrofit.create(RetrofitService::class.java)
            retrofitService.userCheckNickname(nickname).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    var s = response.body().toString()
                    AlertDialog.Builder(this@SignActivity).setMessage(s).create().show()
                    chechNickname = s.trim().toBoolean()
                    if (chechNickname) {
                        AlertDialog.Builder(this@SignActivity).setMessage("사용가능 합니다.").create()
                            .show()
                        binding.inputNickname.editText?.clearFocus()
                        chechNickname = true
                    } else AlertDialog.Builder(this@SignActivity).setMessage("이미 사용중입니다.").create()
                        .show()
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(this@SignActivity, "관리자에게 문의하세요.", Toast.LENGTH_SHORT).show()
                    Log.d("서버오류", "${t.message}")
                }



            })


        }
        return true

    }


    private fun signup() {

        nickname = binding.inputNickname.editText!!.text.toString()
        email = binding.inputEmail.editText!!.text.toString()
        password = binding.inputPassword.editText!!.text.toString()
        passwordConfirm = binding.inputPasswordCon.editText!!.text.toString()

        if (saveCheck(nickname, email, password, passwordConfirm) && checkNickname()) {

            val retrofit = RetrofitHelper.getunsafeRetrofitInstance("https://${G.baseUrl}")
            val retrofitService = retrofit.create(RetrofitService::class.java)

            val userData = UserSignupData(nickname, email, password)
            retrofitService.userDataToServer(userData).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    var s = response.body().toString()
                    Log.d("ddd","${s}")
                    //Toast.makeText(this@SignupActivity, s, Toast.LENGTH_SHORT).show()
                    if (s.trim().equals("회원가입이 완료되었습니다.")) finish()


                    finish()

                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(this@SignActivity, "관리자에게 문의하세요", Toast.LENGTH_SHORT).show()
                    Log.d("서버오류", "${t.message}")
                }

            })//callback


        } else AlertDialog.Builder(this).setMessage("중복확인을 해주세요").create().show()





    }

    private fun saveCheck(nickname:String,email: String,password:String,passwordConfirm:String) : Boolean {

        var boolean = false

        when{


            !email.contains("@") -> {
                AlertDialog.Builder(this).setMessage("@넣어 입력해주세요").create().show()
                boolean = false
            }

            password != passwordConfirm -> {
                AlertDialog.Builder(this).setMessage("패스워드가 다릅니다.다시 확인해주세요").create().show()
                boolean = false
            }

            nickname.length < 2 -> {
                AlertDialog.Builder(this).setMessage("닉네임이 너무 짧습니다").create().show()
                boolean = false
            }

            password.length < 4 -> {
                AlertDialog.Builder(this).setMessage("비밀번호가 너무 짧습니다").create().show()
                boolean = false
            }

            password.contains(" ") || nickname.contains(" ") || email.contains(" ") -> {
                AlertDialog.Builder(this).setMessage("띄어쓰기는 사용할 수 없습니다").create().show()
                boolean = false
            }

            !nickname.isNotEmpty() && !email.isNotEmpty() && !password.isNotEmpty() && !passwordConfirm.isNotEmpty() -> {
                AlertDialog.Builder(this).setMessage("모두 입력해주세요").create().show()
                boolean = false
            }

            else -> boolean = true
        } // when...

        return boolean
    }

}