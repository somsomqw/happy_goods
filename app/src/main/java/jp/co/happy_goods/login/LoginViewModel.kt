package jp.co.happy_goods.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.co.orix.credit.nextage.utils.toEmptyString

class LoginViewModel : ViewModel() {

    private val _signUpResult = MutableLiveData<Boolean>()
    val signUpState: LiveData<Boolean> = _signUpResult

    private val _realTimeEmail = MutableLiveData<String>()
    val realTimeEmail: LiveData<String> = _realTimeEmail

    var email: String = ""
        private set
    var password: String = ""
        private set

    fun loginDataChanged(
        email: String? = null,
        password: String? = null
    ){
        this.email = email.toEmptyString()
        this.password = password.toEmptyString()


    }


    /**
     * email必須チェック
     */
    private fun isEmailValid(email: String): Boolean {
        return email.length > 5
    }
}