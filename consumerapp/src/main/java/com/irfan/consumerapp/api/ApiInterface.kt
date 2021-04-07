package com.irfan.consumerapp.api

import com.irfan.consumerapp.BuildConfig
import com.irfan.consumerapp.model.DetailUser
import com.irfan.consumerapp.model.SearchResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @Headers("Accept:application/json")
    @GET("search/users?")
    suspend fun searchUser(@Header("Authorization")authorization: String? = "token " + BuildConfig.GITHUB_TOKEN, @Query("q") username: String): Response<SearchResponse>

    @Headers("Accept:application/json")
    @GET("users/{username}")
    suspend fun getDetailUser(@Header("Authorization")authorization: String? = "token " + BuildConfig.GITHUB_TOKEN, @Path("username") username: String): Response<DetailUser>

    @Headers("Accept:application/json")
    @GET("users/{username}/followers")
    suspend fun getListFollower(@Header("Authorization")authorization: String? = "token " + BuildConfig.GITHUB_TOKEN, @Path("username") username: String): Response<ArrayList<DetailUser>>

    @Headers("Accept:application/json")
    @GET("users/{username}/following")
    suspend fun getListFollowing(@Header("Authorization")authorization: String? = "token " + BuildConfig.GITHUB_TOKEN, @Path("username") username: String): Response<ArrayList<DetailUser>>
}