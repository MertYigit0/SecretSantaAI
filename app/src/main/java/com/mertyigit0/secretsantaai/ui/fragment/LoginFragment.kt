package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.secretsantaai.R
import com.mertyigit0.secretsantaai.databinding.FragmentLoginBinding
import com.mertyigit0.secretsantaai.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        // Kullanıcı zaten giriş yaptıysa homeFragment'a yönlendirilir
        if (firebaseAuth.currentUser != null) {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            return
        }

        setupLoginButton()
        setupObservers()
        setupRegisterText()
    }

    private fun setupLoginButton() {
        binding.loginButton.setOnClickListener {
            val email = binding.loginEmailEditText.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.fill_in_all_fields), Toast.LENGTH_SHORT).show()

            } else {
                loginViewModel.login(email, password)
            }
        }
    }

    private fun setupObservers() {
        loginViewModel.loginStatus.observe(viewLifecycleOwner) { result ->
            when {
                result.isSuccess -> {
                    Toast.makeText(requireContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
                result.isFailure -> {
                    result.exceptionOrNull()?.let {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.login_failed, it.message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun setupRegisterText() {
        binding.registerNowText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
