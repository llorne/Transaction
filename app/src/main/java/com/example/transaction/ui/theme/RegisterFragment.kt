package com.example.transaction.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.transaction.R
import com.example.transaction.databinding.FragmentRegisterBinding
import com.example.transaction.retrofit.RegRequest
import com.example.transaction.retrofit.RegistrationApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // Retrofit внутри фрагмента
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/") // localhost для эмулятора чтобы работал с компа(т.к. серв на компе запускается)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val registrationApi = retrofit.create(RegistrationApi::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)

        binding.registerButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val user = registrationApi.auth(
                        RegRequest(
                            binding.username.text.toString(),
                            binding.firstName.text.toString(),
                            binding.lastName.text.toString(),
                            binding.password.text.toString(),
                            binding.confirmPassword.text.toString()
                        )
                    )


                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Регистрация успешна! Привет, ${user.firstname}",
                            Toast.LENGTH_SHORT
                        ).show()


                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }

                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Ошибка: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        Log.e("registerException", "Ошибка в создании аккаунта", e)
                    }
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
