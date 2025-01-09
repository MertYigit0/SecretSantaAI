package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.RadioButton
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
            val gender = getSelectedGender()  // Seçilen cinsiyet
            val occasion = binding.occasionSpinner.selectedItem.toString()
            val interests = binding.interestsEditText.text.toString()
            val budget = binding.budgetEditText.text.toString()

            if (age.isNotEmpty() && gender.isNotEmpty() && occasion.isNotEmpty()) {
                aiHelpViewModel.getGiftRecommendation(age, gender, occasion, interests, budget)

                // AiResultFragment'e geçiş yapılıyor
                val action = AiHelpFragmentDirections.actionAiHelpFragmentToAiResultFragment()
                findNavController().navigate(action)
            } else {
                Toast.makeText(context, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }

    }

    // Cinsiyeti al
    private fun getSelectedGender(): String {
        val selectedGenderId = binding.genderRadioGroup.checkedRadioButtonId
        val genderRadioButton: RadioButton = binding.root.findViewById(selectedGenderId)
        return genderRadioButton.text.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
