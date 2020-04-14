package com.mlprogramming.anothertodolist.user

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
import com.mlprogramming.anothertodolist.storage.UserStorage
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserManager @Inject constructor(
    private var auth: AuthInterface,
    private val userStorage: UserStorage,
    private val userComponentFactory: UserComponent.Factory
) {
    companion object {
        const val RC_SIGN_IN = 9001
    }

    lateinit var gso: GoogleSignInOptions
    private var googleApiClient: GoogleApiClient? = null

    var userComponent: UserComponent? = null

    fun getUserName(): String? {
        return auth.getUserMail().let {
            it?.split("@")!![0]
        }
    }

    fun getUserId(): String? {
        return auth.getUserId()
    }

    fun getUserMail(): String? {
        return auth.getUserMail()
    }

    fun isUserLoggedIn() = userComponent != null || userStorage.getUserId() != null

    fun logout() {
        userStorage.clearUser()
        auth.logoutUser()
        Auth.GoogleSignInApi.signOut(googleApiClient);
        userComponent = null
    }

    fun login(appCompatActivity: AppCompatActivity) {
        if (googleApiClient == null) {
            gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(appCompatActivity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

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

    fun loginUserLoggedIn() {
        if (userComponent == null) {
            userComponent = userComponentFactory.create()
        }
    }

    fun userJustLoggedIn() {
        auth.initUserData()
        getUserId()?.let { userStorage.setUserId(it) }
        getUserMail()?.let { userStorage.setUserMail(it) }

        userComponent = userComponentFactory.create()
    }
}