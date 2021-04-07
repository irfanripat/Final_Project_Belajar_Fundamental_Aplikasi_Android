package com.irfan.consumerapp.activity.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.irfan.consumerapp.`package`.DatabaseContract
import com.irfan.consumerapp.model.DetailUser
import com.irfan.consumerapp.util.MappingHelper
import kotlinx.coroutines.*

class MainViewModel : ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val _listUser = MutableLiveData<ArrayList<DetailUser>>()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val listUser : LiveData<ArrayList<DetailUser>>
        get() = _listUser


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun getDataFromContentProvider(context: Context) : LiveData<ArrayList<DetailUser>>{
        uiScope.launch {
            val deferredUser =  async(Dispatchers.IO) {
                val cursor = context.contentResolver?.query(DatabaseContract.UserColumns.CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val user = deferredUser.await()
            _listUser.value = user
        }
        return listUser
    }
}