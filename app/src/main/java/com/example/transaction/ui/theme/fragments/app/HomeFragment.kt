package com.example.transaction.ui.theme;

import LoginApi
import RefreshRequest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.transaction.R
import com.example.transaction.databinding.FragmentHomeBinding
import com.example.transaction.retrofit.JwtWrapper
import com.example.transaction.retrofit.loadJwt
import com.example.transaction.retrofit.saveJwt
import java.time.Instant

public class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private lateinit var loginApi:
            LoginApi

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
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


