package com.example.campainhasmart

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.campainhasmart.databinding.ActivityMainBinding
import com.example.campainhasmart.model.Repository
import com.google.android.material.bottomnavigation.BottomNavigationView
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var n = 0
    private val id: Int
        get() = n++

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard
//                R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        Repository.getRepository(this).notificationOccurrence.observe(this) {
            it?.let {
                Timber.d("Notification: ${it.id}")
                warningDialog("Há uma nova ocorrência a ${it.dateString}")
            }

        }

    }


    private fun warningDialog(message: String) {

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.create().show()
    }
}