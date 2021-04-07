package com.irfan.consumerapp.activity.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.irfan.consumerapp.api.ApiInterface
import com.irfan.consumerapp.api.RetrofitClient
import com.irfan.consumerapp.model.DetailUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val _listUser = MutableLiveData<ArrayList<DetailUser>>()
    private val _isSuccess = MutableLiveData<Int>()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val listUser : LiveData<ArrayList<DetailUser>>
        get() = _listUser

    val isSuccess : LiveData<Int>
        get() = _isSuccess

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        _isSuccess.value = -1
    }

    fun setSearchQuery(query: String) {
        _isSuccess.value = -1
        val api = RetrofitClient.getRetrofitInstance().create(ApiInterface::class.java)
        val listItem = ArrayList<DetailUser>()
        uiScope.launch {
            try {
                val response = api.searchUser(username = query)
                if (response.isSuccessful) {
                    _isSuccess.value = 1
                    for (user in response.body()?.items!!) {
                        listItem.add(user)
                    }
                    Log.d("response", listItem.toString())
                    _listUser.postValue(listItem)
                } else {
                    _isSuccess.value = 0
                }
            } catch (e: Throwable) {
                _isSuccess.value = 0
                Log.e("Throwable", e.toString())
            }
        }
    }

    fun getSearchResult(): LiveData<ArrayList<DetailUser>> {
        return listUser
    }
}