package com.mrhiles.mwomeokji_android.fragments

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.mrhiles.mwomeokji_android.G
import com.mrhiles.mwomeokji_android.R
import com.mrhiles.mwomeokji_android.UserLoginData
import com.mrhiles.mwomeokji_android.activities.ChangeProfileActivity
import com.mrhiles.mwomeokji_android.activities.LoginActivity
import com.mrhiles.mwomeokji_android.activities.MainActivity
import com.mrhiles.mwomeokji_android.activities.PersonRuleActivity
import com.mrhiles.mwomeokji_android.databinding.FragmentMyPageBinding
import com.mrhiles.mwomeokji_android.network.RetrofitHelper
import com.mrhiles.mwomeokji_android.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageFragment : Fragment() {

    private val binding by lazy { FragmentMyPageBinding.inflate(layoutInflater) }

    //var imgUrl= "http://52.79.98.24/backend/${G.userAccount?.imgfile}"

    companion object {
        private const val REQUEST_CHANGE_PROFILE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogout.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        binding.btnLogout.setOnClickListener { showLogoutDialog() }
        binding.userDelete.setOnClickListener { showDeleteUserDialog() }
        binding.changeProfile.setOnClickListener { navigateToChangeProfile() }
        binding.personRule.setOnClickListener { navigateToPersonRule() }
        binding.mypageUserNickname.text = G.userAccount?.nickname

        updateNickname()
    }

    override fun onResume() {
        super.onResume()
        //imgUrl = "http://52.79.98.24/backend/${G.userAccount?.imgfile}"
        reloadMypage()
    }


    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("로그아웃 하시겠습니까?")
            .setPositiveButton("네") { _, _ -> logout() }
            .setNegativeButton("아니오") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun logout() {
        val preferences = (activity as MainActivity).getSharedPreferences("UserData", AppCompatActivity.MODE_PRIVATE)
        preferences.edit().clear().apply()

        startActivity(Intent(requireContext(), LoginActivity::class.java))
        (activity as MainActivity).finish()
    }

    private lateinit var alertDialog: AlertDialog
    private fun showDeleteUserDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_user_delete, null)
        alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.btn_close).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.btn_user_delete).setOnClickListener {
            val passwordInput = dialogView.findViewById<TextInputLayout>(R.id.input_password_delete1)
            val password = passwordInput.editText!!.text.toString()
            deleteUser(password)
        }
        alertDialog.show()
    }

    private fun deleteUser(password: String) {
        val retrofit = RetrofitHelper.getRetrofitInstance("http://52.79.98.24")
        val retrofitService = retrofit.create(RetrofitService::class.java)
        val email = G.userAccount?.email ?: ""
        val loginData = UserLoginData(email, password)

        retrofitService.userDelete(loginData).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val message = response.body()
                val deleteDialog = AlertDialog.Builder(requireContext()).setMessage(message).create()
                deleteDialog.show()

                if (message == "회원탈퇴 되었습니다.\n이용해주셔서 감사합니다") {
                    Handler(Looper.getMainLooper()).postDelayed({
                        deleteDialog.dismiss()
                        logout()
                    }, 2000)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(requireContext(), "관리자에게 문의하세요", Toast.LENGTH_SHORT).show()
                Log.e("탈퇴오류", t.message ?: "Unknown error")
            }
        })
    }

    private fun updateProfileImage() {
        val imgUrl = "http://52.79.98.24/backend/upload/img${G.userAccount?.imgfile}"
        if (G.userAccount?.imgfile.isNullOrEmpty()) {
            binding.mypageUserImage.setImageResource(R.drawable.logo2)
        } else {
            Glide.with(requireContext()).load(imgUrl).into(binding.mypageUserImage)
        }
    }

    private fun updateNickname() {
        binding.mypageUserNickname.text = G.userAccount?.nickname
    }

    private fun navigateToChangeProfile() {
        startActivityForResult(Intent(requireContext(), ChangeProfileActivity::class.java), REQUEST_CHANGE_PROFILE)
    }

    private fun navigateToPersonRule() {
        startActivity(Intent(requireContext(), PersonRuleActivity::class.java))
    }

    private fun reloadMypage() {
        updateNickname()
        updateProfileImage()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHANGE_PROFILE && resultCode == AppCompatActivity.RESULT_OK) {
            reloadMypage()
        }
    }
}