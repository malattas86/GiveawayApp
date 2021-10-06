package com.mats.giveawayapp.ui.activities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mats.giveawayapp.MyApplication
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.ActivityDashboardBinding
import com.mats.giveawayapp.firestore.FirestoreClass

class DashboardActivity : BaseActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private var onlineStatus: MyApplication = MyApplication()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard,
                R.id.navigation_itemBoard,
                R.id.navigation_chatBoard,
                R.id.navigation_notifications
            )
        )*/

        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }

    override fun onResume() {
        super.onResume()
        onlineStatus.onMoveToForeground()
        val te = findViewById<View>(R.id.navigation_chatBoard)
        if (FirebaseAuth.getInstance().currentUser != null) {
            te.visibility = View.VISIBLE
        } else {
            te.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        onlineStatus.onMoveToBackground()
    }
}