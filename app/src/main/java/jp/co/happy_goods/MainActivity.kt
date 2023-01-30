package jp.co.happy_goods

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import jp.co.happy_goods.databinding.ActivityMainBinding
import jp.co.happy_goods.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    lateinit var navController: NavController
    private val auth: FirebaseAuth by lazy{ Firebase.auth}
    private var backKeyPressedTime: Long = 0

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
                    val user = auth.currentUser
                    if (user?.email != null) {
                        navController.navigate(R.id.profileFragment)
                    } else {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            true
        }

        navView.selectedItemId = R.id.homeFragment
    }

    fun hideBottomNavi(status: Boolean) = if(status) binding.bottomNavigationView.visibility = View.GONE else
        binding.bottomNavigationView.visibility = View.VISIBLE

    /**
     * 端末の戻るボタン監視・
     */
    override fun onBackPressed() {
        //super.onBackPressed()
        // アプリ内の端末をバックさせるボタンをクリック防止
        if(System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis()
            return
        }
        // 2回クリックするとアプリ終了
        if(System.currentTimeMillis() <= backKeyPressedTime + 2500){
            finishAffinity()
        }
    }
}