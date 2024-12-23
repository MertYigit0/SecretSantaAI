package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.appcompat.app.AlertDialog
import com.mertyigit0.secretsantaai.R
import com.mertyigit0.secretsantaai.databinding.FragmentJoinGroupBinding
import com.mertyigit0.secretsantaai.viewmodels.JoinGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinGroupFragment : Fragment() {

    private var _binding: FragmentJoinGroupBinding? = null
    private val binding get() = _binding!!
    private val joinGroupViewModel: JoinGroupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJoinGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.setOnClickListener {
            showJoinGroupDialog()
        }
    }

    private fun showJoinGroupDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter Group ID")

        val input = EditText(requireContext())
        input.hint = "Group ID"

        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val groupId = input.text.toString().trim()

            if (groupId.isNotEmpty()) {
                checkGroupExistence(groupId)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid Group ID", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun checkGroupExistence(groupId: String) {
        joinGroupViewModel.checkIfGroupExists(groupId).observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                if (it) {
                    joinGroup(groupId)
                } else {
                    Toast.makeText(requireContext(), "Group does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            result.onFailure {
                Toast.makeText(requireContext(), "Error checking group: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun joinGroup(groupId: String) {
        // Kullanıcıyı gruba eklemek için ViewModel'den çağrı
        val userId = "someUserId"  // Bu, giriş yapmış kullanıcı ID'si olmalı
        joinGroupViewModel.joinGroup(groupId, userId).observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Successfully joined the group", Toast.LENGTH_SHORT).show()
            }
            result.onFailure {
                Toast.makeText(requireContext(), "Failed to join the group: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
