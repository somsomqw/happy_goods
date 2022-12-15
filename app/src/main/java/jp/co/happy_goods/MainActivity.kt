package jp.co.happy_goods

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import jp.co.happy_goods.heart.HeartFragment
import jp.co.happy_goods.home.HomeFragment
import jp.co.happy_goods.message.MessageFragment
import jp.co.happy_goods.plus.PlusFragment
import jp.co.happy_goods.profile.ProfileFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
        val messageFragment = MessageFragment()
        val plusFragment = PlusFragment()
        val heartFragment = HeartFragment()
        val profileFragment = ProfileFragment()


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        replaceFragment(homeFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.homeFragment -> replaceFragment(homeFragment)
                R.id.messageFragment -> replaceFragment(messageFragment)
                R.id.plusFragment -> replaceFragment(plusFragment)
                R.id.heartFragment -> replaceFragment(heartFragment)
                R.id.profileFragment -> replaceFragment(profileFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainer, fragment)
                commit()
            }
    }
}