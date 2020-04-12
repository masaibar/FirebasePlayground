package com.masaibar.firebaseplayground.storage

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class StorageViewModel : ViewModel() {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


}
