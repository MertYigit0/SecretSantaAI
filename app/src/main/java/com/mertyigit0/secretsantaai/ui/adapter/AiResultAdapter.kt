// AiResultAdapter.kt
package com.mertyigit0.secretsantaai.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.secretsantaai.R
import com.mertyigit0.secretsantaai.databinding.ItemGiftRecommendationBinding

// AiResultAdapter.kt
class AiResultAdapter(
    private val recommendations: List<String>,
    private val onFavoriteClick: (String) -> Unit
) : RecyclerView.Adapter<AiResultAdapter.ViewHolder>() {

    private val favoriteItems = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGiftRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recommendation = recommendations[position]
        holder.binding.root.visibility = if (recommendation.isEmpty()) View.GONE else View.VISIBLE
        holder.binding.giftRecommendationText.text = recommendation

        val starIcon = if (favoriteItems.contains(recommendation)) {
            R.drawable.baseline_star_24
        } else {
            R.drawable.baseline_star_border_24
        }
        holder.binding.favoriteStar.setImageResource(starIcon)

        holder.binding.favoriteStar.setOnClickListener {
            if (favoriteItems.contains(recommendation)) {
                favoriteItems.remove(recommendation)
            } else {
                favoriteItems.add(recommendation)
            }
            notifyItemChanged(position)
            onFavoriteClick(recommendation)
        }
    }

    override fun getItemCount(): Int = recommendations.size

    class ViewHolder(val binding: ItemGiftRecommendationBinding) : RecyclerView.ViewHolder(binding.root)
}
