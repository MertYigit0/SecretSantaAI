package com.mertyigit0.secretsantaai.ui.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.mertyigit0.secretsantaai.data.model.Group
import com.mertyigit0.secretsantaai.databinding.FragmentInviteBinding
import com.mertyigit0.secretsantaai.ui.adapter.GroupAdapter
import com.mertyigit0.secretsantaai.viewmodels.InviteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InviteFragment : Fragment() {

    private var _binding: FragmentInviteBinding? = null
    private val binding get() = _binding!!
    private val inviteViewModel: InviteViewModel by viewModels()

    private val groupAdapter = GroupAdapter()

    // Firebase Authentication instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInviteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView yapılandırması
        binding.groupsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.groupsRecyclerView.adapter = groupAdapter

        // Kullanıcı ID'si ile grupları yükleme
        val userId = auth.currentUser?.uid // Firebase Authentication'dan kullanıcı ID'si alıyoruz

        if (userId != null) {
            inviteViewModel.loadUserGroups(userId)
        } else {
            // Kullanıcı oturum açmamışsa uyarı göster
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }

        // Kullanıcı gruplarını gözlemliyoruz
        inviteViewModel.userGroups.observe(viewLifecycleOwner) { groups ->
            groupAdapter.submitList(groups)
        }

        // Tıklama olayını dinliyoruz
        groupAdapter.setOnItemClickListener { group ->
            generateQrCode(group)
        }
    }

    // QR kod üretme fonksiyonu
    private fun generateQrCode(group: Group) {
        try {
            val qrCodeData = group.groupId // QR kod için grup ID'sini kullanıyoruz

            val hints = mutableMapOf<EncodeHintType, Any>()
            hints[EncodeHintType.MARGIN] = 1 // QR kodunun kenar boşluğunu ayarlıyoruz

            // ZXing ile QR kodu üretiyoruz
            val qrCodeWriter = QRCodeWriter()
            val bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, 512, 512, hints)

            // QR kodu bitmap'e dönüştürme
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }

            // QR kodu bir ImageView'de gösterme
            binding.qrCodeImageView.setImageBitmap(bitmap)

            // Kullanıcıya bilgilendirme
            Toast.makeText(requireContext(), "QR Code for ${group.groupName} generated", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error generating QR code", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
