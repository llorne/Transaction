package com.example.transaction.ui

import LoginApi
import LoginRequest
import RefreshRequest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.transaction.R
import com.example.transaction.databinding.FragmentLoginBinding
import com.example.transaction.retrofit.*
import com.example.transaction.ui.theme.HomeFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant


class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    private lateinit var loginApi: LoginApi
    interface OnFragmentActionListener {
        fun onChangeAttribute(newValue: String)
    }
    private var actionListener: OnFragmentActionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Ensure the hosting Activity implements the interface
        actionListener = context as? OnFragmentActionListener
        if (actionListener == null) {
            throw ClassCastException("$context must implement OnFragmentActionListener")
        }
    }
    private fun updateParentAttribute() {
        actionListener?.onChangeAttribute("New Value")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)


        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        loginApi = retrofit.create(LoginApi::class.java)

        // 1) При старте проверяем токены
        viewLifecycleOwner.lifecycleScope.launch {
            val ctx = requireContext() // это main-thread safe
            val ok = withContext(Dispatchers.IO) { checkTokens(ctx) }
            if (ok) {
                // accessToken действителен либо был успешно обновлён
                Toast.makeText(ctx, "Успешный вход (токен валиден)", Toast.LENGTH_SHORT).show()
                Log.i("AuthCheck", "Token valid — вход выполнен автоматически")
                updateParentAttribute()
                val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
                ft.replace(R.id.nav_host_fragment, HomeFragment(), "No")
                ft.commit()
                return@launch
            } else {
                Log.i("AuthCheck", "Нет валидных токенов — показываем форму логина")
            }
        }

        //  2) Нажатие кнопки Login(Войти)



        //  2) Нажатие кнопки Login(Войти)
        binding.loginButton.setOnClickListener {
            // Для дорогого Султана Тимура - это чтобы не авторизовываться ибо я не умею)
            //startActivity(Intent(requireContext(), HomeActivity::class.java))
            updateParentAttribute()
//            val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
//            ft.replace(R.id.nav_host_fragment, HomeFragment(), "No")
//            ft.commit()

            viewLifecycleOwner.lifecycleScope.launch {
                val ctx = requireContext()
                val username = binding.username.text.toString().trim()
                val password = binding.password.text.toString()

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(ctx, "Введите логин и пароль", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    try {
                        val responseToken = loginApi.authLogin(
                            LoginRequest(username = username, password = password)
                        )

                        val jwtWrapper = JwtWrapper(
                            jwtToken = JwtWrapper.JwtToken(
                                accessToken = responseToken.accessToken,
                                refreshToken = responseToken.refreshToken
                            )
                        )
                        saveJwt(ctx, jwtWrapper)

                        Log.i("LoginResponse", "accessToken = ${responseToken.accessToken.accessToken}")
                        Log.i("LoginResponse", "accessExpires = ${responseToken.accessToken.expiresAt}")
                        Log.i("LoginResponse", "refreshToken = ${responseToken.refreshToken.refreshToken}")
                        Log.i("LoginResponse", "refreshExpires = ${responseToken.refreshToken.expiresAt}")

                        withContext(Dispatchers.Main) {
                            Toast.makeText(ctx, "Успешный вход 🎉", Toast.LENGTH_SHORT).show()
                            updateParentAttribute()
                            val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
                            ft.replace(R.id.nav_host_fragment, HomeFragment(), "No")
                            ft.commit()
                        }
                    } catch (e: HttpException) {
                        withContext(Dispatchers.Main) {
                            when (e.code()) {
                                401 -> Toast.makeText(ctx, "Неверные учетные данные", Toast.LENGTH_LONG).show()
                                409 -> Toast.makeText(ctx, "Пользователь уже существует", Toast.LENGTH_LONG).show()
                                else -> Toast.makeText(ctx, "Ошибка входа: ${e.code()}", Toast.LENGTH_LONG).show()
                            }
                        }
                        Log.e("LoginError", "HttpException", e)
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(ctx, "Ошибка: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                        Log.e("LoginError", "Exception", e)
                    }
                }
            }
        }



        // 3) Переход на регистрацию
        binding.toRegisterText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Проверяет токены:
     * - если accessToken ещё жив — возвращает true
     * - если accessToken просрочен и refreshToken корректен — пытается обновить через API и сохранить новые токены, возвращает true (если успешно)
     * - иначе возвращает false
     *
     * Важно: поле expiresAt в accessToken/refreshToken должно быть String в формате ISO-8601,
     * чтобы Instant.parse(...) работал.
     */
    private suspend fun checkTokens(context: Context): Boolean {
        val stored = loadJwt(context) ?: return false

        return try {
            val accessExpires = Instant.parse(stored.jwtToken.accessToken.expiresAt)
            val now = Instant.now()

            if (now.isBefore(accessExpires)) {
                Log.i("AuthCheck", "accessToken ещё действителен (until $accessExpires)")
                true
            } else {
                Log.i("AuthCheck", "accessToken просрочен — пробуем refresh")
                try {
                    val refreshValue = stored.jwtToken.refreshToken.refreshToken
                    val newToken = loginApi.refresh(RefreshRequest(refreshToken = refreshValue))

                    val newWrapper = JwtWrapper(
                        jwtToken = JwtWrapper.JwtToken(
                            accessToken = newToken.accessToken,
                            refreshToken = newToken.refreshToken
                        )
                    )
                    saveJwt(context, newWrapper)
                    Log.i("AuthCheck", "refresh успешен — новые токены сохранены")
                    true
                } catch (e: Exception) {
                    Log.w("AuthCheck", "refresh failed", e)
                    false
                }
            }
        } catch (e: Exception) {
            Log.e("AuthCheck", "Ошибка при разборе expiresAt или другом", e)
            false
        }
    }
}
