package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.mertyigit0.secretsantaai.R
import com.mertyigit0.secretsantaai.databinding.FragmentAiFavoritesBinding
import com.mertyigit0.secretsantaai.ui.adapter.AiResultAdapter
import com.mertyigit0.secretsantaai.viewmodels.AiFavoritesViewModel
import kotlinx.coroutines.launch


import android.util.Log

// AiFavoritesFragment.kt
class AiFavoritesFragment : Fragment() {

    private val aiFavoritesViewModel: AiFavoritesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAiFavoritesBinding.inflate(inflater, container, false)
        binding.favoriteRecommendationsRecyclerView.layoutManager = GridLayoutManager(context, 2)

        lifecycleScope.launch {
            aiFavoritesViewModel.favoriteItems.collect { favorites ->
                val adapter = AiResultAdapter(favorites.toList()) { item ->
                    if (favorites.contains(item)) {
                        aiFavoritesViewModel.removeFromFavorites(item)
                    } else {
                        aiFavoritesViewModel.addToFavorites(item)
                    }
                }
                binding.favoriteRecommendationsRecyclerView.adapter = adapter

                val isEmpty = favorites.isEmpty()
                binding.favoriteRecommendationsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
                binding.emptyImageView.visibility = if (isEmpty) View.VISIBLE else View.GONE
                binding.emptyMessageText.visibility = if (isEmpty) View.VISIBLE else View.GONE
            }
        }
        return binding.root
    }
}
