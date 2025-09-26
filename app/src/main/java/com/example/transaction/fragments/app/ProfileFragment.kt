package com.example.transaction.ui.theme

import ProfileApi
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.transaction.R
import com.example.transaction.databinding.FragmentProfileBinding
import com.example.transaction.retrofit.JwtWrapper
import com.example.transaction.retrofit.clearJwt
import com.example.transaction.retrofit.loadJwt
import com.example.transaction.retrofit.saveJwt
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        // Spinner
        val genderAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Мужской", "Женский")
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = genderAdapter


        val jwtWrapper: JwtWrapper? = loadJwt(requireContext())
        val token = jwtWrapper?.jwtToken?.accessToken?.accessToken


        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }


        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                token?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }
                chain.proceed(requestBuilder.build())
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val profileApi = retrofit.create(ProfileApi::class.java)


        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val profile = profileApi.getProfile()
                binding.username.setText(profile.username)
                binding.firstName.setText(profile.firstname)
                binding.lastName.setText(profile.lastname)
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Ошибка получения профиля", e)
            }
        }
        val navController = requireActivity()
            .supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment)
            ?.findNavController()

        binding.imageView.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Выход")
                .setMessage("Вы действительно хотите выйти?")
                .setPositiveButton("Да") { _, _ ->
                    clearJwt(requireContext())

                    val bottomNav = requireActivity().findViewById<com.google.android.material.bottomnavigation.
                    BottomNavigationView?>(R.id.bottom_navigation)
                    bottomNav?.visibility = View.GONE

                    val navController = (requireActivity()
                        .supportFragmentManager
                        .findFragmentById(R.id.nav_host_fragment) as? androidx.navigation.
                        fragment.NavHostFragment)
                        ?.navController

                    if (navController != null) {
                        navController.navigate(
                            R.id.loginFragment,
                            null,
                            androidx.navigation.NavOptions.Builder()
                                .setPopUpTo(R.id.loginFragment, true)
                                .build()
                        )
                    } else {
                        val intent = requireActivity().intent
                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        requireActivity().finish()
                        startActivity(intent)
                    }
                }
                .setNegativeButton("Отмена", null)
                .show()
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
