package com.mrhiles.mwomeokji_android.fragments

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.mrhiles.mwomeokji_android.G
import com.mrhiles.mwomeokji_android.R
import com.mrhiles.mwomeokji_android.activities.ChangeProfileActivity
import com.mrhiles.mwomeokji_android.activities.LoginActivity
import com.mrhiles.mwomeokji_android.activities.MainActivity
import com.mrhiles.mwomeokji_android.activities.PersonRuleActivity
import com.mrhiles.mwomeokji_android.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {
    private val binding by lazy { FragmentMyPageBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogout.paintFlags = Paint.UNDERLINE_TEXT_FLAG // 밑줄
        binding.btnLogout.setOnClickListener{clickLogout()}
        binding.userDelete.setOnClickListener{userDelete()}
        binding.changeProfile.setOnClickListener { startActivity(Intent(requireContext(),ChangeProfileActivity::class.java)) }
        binding.personRule.setOnClickListener{startActivity(Intent(requireContext(),PersonRuleActivity::class.java))}

        load()
        binding.mypageUserNickname.text= G.user_nickname
    }

    override fun onResume() {
        super.onResume()
        load()
    }
    private fun load(){

    }

    private  fun clickLogout(){
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("로그아웃 하시겠습니까?")
        dialog.setPositiveButton("네", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val preferences = (activity as MainActivity).getSharedPreferences("UserData",AppCompatActivity.MODE_PRIVATE)
                val editor = preferences.edit()
                editor.clear()
                editor.apply()

                startActivity(Intent(requireContext(),LoginActivity::class.java))
                (activity as MainActivity).finish()
            }

        })//setPositive

        dialog.setNegativeButton("아니오", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog?.dismiss()
            }

        })//setNagative
        dialog.create().show()

    }

    lateinit var alertDialog: AlertDialog
    private fun userDelete(){
        val dialogV = layoutInflater.inflate(R.layout.dialog_user_delete, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogV)
        alertDialog = builder.create()
        val inputPassword = dialogV.findViewById<TextInputLayout>(R.id.input_password_delete1)

        if (G.user_providerId == null || G.user_providerId == "") inputPassword.visibility = View.VISIBLE
        else inputPassword.visibility = View.INVISIBLE


    }

}