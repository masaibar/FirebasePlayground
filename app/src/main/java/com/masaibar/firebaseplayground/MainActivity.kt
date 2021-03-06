package com.masaibar.firebaseplayground

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.masaibar.firebaseplayground.databinding.ActivityMainBinding
import com.masaibar.firebaseplayground.storage.StorageActivity

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                MainViewModel(this@MainActivity.activityResultRegistry) as T
        }
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        binding.signInButton.setOnClickListener {
            viewModel.signInWithGoogle(this)
        }

        binding.buttonStorage.setOnClickListener {
            startActivity(
                StorageActivity.createIntent(this)
            )
        }

        viewModel.uiModel.observe(this, Observer { uiModel ->
            binding.uid = uiModel.uid
        })

        viewModel.loadCurrentUser()
    }
}
