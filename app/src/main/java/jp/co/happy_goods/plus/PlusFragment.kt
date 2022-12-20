package jp.co.happy_goods.plus

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import jp.co.happy_goods.DBKey.Companion.DB_ITEMS
import jp.co.happy_goods.MainActivity
import jp.co.happy_goods.R
import jp.co.happy_goods.databinding.FragmentPlusBinding
import jp.co.happy_goods.home.ItemListModel

class PlusFragment : Fragment(R.layout.fragment_plus) {
    private lateinit var _context: MainActivity
    private lateinit var navController: NavController
    private var _binding: FragmentPlusBinding?= null
    private val binding get() = _binding!!
    private var selectedUri: Uri ?= null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    private val itemsDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ITEMS)
    }
    private val fireDatabase = FirebaseDatabase.getInstance().reference

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
        _context.hideBottomNavi(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlusBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.thumbnailButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(_context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                    startContextProvider()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)-> {
                    showPermissionContextPopup()
                }
                else -> {requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)}
            }
        }

        binding.submitButton.setOnClickListener {
            val title = binding.titleEditText.text
            val price = binding.priceEditText.text
            val sellerId = auth.currentUser?.uid.orEmpty()

            val model = ItemListModel(sellerId, "$title", System.currentTimeMillis(), "$price 원", "")
            itemsDB.push().setValue(model)

            Toast.makeText(requireContext(), "등록되었습니다", Toast.LENGTH_SHORT).show()

            navController = Navigation.findNavController(it)
            navController.navigate(R.id.action_plusFragment_to_homeFragment)
        }

        binding.backButton.setOnClickListener {
            navController = Navigation.findNavController(it)
            navController.navigate(R.id.action_plusFragment_to_homeFragment)
        }



        return root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1010 ->
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startContextProvider()
                }
            else -> Toast.makeText(_context, "권한을 거부하셨습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startContextProvider(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2020)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode != Activity.RESULT_OK){
            return
        }
        when (requestCode) {
            2020 -> {
                val uri = data?.data
                if (uri != null) {
                    binding.thumbnailImageView.setImageURI(uri)
                    binding.thumbnailButton.visibility = View.GONE
                    selectedUri = uri
                } else {
                    Toast.makeText(_context, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            else -> Toast.makeText(_context, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(_context)
            .setTitle("권한이 필요합니다")
            .setMessage("사진을 가져오기위해 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            }
            .create()
            .show()
    }

}