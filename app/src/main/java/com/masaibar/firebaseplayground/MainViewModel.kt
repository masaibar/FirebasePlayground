package com.masaibar.firebaseplayground

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.masaibar.firebaseplayground.ext.combine

class MainViewModel(
    registry: ActivityResultRegistry
) : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val signInWithGoogleLauncher: ActivityResultLauncher<Intent> = registry.register(
        "key", // TODO ここは適当すぎるのでは
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        Log.d("googleSignIn", activityResult.toString())

        if (activityResult.data == null) {
            return@register
        }

        GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.getResult(ApiException::class.java)?.let {
                        firebaseAuthWithGoogle(it)
                    }
                } else {
                    // TODO call showToast
                }
            }
    }


    data class UiModel(
        val uid: String?
    ) {
        companion object {
            val EMPTY = UiModel(
                uid = null
            )
        }
    }

    private val currentUserLiveData: MutableLiveData<FirebaseUser> =
        MutableLiveData()

    val uiModel = combine(
        initialValue = UiModel.EMPTY,
        liveData1 = currentUserLiveData
    ) { _, currentUser ->
        UiModel(
            uid = currentUser.uid
        )
    }

    fun signInWithGoogle(activity: Activity) {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(
            activity,
            googleSignInOptions
        )
        signInWithGoogleLauncher.launch(googleSignInClient.signInIntent)
    }

    fun getCurrentUser() {
        currentUserLiveData.postValue(auth.currentUser)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentUserLiveData.postValue(auth.currentUser)
                }
            }
    }
}
