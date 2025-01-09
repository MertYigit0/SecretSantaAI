// AiResultAdapter.kt
package com.mertyigit0.secretsantaai.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.secretsantaai.R
import com.mertyigit0.secretsantaai.databinding.ItemGiftRecommendationBinding

class AiResultAdapter(
    private val recommendations: List<String>,
    private val onFavoriteClick: (String) -> Unit // Favoriye ekleme için callback
) : RecyclerView.Adapter<AiResultAdapter.ViewHolder>() {

    private val favoriteItems = mutableSetOf<String>() // Favori öğeleri tutacak set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGiftRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recommendation = recommendations[position]
        holder.binding.giftRecommendationText.text = recommendation

        // Yıldız simgesinin durumunu kontrol et
        val starIcon = if (favoriteItems.contains(recommendation)) {
            R.drawable.baseline_star_24 // Sarı yıldız
        } else {
            R.drawable.baseline_star_border_24 // Boş yıldız
        }
        holder.binding.favoriteStar.setImageResource(starIcon)

        // Yıldız tıklama olayını işleyin
        holder.binding.favoriteStar.setOnClickListener {
            if (favoriteItems.contains(recommendation)) {
                favoriteItems.remove(recommendation)
            } else {
                favoriteItems.add(recommendation)
            }
            notifyItemChanged(position)
            onFavoriteClick(recommendation) // Favorilere ekle
        }
    }

    override fun getItemCount(): Int = recommendations.size

    class ViewHolder(val binding: ItemGiftRecommendationBinding) : RecyclerView.ViewHolder(binding.root)
}
