package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.mertyigit0.secretsantaai.R
import com.mertyigit0.secretsantaai.databinding.FragmentCreateGroupBinding
import com.mertyigit0.secretsantaai.viewmodels.CreateGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateGroupFragment : Fragment() {

    private var _binding: FragmentCreateGroupBinding? = null
    private val binding get() = _binding!!

    // Hilt ile ViewModel enjeksiyonu
    private val createGroupViewModel: CreateGroupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Switch'in durumunu kontrol et
        binding.switchDatePicker.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.datePicker.visibility = View.VISIBLE
            } else {
                binding.datePicker.visibility = View.GONE
            }
        }

        // Create Group butonuna tıklama
        binding.buttonCreateGroup.setOnClickListener {
            val userName = binding.editTextUserName.text.toString().trim()
            val groupName = binding.editTextGroupName.text.toString().trim()
            val note = binding.editTextNote.text.toString().trim()

            if (userName.isEmpty() || groupName.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // LiveData'yı gözlemleme
                createGroupViewModel.createGroup(groupName, userName).observe(viewLifecycleOwner, Observer { result ->
                    result.onSuccess {
                        Toast.makeText(requireContext(), "Group Created", Toast.LENGTH_SHORT).show()
                        // Grup oluşturulmuşsa başka bir yere yönlendirme yapılabilir
                        findNavController().navigate(R.id.action_createGroupFragment_to_homeFragment)
                    }.onFailure {
                        Toast.makeText(requireContext(), "Group creation failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
