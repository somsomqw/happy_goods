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
import jp.co.happy_goods.R
import jp.co.happy_goods.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var itemListAdapter : ItemListAdapter
    private lateinit var articleDB : DatabaseReference

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

    private var numBanner = 3 // 배너 갯수
    private var currentPosition = Int.MAX_VALUE / 2
    private var myHandler = MyHandler()
    private val intervalTime = 3000.toLong() // 몇초 간격으로 페이지를 넘길것인지 (1500 = 1.5초)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 배너 뷰페이저
        binding.bannerImg.adapter = ViewPagerAdapter(getBannerList(), requireContext())// 어댑터 생성
        binding.bannerImg.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
        binding.bannerImg.setCurrentItem(currentPosition, false) // 현재 위치를 지정

        binding.textViewTotalBanner.text = numBanner.toString()

        binding.bannerImg.apply {
            registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.textViewCurrentBanner.text = "${(position%3)+1}" // position이 0부터 시작해서 +1
                }
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    when (state) {
                        // 뷰페이저에서 손 떼었을때 / 뷰페이저 멈춰있을 때
                        ViewPager2.SCROLL_STATE_IDLE -> autoScrollStart(intervalTime)
                        // 뷰페이저 움직이는 중
                        ViewPager2.SCROLL_STATE_DRAGGING -> autoScrollStop()
                    }
                }
            })
        }

        binding.linearLayoutSeeAll.setOnClickListener {
            Toast.makeText(context, "모두 보기 클릭했음", Toast.LENGTH_SHORT).show()
        }

        // 아이템 리사이클러뷰
        itemList.clear()
        articleDB = Firebase.database.reference.child(DB_ITEMS)

        itemListAdapter = ItemListAdapter()
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
            Toast.makeText(context, "검색버튼클릭", Toast.LENGTH_SHORT).show()
        }

        // navigation drawer
        val menuDrawer = toolbar.menuIcon
        menuDrawer.setOnClickListener {
            Toast.makeText(context, "메뉴버튼클릭", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        itemListAdapter.notifyDataSetChanged()
        // 다른 페이지로 떠나있는 동안 스크롤이 동작할 필요는 없음. 정지
        autoScrollStart(intervalTime)
    }
    override fun onPause() {
        super.onPause()
        // 다른 페이지로 떠나있는 동안 스크롤이 동작할 필요는 없음. 정지
        autoScrollStop()
    }
    override fun onDestroyView() {
        super.onDestroyView()

        articleDB.removeEventListener(listener)
    }

    // 배너 뷰페이저에 들어갈 이미지
    private fun getBannerList(): ArrayList<Int>{
        return arrayListOf<Int>(R.drawable.ic_home, R.drawable.ic_message, R.drawable.ic_add_circle )
    }
    private fun autoScrollStart(intervalTime: Long) {
        myHandler.removeMessages(0) // 이거 안하면 핸들러가 1개, 2개, 3개 ... n개 만큼 계속 늘어남
        myHandler.sendEmptyMessageDelayed(0, intervalTime) // intervalTime 만큼 반복해서 핸들러를 실행하게 함
    }

    private fun autoScrollStop(){
        myHandler.removeMessages(0) // 핸들러를 중지시킴
    }

    private inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if(msg.what == 0) {
                binding.bannerImg.setCurrentItem(++currentPosition, true) // 다음 페이지로 이동
                autoScrollStart(intervalTime) // 스크롤을 계속 이어서 한다.
            }
        }
    }
}