package jp.co.happy_goods.home

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import jp.co.happy_goods.DBKey.Companion.DB_ITEMS
import jp.co.happy_goods.MainActivity
import jp.co.happy_goods.R
import jp.co.happy_goods.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var itemListAdapter : ItemListAdapter
    private lateinit var articleDB : DatabaseReference
    private lateinit var navController: NavController

    private val itemList = mutableListOf<ItemListModel>()
    private val listener = object: ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val articleModel = snapshot.getValue(ItemListModel::class.java)
            articleModel ?: return

            itemList.add(articleModel)
            itemListAdapter.submitList(itemList)
        }
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}
    }

    private var _binding: FragmentHomeBinding ?= null
    private val binding get() = _binding!!
    private val auth: FirebaseAuth by lazy{ Firebase.auth}

    private var numBanner = 3 // ?????? ??????
    private var currentPosition = Int.MAX_VALUE / 2
    private var myHandler = MyHandler()
    private val intervalTime = 3000.toLong() // ?????? ???????????? ???????????? ??????????????? (1500 = 1.5???)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // ?????? ????????????
        binding.bannerImg.adapter = ViewPagerAdapter(getBannerList(), requireContext())// ????????? ??????
        binding.bannerImg.orientation = ViewPager2.ORIENTATION_HORIZONTAL // ????????? ?????????
        binding.bannerImg.setCurrentItem(currentPosition, false) // ?????? ????????? ??????

        binding.textViewTotalBanner.text = numBanner.toString()

        binding.bannerImg.apply {
            registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.textViewCurrentBanner.text = "${(position%3)+1}" // position??? 0?????? ???????????? +1
                }
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    when (state) {
                        // ?????????????????? ??? ???????????? / ???????????? ???????????? ???
                        ViewPager2.SCROLL_STATE_IDLE -> autoScrollStart(intervalTime)
                        // ???????????? ???????????? ???
                        ViewPager2.SCROLL_STATE_DRAGGING -> autoScrollStop()
                    }
                }
            })
        }

        binding.linearLayoutSeeAll.setOnClickListener {
            Toast.makeText(context, "?????? ?????? ????????????", Toast.LENGTH_SHORT).show()
        }

        // ????????? ??????????????????
        itemList.clear()
        articleDB = Firebase.database.reference.child(DB_ITEMS)

        itemListAdapter = ItemListAdapter(onItemClicked = {
            Toast.makeText(context, it.title, Toast.LENGTH_SHORT).show()

            navController = Navigation.findNavController(root)
            navController.navigate(R.id.action_homeFragment_to_itemDetailFragment, makeBundle(it))
        })
//        itemListAdapter.submitList(mutableListOf<ItemListModel>().apply {
//            add(ItemListModel("0", "aaa", 100000, "5000", ""))
//            add(ItemListModel("1", "bbb", 200000, "2000", ""))
//        })
        binding.itemRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.itemRecyclerView.adapter = itemListAdapter

        articleDB.addChildEventListener(listener)

        // search Icon eventListener
        val toolbar = binding.toolbarLayout
        val searchIcon = toolbar.searchIcon
        searchIcon.setOnClickListener {
            Toast.makeText(context, "??????????????????", Toast.LENGTH_SHORT).show()
        }

        // navigation drawer
        val menuDrawer = toolbar.menuIcon
        menuDrawer.setOnClickListener {
            Toast.makeText(context, "??????????????????", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        itemListAdapter.notifyDataSetChanged()
        // ?????? ???????????? ???????????? ?????? ???????????? ????????? ????????? ??????. ??????
        autoScrollStart(intervalTime)
    }
    override fun onPause() {
        super.onPause()
        // ?????? ???????????? ???????????? ?????? ???????????? ????????? ????????? ??????. ??????
        autoScrollStop()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        articleDB.removeEventListener(listener)
    }

    // ?????? ??????????????? ????????? ?????????
    private fun getBannerList(): ArrayList<Int>{
        return arrayListOf<Int>(R.drawable.ic_home, R.drawable.ic_message, R.drawable.ic_add_circle )
    }
    private fun autoScrollStart(intervalTime: Long) {
        myHandler.removeMessages(0) // ?????? ????????? ???????????? 1???, 2???, 3??? ... n??? ?????? ?????? ?????????
        myHandler.sendEmptyMessageDelayed(0, intervalTime) // intervalTime ?????? ???????????? ???????????? ???????????? ???
    }

    private fun autoScrollStop(){
        myHandler.removeMessages(0) // ???????????? ????????????
    }

    private inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if(msg.what == 0) {
                binding.bannerImg.setCurrentItem(++currentPosition, true) // ?????? ???????????? ??????
                autoScrollStart(intervalTime) // ???????????? ?????? ????????? ??????.
            }
        }
    }

    private fun makeBundle(it:ItemListModel): Bundle {
        val bundle = Bundle()
        bundle.putString("SELLER_ID", it.sellerId)
        bundle.putString("TITLE", it.title)
        bundle.putLong("CREATED_AT", it.createdAt)
        bundle.putString("PRICE", it.price)
        bundle.putInt("STOCK", it.stock)
        bundle.putString("DESCRIPTION", it.description)
        bundle.putString("IMAGE_URL", it.imageUrl)
        return bundle
    }
}