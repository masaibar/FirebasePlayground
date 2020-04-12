package com.masaibar.firebaseplayground

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.masaibar.firebaseplayground.ext.combine

class MainViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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

    fun getCurrentUser() {
        currentUserLiveData.postValue(auth.currentUser)
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentUserLiveData.postValue(auth.currentUser)
                }
            }
    }
}
