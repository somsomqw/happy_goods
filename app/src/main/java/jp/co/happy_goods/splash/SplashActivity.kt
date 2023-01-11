package jp.co.happy_goods.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import jp.co.happy_goods.MainActivity
import jp.co.happy_goods.R
import jp.co.happy_goods.databinding.ActivitySplashBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)
        val lottieView = binding.splashImageView
        val animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                lottieView.startAnimationAsync(animationFadeIn, 0) //  Fade-Inを実行
                lottieView.startAnimationAsync(animationFadeOut, 1) // Fade-Outを実行
            }
        }
    }

    private fun moveToLogin() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private suspend fun View.startAnimationAsync(anim: Animation, flag: Int) {
        return suspendCoroutine { continuation ->
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    if (flag != 0) {
                        moveToLogin()
                    }
                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (flag == 0) {
                        continuation.resume(Unit)
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }
            })
            this.startAnimation(anim)
        }
    }
}