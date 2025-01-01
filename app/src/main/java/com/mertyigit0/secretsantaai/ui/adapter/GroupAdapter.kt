package com.mertyigit0.secretsantaai.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.secretsantaai.databinding.ItemGroupBinding
import com.mertyigit0.secretsantaai.data.model.Group

class GroupAdapter : ListAdapter<Group, GroupAdapter.GroupViewHolder>(GroupDiffCallback()) {

    private var onItemClickListener: ((Group) -> Unit)? = null
    private var selectedPosition: Int = -1  // Seçili öğe için pozisyon

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = getItem(position)
        holder.bind(group, position)
    }

    inner class GroupViewHolder(private val binding: ItemGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(group: Group, position: Int) {
            binding.groupName.text = group.groupName

            // Seçili öğe durumuna göre renk değiştirme
            if (position == selectedPosition) {
                // Seçili öğe için stil veya renk değişikliği
                binding.root.setBackgroundColor(android.graphics.Color.LTGRAY) // Örneğin gri arka plan
            } else {
                // Diğer öğeler için varsayılan stil
                binding.root.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            }

            // Tıklama işlemi
            itemView.setOnClickListener {
                selectedPosition = position  // Tıklanan öğeyi seçili olarak işaretle
                notifyDataSetChanged()  // Tüm öğeleri yeniden render et
                onItemClickListener?.invoke(group)
            }
        }
    }

    // Tıklama dinleyicisini set etmek için metod
    fun setOnItemClickListener(listener: (Group) -> Unit) {
        onItemClickListener = listener
    }

    class GroupDiffCallback : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem.groupId == newItem.groupId
        }

        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem == newItem
        }
    }
}


