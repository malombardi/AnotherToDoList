package com.mlprogramming.anothertodolist.storage

interface UserStorage {
    fun getUserId(): String?
    fun getUserName(): String?
    fun getUserMail(): String?
    fun setUserId(uid: String)
    fun setUserName(userName: String)
    fun setUserMail(userMail: String)
    fun clearUser()
}