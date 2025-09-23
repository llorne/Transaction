package com.example.transaction

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.transaction.databinding.ActivityMainBinding
import com.example.transaction.ui.LoginFragment
import com.example.transaction.ui.theme.AnalyzeFragment
import com.example.transaction.ui.theme.HomeFragment
import com.example.transaction.ui.theme.ProfileFragment
import com.example.transaction.ui.theme.TransactionsFragment
import com.example.transaction.ui.theme.VaultsFragment


class MainActivity : AppCompatActivity(), LoginFragment.OnFragmentActionListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNav()
    }
    override fun onChangeAttribute(newValue: String) {
        binding.bottomNavigation.visibility = View.VISIBLE
    }
    private fun setUpNav(){
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.vaults -> replaceFragment(VaultsFragment())
                R.id.transactions -> replaceFragment(TransactionsFragment())
                R.id.analyze -> replaceFragment(AnalyzeFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.commit()
    }
}

//        binding.button.setOnClickListener {
//
//            CoroutineScope (Dispatchers.IO).launch {
//                val user = registrationApi.auth(
//                    AuthRequest(
//                        binding.username.text.toString(),
//                        binding.password.text.toString()
//                    )
//                )
//            }

