package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.mertyigit0.secretsantaai.databinding.FragmentAiResultBinding
import com.mertyigit0.secretsantaai.utils.UiState
import com.mertyigit0.secretsantaai.viewmodels.AiHelpViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AiResultFragment : Fragment() {

    private var _binding: FragmentAiResultBinding? = null
    private val binding get() = _binding!!
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

        // StateFlow'u collect etmek için lifecycleScope kullanıyoruz
        lifecycleScope.launch {
            aiHelpViewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.recommendationTextView.text = "Yükleniyor..."
                    }
                    is UiState.Success -> {
                        binding.recommendationTextView.text = state.recommendation  // API'den gelen öneri
                    }
                    is UiState.Error -> {
                        binding.recommendationTextView.text= "Hata: ${state.message}"
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
