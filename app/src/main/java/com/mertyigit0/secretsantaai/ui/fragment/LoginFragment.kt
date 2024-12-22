package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mertyigit0.secretsantaai.R
import com.mertyigit0.secretsantaai.databinding.FragmentLoginBinding
import com.mertyigit0.secretsantaai.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmailEditText.text.toString()
            val password = binding.editTextPassword.text.toString()
            loginViewModel.loginUser(email, password)
        }

        // Sign up tıklama işlemi
        binding.registerText.setOnClickListener {
            navigateToSignUp()
        }
        binding.registerNowText.setOnClickListener {
            navigateToSignUp()
        }

        loginViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                // Başka bir ekrana yönlendirme işlemi
            }.onFailure {
                Toast.makeText(requireContext(), "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // SignUp'a yönlendirme işlemi
    private fun navigateToSignUp() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }
}
