package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mertyigit0.secretsantaai.R
import com.mertyigit0.secretsantaai.databinding.FragmentCreateGroupBinding

class CreateGroupFragment : Fragment() {

    private var _binding: FragmentCreateGroupBinding? = null
    private val binding get() = _binding!!

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
                // Grup oluşturma işlemi yapılacak
                Toast.makeText(requireContext(), "Group Created", Toast.LENGTH_SHORT).show()
                // Group oluşturulmuşsa başka bir yere yönlendirme yapılabilir
                findNavController().navigate(R.id.action_createGroupFragment_to_homeFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
