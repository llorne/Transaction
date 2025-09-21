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
import com.example.transaction.retrofit.Token
import com.example.transaction.retrofit.loadJwt
import com.example.transaction.retrofit.saveJwt
import com.example.transaction.retrofit.toJwtWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.HTTP

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val interceptor = HttpLoggingInterceptor()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)

        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/").client(client) // localhost для эмулятора чтобы работал с компа(т.к. серв на компе запускается)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val registrationApi = retrofit.create(RegistrationApi::class.java)

        binding.registerButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Отправка запроса на регистрацию
                    val token = registrationApi.authReg(
                        RegRequest(
                            binding.username.text.toString(),
                            binding.firstName.text.toString(),
                            binding.lastName.text.toString(),
                            binding.password.text.toString(),
                            binding.confirmPassword.text.toString()
                        )
                    )



                    val jwtWrapper = token.toJwtWrapper()


                    // Сохранение в SharedPreferences
                    context?.let { saveJwt(it, jwtWrapper) }

                    // Чтение для проверки
                    context?.let { ctx ->
                        val restored = loadJwt(ctx)
                        Log.i("Полученный accessToken", "${restored?.jwtToken?.accessToken?.accessToken}")
                        Log.i("Полученный refreshToken", "${restored?.jwtToken?.refreshToken?.refreshToken}")
                    }

                    // Навигация на экран логина
                    withContext(Dispatchers.Main) {
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }

                } catch (e: retrofit2.HttpException) {
                    withContext(Dispatchers.Main) {
                        when (e.code()) {
                            409 -> Toast.makeText(requireContext(), "Пользователь уже существует", Toast.LENGTH_LONG).show()
                            401 -> Toast.makeText(requireContext(), "Не авторизован", Toast.LENGTH_LONG).show()
                            else -> Toast.makeText(requireContext(), "Ошибка: ${e.code()}", Toast.LENGTH_LONG).show()
                        }
                        Log.e("registerException", "Ошибка в создании аккаунта", e)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Неизвестная ошибка", Toast.LENGTH_LONG).show()
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
