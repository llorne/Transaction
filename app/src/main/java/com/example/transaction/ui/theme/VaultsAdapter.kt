package com.example.transaction.ui.theme

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.transaction.databinding.ItemVaultsRvBinding

class VaultsAdapter(private val dataSet: MutableList<String>) :
    RecyclerView.Adapter<VaultsAdapter.VaultsViewHolder>() {
    class VaultsViewHolder(val binding: ItemVaultsRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaultsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemVaultsRvBinding.inflate(inflater, parent, false)
        return VaultsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VaultsViewHolder, position: Int) {
        val data = dataSet[position] // Получение счёта из списка данных по позиции
        val context = holder.itemView.context

        with(holder.binding) {
            vaultType.text = dataSet[position]
            vaultBalance.text = dataSet[position]
            deleteVault.setOnClickListener {
                // Тут удаление счёта
                if (dataSet[position].toFloat() != 0F){
                    dataSet.removeAt(position)
                    notifyItemRemoved(position)
                }else{
                    Toast.makeText(context, "Вы не можете удалить счёт с ненулевым балансом", Toast.LENGTH_SHORT).show()
                }
            }
        }
        holder.itemView.setOnClickListener {
            // Тут отображение транзакций при клике на счёт
            Toast.makeText(context, "Транзакций пока нет", Toast.LENGTH_SHORT).show()
        }
    }
    override fun getItemCount(): Int = dataSet.size
}