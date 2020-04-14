package com.mlprogramming.anothertodolist.auth

interface AuthInterface {
    fun getUserId(): String?
    fun getUserName(): String?
    fun getUserMail(): String?
    fun logoutUser()
    fun initUserData()
    fun isUserLoggedin(): Boolean
}