package com.example.transaction.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.transaction.R

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val loginButton = view.findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {

        }


        val toRegisterButton = view.findViewById<Button>(R.id.toRegisterText)
        toRegisterButton.setOnClickListener {

            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)


        }
    }
}
