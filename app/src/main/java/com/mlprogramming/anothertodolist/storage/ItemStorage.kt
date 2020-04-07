package com.mlprogramming.anothertodolist.storage

interface ItemStorage {
    fun getItem(taskId: String)
    fun getItems(uid: String)
}