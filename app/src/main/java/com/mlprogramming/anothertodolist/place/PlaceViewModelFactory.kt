package com.mlprogramming.anothertodolist.place

import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mlprogramming.anothertodolist.model.ToDoTask
import com.mlprogramming.anothertodolist.storage.StorageManager
import com.mlprogramming.anothertodolist.user.UserManager

class PlaceViewModelFactory (private val activity: FragmentActivity,
                             private val task: ToDoTask) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaceViewModel::class.java)) {
            return PlaceViewModel(activity,task) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
