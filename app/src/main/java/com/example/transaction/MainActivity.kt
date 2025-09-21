package com.example.transaction

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.transaction.databinding.ActivityMainBinding
import com.example.transaction.retrofit.AccountResponseApi 
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}

//        binding.button.setOnClickListener {
//
//            CoroutineScope (Dispatchers.IO).launch {
//                val user = registrationApi.auth(
//                    AuthRequest(
//                        binding.username.text.toString(),
//                        binding.password.text.toString()
//                    )
//                )
//            }

