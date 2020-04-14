package com.mlprogramming.anothertodolist.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class FirebaseAuthInterface @Inject constructor() : AuthInterface {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var firebaseUser: FirebaseUser

    override fun isUserLoggedin(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun initUserData() {
        firebaseAuth.currentUser?.let {
            firebaseUser = it
        }
    }

    override fun getUserId(): String? {
        initUserData()
        return firebaseUser.uid
    }

    override fun getUserMail(): String? {
        initUserData()
        return firebaseUser.email
    }

    override fun getUserName(): String? {
        initUserData()
        return firebaseUser.displayName
    }

    override fun logoutUser() {
        firebaseAuth.signOut();
    }
}