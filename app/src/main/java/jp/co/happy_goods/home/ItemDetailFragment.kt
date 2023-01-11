package jp.co.happy_goods.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import jp.co.happy_goods.MainActivity
import jp.co.happy_goods.R
import jp.co.happy_goods.databinding.FragmentItemDetailBinding

class ItemDetailFragment: Fragment(R.layout.fragment_item_detail) {
    private lateinit var _context: MainActivity
    private lateinit var navController: NavController

    private var _binding: FragmentItemDetailBinding?= null
    private val binding get() = _binding!!


    private var sellerId = ""
    private var title = ""
    private var createAt = 0
    private var price = ""
    private var stock = ""
    private var description = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _context.hideBottomNavi(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _context.hideBottomNavi(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)

        val bundle = arguments
        bundle?.run {
            sellerId = this.getString("SELLER_ID").toString()
            title = this.getString("TITLE").toString()
            createAt = this.getLong("CREATED_AT").toInt()
            price = this.getString("PRICE").toString()
            stock = this.getString("DESCRIPTION").toString()
            description = this.getString("IMAGE_URL").toString()
        }

        binding.itemTitleTextView.text = title

        binding.backButton.setOnClickListener {
            navController = Navigation.findNavController(it)
            navController.navigate(R.id.action_itemDetailFragment_to_homeFragment)
        }



        return binding.root
    }

}