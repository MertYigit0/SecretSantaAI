package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.mertyigit0.secretsantaai.databinding.FragmentAiResultBinding
import com.mertyigit0.secretsantaai.ui.adapter.AiResultAdapter
import com.mertyigit0.secretsantaai.utils.UiState
import com.mertyigit0.secretsantaai.viewmodels.AiFavoritesViewModel
import com.mertyigit0.secretsantaai.viewmodels.AiHelpViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AiResultFragment : Fragment() {

    private var _binding: FragmentAiResultBinding? = null
    private val binding get() = _binding!!
    private val aiFavoritesViewModel: AiFavoritesViewModel by activityViewModels()
    private val aiHelpViewModel: AiHelpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAiResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView ayarları
        val giftRecommendationsRecyclerView = binding.giftRecommendationsRecyclerView
        giftRecommendationsRecyclerView.layoutManager = GridLayoutManager(context, 2)

        // ProgressBar ayarları
        val loadingProgressBar = binding.loadingProgressBar

        // StateFlow'u collect etmek için lifecycleScope kullanıyoruz
        lifecycleScope.launch {
            aiHelpViewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        loadingProgressBar.visibility = View.VISIBLE
                        giftRecommendationsRecyclerView.visibility = View.GONE
                    }
                    is UiState.Success -> {
                        loadingProgressBar.visibility = View.GONE
                        giftRecommendationsRecyclerView.visibility = View.VISIBLE
                        val recommendations = state.recommendation.split("\n")
                        val adapter = AiResultAdapter(recommendations) { recommendation ->
                            // Favori öğe tıklama işlemi
                            aiFavoritesViewModel.addToFavorites(recommendation)
                        }
                        giftRecommendationsRecyclerView.adapter = adapter
                    }
                    is UiState.Error -> {
                        loadingProgressBar.visibility = View.GONE
                        giftRecommendationsRecyclerView.visibility = View.GONE
                        // Hata mesajını göstermek için uygun bir yöntem ekleyebilirsiniz
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


