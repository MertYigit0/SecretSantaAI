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


class AiFavoritesFragment : Fragment() {

    private val aiFavoritesViewModel: AiFavoritesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAiFavoritesBinding.inflate(inflater, container, false)

        // RecyclerView setup
        binding.favoriteRecommendationsRecyclerView.layoutManager = GridLayoutManager(context, 2)

        // Flow'dan veri almak ve RecyclerView'da göstermek için lifecycleScope.launch kullanıyoruz
        lifecycleScope.launch {
            aiFavoritesViewModel.favoriteItems.collect { favorites ->
                // Favori öğelerini listele
                val adapter = AiResultAdapter(favorites.toList()) { item ->
                    // Favori öğe ekleme veya çıkarma işlemi
                    if (favorites.contains(item)) {
                        aiFavoritesViewModel.removeFromFavorites(item)
                    } else {
                        aiFavoritesViewModel.addToFavorites(item)
                    }
                }
                binding.favoriteRecommendationsRecyclerView.adapter = adapter
            }
        }

        return binding.root
    }
}


