package com.example.transaction

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.transaction.databinding.ActivityMainBinding

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

