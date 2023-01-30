package jp.co.happy_goods.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import jp.co.happy_goods.MainActivity
import jp.co.happy_goods.R
import jp.co.happy_goods.databinding.FragmentProfileBinding
import jp.co.happy_goods.utility.SharedPref.getLoginEmail

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var _binding: FragmentProfileBinding?= null
    private val binding get() = _binding!!
    private val auth: FirebaseAuth by lazy{ Firebase.auth}
    private lateinit var _context: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        createUi()



        return binding.root
    }

    private fun createUi() {
        binding.userId.text = getLoginEmail(_context)
    }
}