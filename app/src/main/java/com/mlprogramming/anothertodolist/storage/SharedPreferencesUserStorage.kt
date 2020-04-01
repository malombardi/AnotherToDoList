package com.mlprogramming.anothertodolist.storage

import android.content.Context
import javax.inject.Inject

class SharedPreferencesUserStorage @Inject constructor(context: Context) : UserStorage {

    private val sharedPreferences =
        context.getSharedPreferences("AnotherToDoList", Context.MODE_PRIVATE)

    override fun getUserId(): String? {
        return sharedPreferences.getString("Uid", null)
    }

    override fun getUserName(): String? {
        return sharedPreferences.getString("UserName", null)
    }

    override fun getUserMail(): String? {
        return sharedPreferences.getString("UserMail", null)
    }

    override fun setUserId(uid: String) {
        setString("Uid", uid)
    }

    override fun setUserName(userName: String) {
        setString("UserName", userName)
    }

    override fun setUserMail(userMail: String) {
        setString("UserMail", userMail)
    }

    override fun clearUser() {
        with(sharedPreferences.edit()) {
            clear()
            commit()
        }
    }

    private fun setString(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

}