package com.mlprogramming.anothertodolist.auth

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class FirebaseAuthInterface @Inject constructor(context: Context) : AuthInterface {
    private var firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private var firebaseUser : FirebaseUser? = firebaseAuth.currentUser

    override fun getLoggedUser(): String {
        return firebaseUser?.displayName!!
    }

    override fun logoutUser(){
        firebaseAuth.signOut();
    }
}