package jp.co.happy_goods.login

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import jp.co.happy_goods.MainActivity
import jp.co.happy_goods.data.DBKey.Companion.DB_USERS
import jp.co.happy_goods.data.UserModel
import jp.co.happy_goods.databinding.ActivityLoginBinding
import jp.co.happy_goods.profile.ProfileFragment
import jp.co.happy_goods.utility.SharedPref.updateLoginEmail


class LoginActivity : AppCompatActivity()  {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userDB: DatabaseReference
    private val auth: FirebaseAuth by lazy{ Firebase.auth}
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory()).get(LoginViewModel::class.java)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inputEmail = binding.emailEditText
        val inputPassword = binding.passwordEditText

        val rootLayout: View = findViewById(android.R.id.content)

        inputEmail.doAfterTextChanged {
            loginViewModel.loginDataChanged(
                email = inputEmail.text.toString(),
                password = inputPassword.text.toString()
            )
        }

        inputPassword.doAfterTextChanged {
            loginViewModel.loginDataChanged(
                email = inputEmail.text.toString(),
                password = inputPassword.text.toString()
            )
        }

        binding.signUpButton.setOnClickListener {
            signUpButton(it)
        }

        binding.loginButton.setOnClickListener {
            signInOutButton(it)
        }

    }

    private fun signUpButton(view: View){
        userDB = Firebase.database.reference.child(DB_USERS)

        auth.createUserWithEmailAndPassword(loginViewModel.email, loginViewModel.password)
            .addOnCompleteListener(this@LoginActivity) { task ->
                if(task.isSuccessful){
                    val userInfo = UserModel(
                        email = loginViewModel.email,
                        password = loginViewModel.password,
                        profileImageUrl = ""
                    )
                    userDB.child(auth.currentUser!!.uid)
                        .push()
                        .setValue(userInfo)

                    Snackbar.make(view, "회원가입에 성공했습니다", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(view, "회원가입에 실패했습니다", Snackbar.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInOutButton(view: View){
        auth.signInWithEmailAndPassword(loginViewModel.email, loginViewModel.password)
            .addOnCompleteListener(this@LoginActivity){task ->
                if(task.isSuccessful){
                    updateLoginEmail(this@LoginActivity, loginViewModel.email)
                    Snackbar.make(view, "로그인에 성공했습니다", Snackbar.LENGTH_SHORT).show()
                    successSineIn()
                } else {
                    Snackbar.make(view, "로그인에 실패했습니다", Snackbar.LENGTH_SHORT).show()
                }
            }
    }

    private fun successSineIn() {
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}