package jp.co.happy_goods

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import jp.co.happy_goods.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navView = binding.bottomNavigationView

        navController = findNavController(R.id.fragmentContainer)
        navView.setupWithNavController(navController)
        navController.navigate(R.id.homeFragment)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                }
                R.id.messageFragment -> {
                    navController.navigate(R.id.messageFragment)
                }
                R.id.plusFragment -> {
                    navController.navigate(R.id.plusFragment)
                }
                R.id.heartFragment -> {
                    navController.navigate(R.id.heartFragment)
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment)
                }

            }
            true
        }

        navView.selectedItemId = R.id.homeFragment
    }

    fun hideBottomNavi(status: Boolean) = if(status) binding.bottomNavigationView.visibility = View.GONE else
        binding.bottomNavigationView.visibility = View.VISIBLE
}