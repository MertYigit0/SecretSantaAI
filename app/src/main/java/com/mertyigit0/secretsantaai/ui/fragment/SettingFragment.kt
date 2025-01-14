package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
            showCustomLogoutDialog()
        }

        binding.textLanguageRegion.setOnClickListener {
            showLanguageSelectionDialog()
        }
    }


    private fun showLanguageSelectionDialog() {
        // Dialog için layout'u inflate et
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_language_custom, null)

        // Dialog oluştur
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // RadioGroup'dan seçilen dil bilgisini al
        val rgLanguage = dialogView.findViewById<android.widget.RadioGroup>(R.id.rgLanguage)
        val btnSave = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnDialogSave)
        val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnDialogCancel)

        // Save butonuna tıklandığında seçilen dili işleme
        btnSave.setOnClickListener {
            val selectedLanguageId = rgLanguage.checkedRadioButtonId
            if (selectedLanguageId != -1) {
                val selectedLanguage = dialogView.findViewById<android.widget.RadioButton>(selectedLanguageId).text
                // Burada, seçilen dili işleyebilirsin. Örneğin:
                // Toast.makeText(context, "Selected Language: $selectedLanguage", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        // Cancel butonuna tıklandığında dialog'u kapat
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        // Dialog'u göster
        dialog.show()
    }


    private fun showCustomLogoutDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout_custom, null)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnDialogCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnDialogLogout).setOnClickListener {
            settingViewModel.logout()
            findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
