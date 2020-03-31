package com.mlprogramming.anothertodolist.user

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.mlprogramming.anothertodolist.R
import com.mlprogramming.anothertodolist.auth.AuthInterface
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserManager @Inject constructor(
    private val auth: AuthInterface,

    private val userComponentFactory: UserComponent.Factory
) {
    companion object {
        const val RC_SIGN_IN = 9001
    }

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(Resources.getSystem().getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    private var googleApiClient: GoogleApiClient? = null

    var userComponent: UserComponent? = null
        private set

    val username: String
        get() = auth.getLoggedUser()

    fun isUserLoggedIn() = userComponent != null

    fun logout() {
        auth.logoutUser()
        Auth.GoogleSignInApi.signOut(googleApiClient);
        userComponent = null
    }

    fun login(appCompatActivity: AppCompatActivity) {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(appCompatActivity)
                .enableAutoManage(
                    appCompatActivity,
                    appCompatActivity as (GoogleApiClient.OnConnectionFailedListener)
                )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        }
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(appCompatActivity, signInIntent, RC_SIGN_IN, null)
    }

    fun signInWithCredential(
        credential: AuthCredential,
        onCompleteListener: OnCompleteListener<AuthResult>
    ) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(onCompleteListener)
    }

    fun userJustLoggedIn() {
        userComponent = userComponentFactory.create()
    }
}