package com.mertyigit0.secretsantaai.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app.ui.viewmodel.CreateGroupViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mertyigit0.secretsantaai.R
import com.mertyigit0.secretsantaai.databinding.FragmentCreateGroupBinding
import java.text.SimpleDateFormat
import java.util.*

class CreateGroupFragment : Fragment() {

    private lateinit var binding: FragmentCreateGroupBinding
    private lateinit var viewModel: CreateGroupViewModel
    private var selectedDate: Date? = null // Store selected date as Date object

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(CreateGroupViewModel::class.java)

        // SeekBar listener to update budget display
        binding.seekBarBudget.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.textViewBudgetAmount.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Switch listener to trigger DatePickerDialog
        binding.switchDatePicker.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showDatePickerDialog()
            }
        }

        // Create group button listener
        binding.buttonCreateGroup.setOnClickListener {
            createGroup()
        }

        return binding.root
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            // Create a calendar instance and set the selected date
            val date = Calendar.getInstance()
            date.set(selectedYear, selectedMonth, selectedDay)
            selectedDate = date.time // Store selected date as Date object
        }, year, month, day).show()
    }

    private fun createGroup() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val name = document.getString("username") ?: "Unknown"
                val email = document.getString("email") ?: "Unknown"
                val groupName = binding.editTextGroupName.text.toString()
                val note = binding.editTextNote.text.toString()
                val budget = binding.seekBarBudget.progress

                val selectedDateString = selectedDate?.let {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dateFormat.format(it)
                }

                if (groupName.isNotEmpty()) {
                    // 'createdBy' parametresini ekliyoruz
                    viewModel.createGroup(email, name, groupName, note, budget, selectedDateString, userId)
                    Snackbar.make(binding.root, "Group Created Successfully", Snackbar.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_createGroupFragment_to_homeFragment)
                } else {
                    Snackbar.make(binding.root, "Please fill in all fields", Snackbar.LENGTH_LONG).show()
                }
            }
        }.addOnFailureListener {
            Snackbar.make(binding.root, "Failed to fetch user data", Snackbar.LENGTH_LONG).show()
        }
    }


}

