package com.mertyigit0.secretsantaai

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.mertyigit0.secretsantaai.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNav()
        hideOrShowBottomNav()

        // Bildirim izni başlatıcısını burada tanımla
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Bildirim izni verildi.", Toast.LENGTH_SHORT).show()
                // Bildirim izni verildikten sonra yapılacaklar
            } else {
                Toast.makeText(this, "Bildirim izni verilmedi.", Toast.LENGTH_SHORT).show()
            }
        }

        // Android 13 ve üzeri için bildirim izni kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission()
        }
    }

    private fun hideOrShowBottomNav() {
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
        }
    }

    private fun setupBottomNav() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Zaten izin verildi, burada bildirim başlatılabilir
        } else {
            // Daha önce izin verilmediyse veya reddedildiyse izin iste
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
