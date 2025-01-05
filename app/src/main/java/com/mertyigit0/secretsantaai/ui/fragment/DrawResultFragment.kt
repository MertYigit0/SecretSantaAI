package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mertyigit0.secretsantaai.databinding.FragmentDrawResultBinding

class DrawResultFragment : Fragment() {

    private var _binding: FragmentDrawResultBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrawResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = auth.currentUser?.uid ?: return
        val groupId = arguments?.getString("groupId") ?: return

        // Kullanıcının hediye alacağı kişiyi almak
        firestore.collection("drawResults")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val recipientId = document.getString("recipientId")
                    binding.giftReceiverTextView.text = "You will give a gift to: $recipientId"
                } else {
                    binding.giftReceiverTextView.text = "No draw result found."
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
