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

            // Üyelerin adlarını veya nickname'lerini gösterebilirsiniz.
            val memberNicknames = group.members.joinToString { it["nickname"] ?: "Unknown" }
            binding.peopleTextView.text = "${group.members.size}"

            binding.dateTextView.text = group.date ?: "No Date Set"  // 'date' olarak güncellendi

            // Seçili öğe durumuna göre renk değiştirme
            if (position == selectedPosition) {
                binding.root.setBackgroundColor(Color.LTGRAY) // Seçili öğe için arka plan
            }
            itemView.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
                onItemClickListener?.invoke(group)
            }
        }
    }


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




