package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.app.ui.viewmodel.CreateGroupViewModel
import com.google.android.material.snackbar.Snackbar
import com.mertyigit0.secretsantaai.databinding.FragmentCreateGroupBinding
import java.util.*

class CreateGroupFragment : Fragment() {

    private lateinit var binding: FragmentCreateGroupBinding
    private lateinit var viewModel: CreateGroupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ViewBinding kullanarak binding nesnesini oluşturuyoruz
        binding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(CreateGroupViewModel::class.java)

        // SeekBar listener'ını ayarlıyoruz
        binding.seekBarBudget.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Gerekirse ilerleme durumuna göre UI'ı güncelleyebilirsiniz
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Switch için DatePicker görünürlüğünü ayarlıyoruz
        binding.switchDatePicker.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.datePicker.visibility = View.VISIBLE
            } else {
                binding.datePicker.visibility = View.GONE
            }
        }

        // Grup oluşturma butonuna tıklama işlemi
        binding.buttonCreateGroup.setOnClickListener {
            createGroup()
        }

        return binding.root
    }

    private fun createGroup() {
        val userName = binding.editTextUserName.text.toString()
        val groupName = binding.editTextGroupName.text.toString()
        val note = binding.editTextNote.text.toString()
        val budget = binding.seekBarBudget.progress
        val selectedDate = if (binding.switchDatePicker.isChecked) {
            "${binding.datePicker.year}-${binding.datePicker.month + 1}-${binding.datePicker.dayOfMonth}"
        } else {
            null
        }

        if (userName.isNotEmpty() && groupName.isNotEmpty()) {
            // ViewModel'e grup oluşturma işlemini çağırıyoruz
            viewModel.createGroup(userName, groupName, note, budget, selectedDate)
            Snackbar.make(binding.root, "Group Created Successfully", Snackbar.LENGTH_LONG).show()
        } else {
            Snackbar.make(binding.root, "Please fill in all fields", Snackbar.LENGTH_LONG).show()
        }
    }
}
