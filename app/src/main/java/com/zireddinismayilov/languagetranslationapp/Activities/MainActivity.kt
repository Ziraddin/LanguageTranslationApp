package com.zireddinismayilov.languagetranslationapp.Activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.zireddinismayilov.languagetranslationapp.R
import com.zireddinismayilov.languagetranslationapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavCont()
        bottomNavControllerSetUp()
        setUpHomeBtn()
        drawerNavToggleSetUp()
    }

    private fun initNavCont() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun bottomNavControllerSetUp() {
        val bottomNavView = binding.bottomnav
        bottomNavView.setupWithNavController(navController)
    }

    private fun setUpHomeBtn() {
        binding.homeBtn.apply {
            setOnClickListener {
                navController.navigate(R.id.homeFragment)
            }
        }
    }

    private fun drawerNavToggleSetUp() {
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, binding.myDrawerLayout, R.string.nav_open, R.string.nav_close
        )
        binding.myDrawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
}