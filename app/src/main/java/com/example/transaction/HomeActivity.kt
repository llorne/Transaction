package com.example.transaction

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.example.transaction.databinding.ActivityHomeBinding
import com.example.transaction.ui.theme.AnalyzeFragment
import com.example.transaction.ui.theme.GoalsFragment
import com.example.transaction.ui.theme.ProfileFragment
import com.example.transaction.ui.theme.TransactionsFragment
import com.example.transaction.ui.theme.VaultsFragment

class HomeActivity: AppCompatActivity()  {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(VaultsFragment())
        binding.bottomNavigation.setOnNavigationItemSelectedListener() { item ->
            when(item.itemId) {
                R.id.vaults -> replaceFragment(VaultsFragment())
                R.id.transactions -> replaceFragment(TransactionsFragment())
                R.id.goals -> replaceFragment(GoalsFragment())
                R.id.analyze -> replaceFragment(AnalyzeFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                else -> {}
            }
            true
        }
    }
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.host_fragment, fragment)
        fragmentTransaction.commit()
    }
}