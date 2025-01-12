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
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.secretsantaai.databinding.ActivityMainBinding
import com.mertyigit0.secretsantaai.ui.fragment.HomeFragment
import com.mertyigit0.secretsantaai.ui.fragment.LoginFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val auth = FirebaseAuth.getInstance()
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Bottom Navigation'ı ilk başta ayarla
        setupBottomNav()

        // Kullanıcı zaten giriş yaptıysa, HomeFragment'ı aç
        if (auth.currentUser != null) {
            navController.navigate(R.id.homeFragment) // Burada direkt olarak NavController kullanabilirsiniz
        } else {
            navController.navigate(R.id.loginFragment) // Burada da NavController kullanarak geçiş yapabilirsiniz
        }

        // **Launcher burada tanımlanıyor (onCreate'in başında)**
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Bildirim izni verildi.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Bildirim izni verilmedi.", Toast.LENGTH_SHORT).show()
            }
        }

        // Status Bar Rengi
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)

        // Destination değişimlerini dinle
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }

        // **Android 13 ve üzeri için izin kontrolü**
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission()
        }


        // **Android 13 ve üzeri için izin kontrolü**
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
            // İzin zaten verilmiş
        } else {
            // İzin iste
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
