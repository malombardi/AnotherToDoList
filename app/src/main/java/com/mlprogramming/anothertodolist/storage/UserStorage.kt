package com.mlprogramming.anothertodolist.storage

interface UserStorage {
    fun getUserId(): String?
    fun getUserMail(): String?
    fun setUserId(uid: String)
    fun setUserMail(userMail: String)
    fun clearUser()
}