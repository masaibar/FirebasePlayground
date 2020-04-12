package com.masaibar.firebaseplayground.storage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.masaibar.firebaseplayground.R
import com.masaibar.firebaseplayground.databinding.ActivityStorageBinding
import java.io.File

class StorageActivity : AppCompatActivity(R.layout.activity_storage) {
    companion object {
        private const val REQUEST_CODE_CHOOSER = 1234
        fun createIntent(context: Context) =
            Intent(context, StorageActivity::class.java)
    }

    private val viewModel: StorageViewModel by viewModels()
    private lateinit var binding: ActivityStorageBinding

    private val storage: FirebaseStorage = FirebaseStorage.getInstance() // TODO 後で移す

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_storage)
        binding.buttonUpload.setOnClickListener {
            chooseFile()
        }
    }

    private fun chooseFile() {


        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf";
        }
        startActivityForResult(intent, REQUEST_CODE_CHOOSER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_CODE_CHOOSER ||
            resultCode != Activity.RESULT_OK
        ) {
            return
        }

        val uri = data?.data ?: return

        Log.d("StorageActivity", "uri : $uri")
        upload(uri)
    }

    private fun upload(uri: Uri) {
        val file = Uri.fromFile(File(uri.toString()))
        Log.d("StorageActivity", "filePath: ${file.path}, lastPath: ${file.lastPathSegment}")

        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        val storageRef = storage.reference
        val inputRef = storageRef.child("users/$uid/${file.lastPathSegment}")

        inputRef.putFile(uri).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("StorageActivity", "result: ${task.result}")
            } else {
                Log.d("StorageActivity", "putFile failed: ${task.exception}")
            }
        }
    }
}
