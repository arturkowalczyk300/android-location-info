package com.arturkowalczyk300.locationinfo.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.arturkowalczyk300.locationinfo.model.Location
import com.arturkowalczyk300.locationinfo.model.MainRepository

class MainViewModel : ViewModel() {
    private val repository = MainRepository()

    val currentLocation = repository.currentLocation

    fun initLocationProvider(
        context: Context
    ){
        repository.initLocationProvider(context)
    }

    fun startListeningLocationChange() {
        repository.startListeningLocationChange()
    }

    fun stopListeningLocationChange() {
        repository.stopListeningLocationChange()
    }
}