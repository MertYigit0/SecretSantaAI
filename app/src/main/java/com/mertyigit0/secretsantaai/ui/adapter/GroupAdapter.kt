package com.mertyigit0.secretsantaai.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.secretsantaai.databinding.ItemGroupBinding
import com.mertyigit0.secretsantaai.data.model.Group

class GroupAdapter : ListAdapter<Group, GroupAdapter.GroupViewHolder>(GroupDiffCallback()) {

    // Tıklama işlevini almak için bir listener tanımlıyoruz.
    private var onItemClickListener: ((Group) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = getItem(position)
        holder.bind(group)
    }

    // ViewHolder sınıfı
    inner class GroupViewHolder(private val binding: ItemGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(group: Group) {
            binding.groupName.text = group.groupName
            // Tıklama olayını dinliyoruz
            itemView.setOnClickListener {
                onItemClickListener?.invoke(group)
            }
        }
    }

    // onItemClickListener fonksiyonunu set etmek için bir metod ekliyoruz
    fun setOnItemClickListener(listener: (Group) -> Unit) {
        onItemClickListener = listener
    }

    // Group nesnelerinin karşılaştırılmasını sağlayan DiffCallback
    class GroupDiffCallback : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem.groupId == newItem.groupId
        }

        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem == newItem
        }
    }
}

