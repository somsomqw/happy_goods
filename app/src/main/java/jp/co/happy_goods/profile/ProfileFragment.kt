package jp.co.happy_goods.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.co.happy_goods.R
import jp.co.happy_goods.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var _binding: FragmentProfileBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root



        return root
    }
}