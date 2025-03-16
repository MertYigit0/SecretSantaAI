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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.secretsantaai.data.repository.DataStoreManager
import com.mertyigit0.secretsantaai.databinding.ActivityMainBinding
import com.mertyigit0.secretsantaai.ui.fragment.HomeFragment
import com.mertyigit0.secretsantaai.ui.fragment.LoginFragment
import com.mertyigit0.secretsantaai.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var auth: FirebaseAuth

    private val viewModel: MainViewModel by viewModels()
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavController()

        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)

        // **Dil değişikliklerini ve oturum durumunu gözlemle**
        observeViewModel()

        setupNotificationPermission()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.visibility =
                if (destination.id in listOf(R.id.loginFragment, R.id.registerFragment)) View.GONE else View.VISIBLE
        }
    }

    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun observeViewModel() {
        viewModel.languageCode.observe(this) { languageCode ->
            updateLocale(languageCode)
        }

        viewModel.isUserAuthenticated.observe(this) { isAuthenticated ->
            val destination = if (isAuthenticated) R.id.homeFragment else R.id.loginFragment
            navController.navigate(destination)
        }
    }

    private fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun setupNotificationPermission() {
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            val message = if (isGranted) "Bildirim izni verildi." else "Bildirim izni reddedildi."
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

