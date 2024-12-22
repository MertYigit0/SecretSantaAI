package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mertyigit0.secretsantaai.R
import com.mertyigit0.secretsantaai.databinding.FragmentRegisterBinding
import com.mertyigit0.secretsantaai.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Kayıt butonuna tıklama işlemi
        binding.registerButton.setOnClickListener {
            val email = binding.registerEmailEditText.text.toString().trim()
            val password = binding.registerPasswordEditText.text.toString().trim()
            val confirmPassword = binding.registerConfirmPasswordEditText.text.toString().trim()

            // Hata kontrolü
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                registerViewModel.register(email, password)
            }
        }

        // Kayıt işlemi sonucunu dinleme
        registerViewModel.registerStatus.observe(viewLifecycleOwner) { result ->
            when {
                result.isSuccess -> {
                    // Kayıt başarılı
                    Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT).show()
                    // HomeFragment'e yönlendirme
                    findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                }
                result.isFailure -> {
                    // Kayıt başarısız
                    Toast.makeText(requireContext(), "Registration failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Zaten bir hesabı olan kullanıcılar için LoginFragment'e yönlendirme
        binding.loginNowText.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.loginText.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
