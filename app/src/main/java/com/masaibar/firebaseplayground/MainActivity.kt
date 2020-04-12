package com.masaibar.firebaseplayground

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.masaibar.firebaseplayground.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    companion object {
        private const val REQUEST_SIGN_IN = 1234
    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        binding.signInButton.setOnClickListener {
            signInWithGoogle()
        }

        viewModel.uiModel.observe(this, Observer { uiModel ->
            binding.uid = uiModel.uid
        })

        viewModel.getCurrentUser()
    }

    private fun signInWithGoogle() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(
            this,
            googleSignInOptions
        )
        startActivityForResult(
            googleSignInClient.signInIntent,
            REQUEST_SIGN_IN
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGN_IN && data != null) {
            GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.getResult(ApiException::class.java)?.let {
                        viewModel.firebaseAuthWithGoogle(it)
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Google sign in failed: cuz: ${task.exception}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
