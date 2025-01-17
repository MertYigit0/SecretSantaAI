package com.mertyigit0.secretsantaai.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mertyigit0.secretsantaai.databinding.FragmentDrawResultBinding
import com.mertyigit0.secretsantaai.viewmodels.DrawResultViewModel
import com.airbnb.lottie.LottieAnimationView
import com.mertyigit0.secretsantaai.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DrawResultFragment : Fragment() {

    private var _binding: FragmentDrawResultBinding? = null
    private val binding get() = _binding!!
    private val drawResultViewModel: DrawResultViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()

    private val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrawResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val groupId = arguments?.getString("groupId") ?: return
        val userId = auth.currentUser?.uid ?: return

        // Butona tıklama olayını dinleyin

            // Lottie animasyonu başlat
            val lottieAnimationView: LottieAnimationView = binding.lottieAnimationView
            lottieAnimationView.playAnimation()




        // Çekiliş sonuçlarını çek
        drawResultViewModel.fetchDrawResult(userId, groupId)

        drawResultViewModel.giftReceiver.observe(viewLifecycleOwner) { receiverName ->
            // Çekiliş sonucunu göster
            binding.giftReceiverTextView.text = receiverName ?: getString(R.string.no_match_found)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null) // Handler'ı temizle
        _binding = null
    }
}
