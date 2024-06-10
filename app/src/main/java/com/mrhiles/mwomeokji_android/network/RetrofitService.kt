package com.mrhiles.mwomeokji_android.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitService {
//
//    //로그인
//    @POST("/sign/login.php")
//    fun login(@Body loginData: LoginData) : Call<LoginResponse> // 4200 회원, 4204 회원 아님,
//    // 4203 이메일 로그인 정보 틀림, 1200 간편 회원 추가 성공, 1201 간편회원 추가 실패
//
//    //회원가입(이메일)
//    @POST("/sign/signup.php")
//    fun singUp(@Body signUpData: SignUpData) : Call<String> // 1200 회원 추가 성공, 1201 회원 추가 실패, 4330 닉네임 또는 이메일 중복
//
//    //회원가입(간편회원)
//    @POST("/sign/easy_signup.php")
//    fun easySignUp(@Body signUpData: EasySignUpData) : Call<String> // 1200 회원 추가 성공, 1201 회원 추가 실패, 4330 닉네임 또는 이메일 중복
//
//    //회원가입 시 닉네임 중복체크
//    @GET("/sign/email_nickname_dupli_check.php")
//    fun dupliCheck(@Query("nickname") nickname:String) : Call<String> // 4320 닉네임 중복, 4300 중복 x

}