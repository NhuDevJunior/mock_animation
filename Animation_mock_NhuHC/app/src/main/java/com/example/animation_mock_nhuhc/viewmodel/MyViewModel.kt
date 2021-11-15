package com.example.animation_mock_nhuhc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel:ViewModel() {
    private var _posHighLight = MutableLiveData<Int>()
    val observerPosHighLight: LiveData<Int>
        get() = _posHighLight
    fun setPostHighLight(newData:Int){
        _posHighLight.postValue(newData)
    }
}