package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.mertyigit0.secretsantaai.databinding.FragmentAiHelpBinding
import com.mertyigit0.secretsantaai.viewmodels.AiHelpViewModel

class AiHelpFragment : Fragment() {

    private var _binding: FragmentAiHelpBinding? = null
    private val binding get() = _binding!!
    private val aiHelpViewModel: AiHelpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAiHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Submit butonuna tıklama işlemi
        binding.submitButton.setOnClickListener {
            val age = binding.ageEditText.text.toString()
            val gender = "man"
            val occasion = binding.occasionSpinner.selectedItem.toString()

            if (age.isNotEmpty() && gender.isNotEmpty() && occasion.isNotEmpty()) {
                aiHelpViewModel.getGiftRecommendation(age, gender, occasion)

                // AiResultFragment'e geçiş yapılıyor
                val action = AiHelpFragmentDirections.actionAiHelpFragmentToAiResultFragment()
                findNavController().navigate(action)
            } else {
                Toast.makeText(context, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
