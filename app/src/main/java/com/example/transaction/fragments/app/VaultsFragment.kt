package com.example.transaction.ui.theme

import LoginApi
import RefreshRequest
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.transaction.R
import com.example.transaction.VaultsAdapter
import com.example.transaction.databinding.FragmentVaultsBinding
import com.example.transaction.models.Vault
import com.example.transaction.retrofit.JwtWrapper
import com.example.transaction.retrofit.loadJwt
import com.example.transaction.retrofit.saveJwt
import java.time.Instant

class VaultsFragment : Fragment(R.layout.fragment_vaults), VaultsAdapter.OnVaultItemClickListener {
    private var _binding: FragmentVaultsBinding? = null

    private val binding get() = _binding!!
    private var currentlySelectedPosition = -1
    private lateinit var adapter: VaultsAdapter
    private val dataSet = mutableListOf<Vault>()

    private lateinit var loginApi: LoginApi

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVaultsBinding.bind(view)
        // Тестовые счета
        var vault = Vault("Счёт 1", "Наличные", 130.0, R.drawable.dollar, true)
        var vault2 = Vault("Счёт 2", "Дебетовая", 10.0, R.drawable.euro, false)
        dataSet.add(vault)
        dataSet.add(vault2)
        binding.addNewVault.setOnClickListener {
            hideVaultInfo()
            currentlySelectedPosition = -1
            // Тут добавление нового счёта
            var vault = Vault("Счёт 1", "Наличные", 130.0, R.drawable.dollar, true)
            dataSet.add(vault)
            onSumChange()
            binding.recyclerView.adapter?.notifyItemInserted(dataSet.size - 1)
        }

        onSumChange()
        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.root.setOnClickListener {
            hideVaultInfo()
            currentlySelectedPosition = -1
        }
        binding.vaultOnclickInfo.setOnClickListener {
        }
    }

    private fun hideVaultInfo() {
        binding.vaultMainLayout.foreground = null
        binding.vaultOnclickInfo.visibility = View.GONE
    }

    private fun onSumChange() {
        binding.vaultRubblesSum.text =
            (dataSet.filter { vault -> vault.currency == R.drawable.ruble }
                .sumOf { vault -> vault.balance }).toString()
        binding.vaultDollarsSum.text =
            (dataSet.filter { vault -> vault.currency == R.drawable.dollar }
                .sumOf { vault -> vault.balance }).toString()
        binding.vaultEurosSum.text =
            (dataSet.filter { vault -> vault.currency == R.drawable.euro }
                .sumOf { vault -> vault.balance }).toString()
    }

    override fun onVaultItemClick(position: Int) {
        if (currentlySelectedPosition == -1) {
            setupViews(dataSet[position])
            binding.vaultOnclickInfo.visibility = View.VISIBLE
            binding.vaultMainLayout.foreground = resources.getDrawable(R.color.gray)
            currentlySelectedPosition = position
            when (dataSet[position].currency) {
                R.drawable.ruble -> binding.vaultCurrencyDialog.contentDescription = "ruble"
                R.drawable.dollar -> binding.vaultCurrencyDialog.contentDescription = "dollar"
                R.drawable.euro -> binding.vaultCurrencyDialog.contentDescription = "euro"
            }
            binding.vaultCurrencyDialog.setOnClickListener {
                when (binding.vaultCurrencyDialog.contentDescription) {
                    "ruble" -> {
                        binding.vaultCurrencyDialog.contentDescription = "dollar"
                        binding.vaultCurrencyDialog.setImageIcon(
                            Icon.createWithResource(
                                context,
                                R.drawable.dollar
                            )
                        )
                    }

                    "dollar" -> {
                        binding.vaultCurrencyDialog.contentDescription = "euro"
                        binding.vaultCurrencyDialog.setImageIcon(
                            Icon.createWithResource(
                                context,
                                R.drawable.euro
                            )
                        )
                    }

                    "euro" -> {
                        binding.vaultCurrencyDialog.contentDescription = "ruble"
                        binding.vaultCurrencyDialog.setImageIcon(
                            Icon.createWithResource(
                                context,
                                R.drawable.ruble
                            )
                        )
                    }
                }
            }
            binding.vaultStatusImgDialog.contentDescription = if (dataSet[position].status) "checkmark" else "cross"
            binding.vaultStatusImgDialog.setOnClickListener {
                when (binding.vaultStatusImgDialog.contentDescription) {
                    "checkmark" -> {
                        binding.vaultStatusImgDialog.contentDescription = "cross"
                        binding.vaultStatusDialog.text = "Счёт закрыт"
                        binding.vaultStatusImgDialog.setImageIcon(Icon.createWithResource(context, R.drawable.cross))
                    }

                    "cross" -> {
                        binding.vaultStatusImgDialog.contentDescription = "checkmark"
                        binding.vaultStatusDialog.text = "Счёт открыт"
                        binding.vaultStatusImgDialog.setImageIcon(Icon.createWithResource(context, R.drawable.checkmark))
                    }
                }
            }
            binding.vaultSaveButtonDialog.setOnClickListener {
                changeItem(position)
            }
        } else {
            hideVaultInfo()
            currentlySelectedPosition = -1
        }
    }

    private fun changeItem(position: Int) {
        val name = if (!binding.vaultNameDialog.text.toString()
                .isEmpty()
        ) binding.vaultNameDialog.text.toString() else "Без имени"
        val type = if (!binding.vaultTypeDialog.selectedItem.toString()
                .isEmpty()
        ) binding.vaultTypeDialog.selectedItem.toString() else "Без типа"
        val balance = if (!binding.vaultBalanceDialog.text.toString()
                .isEmpty()
        ) binding.vaultBalanceDialog.text.toString().toDouble() else 0.0
        var currency = 0
        when (binding.vaultCurrencyDialog.contentDescription) {
            "ruble" -> currency = R.drawable.ruble
            "dollar" -> currency = R.drawable.dollar
            "euro" -> currency = R.drawable.euro
        }
        var status = false
        when (binding.vaultStatusImgDialog.contentDescription) {
            "checkmark" -> status = true
            "cross" -> status = false
        }
        val vault = Vault(name, type, balance, currency, status)
        dataSet[position] = vault
        binding.recyclerView.adapter?.notifyDataSetChanged()
        onSumChange()
        hideVaultInfo()
    }

    private fun setupViews(vault: Vault) {
        val status = if (vault.status) R.drawable.checkmark else R.drawable.cross
        binding.vaultNameDialog.hint = "Имя счёта: ${vault.name}"
        var type = 0
        when (vault.type){
            "Наличные" -> type = 0
            "Кредитка" -> type = 1
            "Дебетовая" -> type = 2
        }
        Log.i("type", type.toString())
        Log.i("type", vault.type)
        binding.vaultTypeDialog.setSelection(type)
        binding.vaultBalanceDialog.hint = "Баланс: ${vault.balance}"
        binding.vaultStatusDialog.text = if (vault.status) "Счёт открыт" else "Счёт закрыт"
        binding.vaultCurrencyDialog.setImageIcon(Icon.createWithResource(context, vault.currency))
        binding.vaultStatusImgDialog.setImageIcon(Icon.createWithResource(context, status))
    }

    private fun setupRecyclerView() {
        adapter = VaultsAdapter(dataSet, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
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

