package com.mlprogramming.anothertodolist.auth

interface AuthInterface {
    fun getLoggedUser(): String
    fun logoutUser()
}