package com.mrhiles.mwomeokji_android.activities

import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.mrhiles.mwomeokji_android.G
import com.mrhiles.mwomeokji_android.R
import com.mrhiles.mwomeokji_android.UserLoginResponse
import com.mrhiles.mwomeokji_android.databinding.ActivityChangeProfileBinding
import com.mrhiles.mwomeokji_android.network.RetrofitHelper
import com.mrhiles.mwomeokji_android.network.RetrofitService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class ChangeProfileActivity : AppCompatActivity() {
    private val binding by lazy { ActivityChangeProfileBinding.inflate(layoutInflater) }

    val imgUrl = "http://52.79.98.24/backend/upload/img${G.userAccount?.imgfile}"

    var imgPath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnChangeUserImage.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        loadProfile()

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnChangeProfile.setOnClickListener { clickChange() }
        binding.btnChangeUserImage.setOnClickListener { clickImage() }
        binding.changeUserNickname.text = G.userAccount?.nickname ?: ""
        binding.userEmailProfile.text = "연결된 계정\n${G.userAccount?.email}"

        binding.btnChangeProfile.isEnabled = true
    }

    private fun clickImage() {
        // 앱에서 사진 가져오기
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Intent(MediaStore.ACTION_PICK_IMAGES)
        else Intent(Intent.ACTION_OPEN_DOCUMENT).setType("image/*")
        resultLauncher.launch(intent)
    }

    // 사진 가져올 대행사
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val uri: Uri? = it.data?.data
        uri?.let {
            Glide.with(this).load(it).into(binding.changeUserImage)

            // uri --> 절대경로
            imgPath = getRealPathFromUri(uri)
        }
    }

    // Uri를 전달받아 실제 파일 경로를 리턴해주는 기능 메소드 구현하기
    private fun getRealPathFromUri(uri: Uri): String? {

        // android 10 버전 부터는 Uri를 통해 파일의 실제 경로를 얻을 수 있는 방법이 없어졌음
        // 그래서 uri에 해당하는 파일을 복사하여 임시로 파일을 만들고 그 파일의 경로를 이용하여 서버에 전송

        // Uri[미디어저장소의 DB 주소]파일의 이름을 얻어오기 - DB SELECT 쿼리작업을 해주는 기능을 가진 객체를 이용
        val cursorLoader: CursorLoader = CursorLoader(this, uri, null, null, null, null)
        val cursor: Cursor? = cursorLoader.loadInBackground()
        val fileName: String? = cursor?.run {
            moveToFirst()
            getString(getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
        } // -------------------------------------------------------------------

        // 복사본이 저장될 파일의 경로와 파일명.확장자
        val file: File = File(externalCacheDir, fileName)

        // 이제 진짜 복사 작업 수행
        val inputStream: InputStream = contentResolver.openInputStream(uri) ?: return null
        val outputStream: OutputStream = FileOutputStream(file)

        // 파일복사
        while (true) {
            val buf: ByteArray = ByteArray(1024) // 빈 바이트 배열[길이:1KB]
            val len: Int = inputStream.read(buf) // 스트림을 통해 읽어들인 바이트들을 buf 배열에 넣어줌 -- 읽어드린 바이트 수를 리턴해 줌
            if (len <= 0) break
            outputStream.write(buf, 0, len) // 덮어쓰기가 아님..
            // offset(오프셋-편차) 0을주면 0번부터 1024가 아님.. 0~1023 번 다음은 편차를 주지말고 1024 ~ 로 주라는 의미임
            // 1024길이만큼 가져오는데.. 편차없이 1024 길이만큼 받다가 읽어드린 바이트(len)의 값만큼 쓰라는 의미임..

        }// while

        // 반복문이 끝났으면 복사가 완료된 것임

        inputStream.close()
        outputStream.close()

        return file.absolutePath
    }////////////////////////////////////////////////////////////////////////////

    private fun clickChange() {
        // 기존 내 이메일과 비교하여 프로필 저장
        val password = binding.changePassword.editText?.text.toString()
        val passwordConfirm = binding.changePasswordCon.editText?.text.toString()

        if (saveCheck(password, passwordConfirm)) {
            Glide.with(this).load(R.drawable.loading).into(binding.loading)
            binding.btnChangeProfile.isEnabled = false
            // 먼저 String 데이터들은 Map collection으로 묶어서 전송 : @PartMap
            val dataPart: MutableMap<String, String> = mutableMapOf()
            dataPart["email"] = G.userAccount?.email ?: ""
            dataPart["password"] = password

            // 이미지파일을 MutipartBody.Part 로 포장하여 전송: @Part
            val filePart: MultipartBody.Part? = imgPath?.let { // 널이 아니면...
                val file = File(it) // 생선손질..
                val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file) // 진공팩포장
                MultipartBody.Part.createFormData("img1", file.name, requestBody) // 택배상자 포장.. == 리턴되는 값
            }

            val retrofit = RetrofitHelper.getRetrofitInstance("http://52.79.98.24")
            val retrofitService = retrofit.create(RetrofitService::class.java)
            retrofitService.userChangeProfile(dataPart, filePart).enqueue(object :
                Callback<UserLoginResponse> {
                override fun onResponse(call: Call<UserLoginResponse>, response: Response<UserLoginResponse>) {
                    val userResponse = response.body()

                    G.userAccount?.imgfile = userResponse?.user?.imgfile ?: ""

                    userResponse?.user?.apply {
                        G.userAccount?.nickname = userResponse.user.nickname
                        G.userAccount?.password = userResponse.user.password
                        G.userAccount?.imgfile = userResponse.user.imgfile
                    }

                    AlertDialog.Builder(this@ChangeProfileActivity).setMessage("변경이 완료되었습니다").create().show()
                    saveSharedPreferences()

                    // 이미지 URL 다시 로드
                    Glide.with(this@ChangeProfileActivity).load("http://52.79.98.24/backend/upload/img${G.userAccount?.imgfile ?: ""}").into(binding.changeUserImage)

                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    }, 1500)
                }

                override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                    if ("${t.message}".contains("IllegalStateException")) {
                        AlertDialog.Builder(this@ChangeProfileActivity).setMessage("이미 사용중인 닉네임입니다").create().show()
                    } else {
                        Toast.makeText(this@ChangeProfileActivity, "관리자에게 문의하세요", Toast.LENGTH_SHORT)
                            .show()
                        Log.e("정보변경 오류", "${t.message}")
                    }

                    binding.btnChangeProfile.isEnabled = true
                } // onFailure...
            })
        }//saveCheck

    }

    private fun loadProfile() {

        if (G.userAccount?.imgfile.equals("") || G.userAccount?.imgfile == null) {
            binding.changeUserImage.setImageResource(R.drawable.logo2)
        } else Glide.with(this).load(imgUrl).into(binding.changeUserImage)
        binding.changeUserNickname.setText(G.userAccount?.nickname ?: "")
    }

    // 저장하기하면 앱에 프로필 이미지 저장하기
    fun saveSharedPreferences() {
        // SharedPreference로 저장하기 - "Date.xml"파일에 저장해주는 객체를 소환하기
        val preferences: SharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        // 저장작업 시작! -- 작성자 객체를 리턴해 줌
        val editor: SharedPreferences.Editor = preferences.edit()
        // 작성자를 통해 데이터를 작성
        editor.putString("imgfile", G.userAccount?.imgfile)
        editor.putString("nickname", G.userAccount?.nickname)
        editor.putString("password", G.userAccount?.password)
        editor.putString("email",G.userAccount?.email)
        editor.apply()
    }

    private fun saveCheck(password: String, passwordCon: String): Boolean {
        var boolean = false

        when {
            password != passwordCon -> {
                AlertDialog.Builder(this).setMessage("패스워드가 다릅니다. 다시 확인해주세요").create().show()
                boolean = false
            }

            password.length in 1..3 -> {
                AlertDialog.Builder(this).setMessage("비밀번호가 너무 짧습니다").create().show()
                boolean = false
            }

            else -> boolean = true
        } // when...

        return boolean
    }
}