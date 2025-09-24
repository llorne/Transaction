package com.example.transaction

import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemVaultsRvBinding
import com.example.transaction.models.Vault

class VaultsAdapter(
    private val dataSet: MutableList<Vault>,
    private val listener: OnVaultItemClickListener
) :
    RecyclerView.Adapter<VaultsAdapter.VaultsViewHolder>() {
    class VaultsViewHolder(val binding: ItemVaultsRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaultsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemVaultsRvBinding.inflate(inflater, parent, false)
        return VaultsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VaultsViewHolder, position: Int) {
        val vault = dataSet[position] // Получение счёта из списка данных по позиции
        val context = holder.itemView.context
        val status = if (vault.status) R.drawable.checkmark else R.drawable.cross
        with(holder.binding) {
            vaultName.text = vault.name
            vaultType.text = vault.type
            vaultBalance.text = vault.balance.toString()
            // Если счёт в евро - R.drawable.euro, рублях - R.drawable.rubles и если в долларах - R.drawable.dollar
            vaultCurrency.setImageIcon(Icon.createWithResource(context, vault.currency))
            vaultStatus.setImageIcon(Icon.createWithResource(context, status))
        }
        holder.itemView.setOnClickListener {
            //VaultInfoDialog(context, vault).show()
            listener.onVaultItemClick(position)

            // Тут отображение транзакций при клике на счёт
            Toast.makeText(context, "Транзакций пока нет", Toast.LENGTH_SHORT).show()
        }
    }

    interface OnVaultItemClickListener {
        fun onVaultItemClick(position: Int)
    }

    override fun getItemCount(): Int = dataSet.size
}