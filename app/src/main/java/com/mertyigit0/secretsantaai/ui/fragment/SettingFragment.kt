package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mertyigit0.secretsantaai.R
import com.mertyigit0.secretsantaai.databinding.FragmentSettingBinding
import com.mertyigit0.secretsantaai.viewmodels.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private val settingViewModel: SettingViewModel by viewModels()
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogout.setOnClickListener {
            settingViewModel.logout()

            // Geriye doğru fragment temizleme işlemi kaldırıldı
            findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
