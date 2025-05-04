package com.example.smart.main

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.smart.R
import com.example.smart.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import io.ak1.BubbleTabBar
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import com.example.smart.ui.report_issue.GuideBottomSheet

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bubbleTabBar: BubbleTabBar
    private val excludedFragmentsInBottomNav: List<Int> = listOf(
        R.id.firstPageFragment,
        R.id.secondPageFragment,
        R.id.thirdPageFragment,
        R.id.fourthPageFragment,
        R.id.fifthPageFragment,
        R.id.sixthPageFragment,
        R.id.homeAccountFragment,
        R.id.firstPageFragment,
        R.id.loginFragment,
        R.id.registerFragment,
        R.id.registerPasswordFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bubbleTabBar = binding.bubbleTabBar
        val fab = binding.fab

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.navController

        bubbleTabBar.addBubbleListener { id ->
            when (id) {
                R.id.firstPage -> {
                    if (navController.currentDestination?.id != R.id.firstPage) {
                        navController.navigate(R.id.homeFragment)
                    }
                }
                R.id.secondPage -> {
                    if (navController.currentDestination?.id != R.id.secondPage) {
                        navController.navigate(R.id.issueListFragment)
                    }
                }
                R.id.thirdPage -> {
                    if (navController.currentDestination?.id != R.id.thirdPage) {
                        navController.navigate(R.id.profileFragment)
                    }
                }
            }
        }

        fab.setOnClickListener {
            val itemInfoBottomSheet = GuideBottomSheet()
            itemInfoBottomSheet.show(supportFragmentManager, "guide_bottom_sheet")
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bubbleTabBar.setSelectedWithId(destination.id, false)

            if (!excludedFragmentsInBottomNav.contains(destination.id)) {
                bubbleTabBar.visibility = View.VISIBLE
                fab.visibility = View.VISIBLE
            } else {
                bubbleTabBar.visibility = View.GONE
                fab.visibility = View.GONE
            }
        }
    }
}