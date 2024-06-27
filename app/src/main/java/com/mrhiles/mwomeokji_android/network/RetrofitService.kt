package com.mrhiles.mwomeokji_android.network

import com.mrhiles.mwomeokji_android.G
import com.mrhiles.mwomeokji_android.UserLoginData
import com.mrhiles.mwomeokji_android.UserLoginResponse
import com.mrhiles.mwomeokji_android.UserSignupData
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query

interface RetrofitService {

    // 1. POST 방식으로 사용자의 정보 서버에 전달
    //@Body로 보낸 json문자열을 $_POST라는 배열에 자동 저장되지 않음. Ex68번. 04Retrofit/bbb.php 참고
    @POST("/backend/signup.php")
    fun userDataToServer(@Body userData: UserSignupData): Call<String>

    @GET("/backend/userTest.php")
    fun userCheckNickname(@Query("nickname") ChecknickName: String): Call<String>


    @POST("/backend/login.php")
    fun userLoginToServer(@Body userData: UserLoginData): Call<UserLoginResponse>

    @POST("/backend/delete.php")
    fun userDelete(@Body userData: UserLoginData): Call<String>

    // 유저 정보 변경
    @Multipart
    @POST("/backend/userChange.php")
    fun userChangeProfile(@PartMap dataPart: Map<String, String>, @Part filePart: MultipartBody.Part? ) : Call<UserLoginResponse>


    @GET("/backend/userTest.php")
    fun userCheckEmail(@Query("email") CheckEmail: String): Call<String>




}