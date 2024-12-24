package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mertyigit0.secretsantaai.R
import com.mertyigit0.secretsantaai.viewmodels.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private val settingViewModel: SettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Ayarlar ekranının layoutunu inflate et
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Logout butonuna tıklama işlemi
        val logoutButton: Button = view.findViewById(R.id.button_logout)
        logoutButton.setOnClickListener {
            // Kullanıcıyı çıkış yapmaya yönlendirecek ViewModel fonksiyonunu çağır
            settingViewModel.logout()

            // Logout işlemi sonrası login ekranına yönlendirme
            findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
        }
    }
}
