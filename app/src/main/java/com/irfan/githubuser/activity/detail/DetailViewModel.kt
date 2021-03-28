package com.irfan.githubuser.activity.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.irfan.githubuser.api.ApiInterface
import com.irfan.githubuser.api.RetrofitClient
import com.irfan.githubuser.db.UserDao
import com.irfan.githubuser.model.DetailUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    private val viewModelJob = SupervisorJob()
    private val _user = MutableLiveData<DetailUser>()
    private val _isSuccess = MutableLiveData<Int>()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val user : LiveData<DetailUser>
        get() = _user

    val isSuccess : LiveData<Int>
        get() = _isSuccess

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        _isSuccess.value = -1
    }


    fun getDetailUser(username: String): LiveData<DetailUser> {
        _isSuccess.value = -1
        val api = RetrofitClient.getRetrofitInstance().create(ApiInterface::class.java)
        uiScope.launch {
            try {
                val response = api.getDetailUser(username = username)
                if (response.isSuccessful) {
                    _isSuccess.value = 1
                    val data = response.body()
                    Log.d("user", data.toString())
                    _user.postValue(data)
                } else {
                    _isSuccess.value = 0
                }
            } catch (e: Throwable) {
                _isSuccess.value = 0
                Log.e("Throwable", e.toString())
            }
        }
        return user
    }
}