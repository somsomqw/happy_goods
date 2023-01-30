package jp.co.happy_goods.plus

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import jp.co.happy_goods.data.DBKey.Companion.DB_ITEMS
import jp.co.happy_goods.MainActivity
import jp.co.happy_goods.R
import jp.co.happy_goods.databinding.FragmentPlusBinding
import jp.co.happy_goods.data.ItemListModel

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
        _binding = null
        _context.hideBottomNavi(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlusBinding.inflate(inflater, container, false)

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

        binding.saveButton.setOnClickListener {
            Toast.makeText(requireContext(), "임시저장 되었습니다", Toast.LENGTH_SHORT).show()
        }

        binding.submitButton.setOnClickListener {
            val sellerId = auth.currentUser?.uid.orEmpty()
            val title = binding.titleEditText.text.toString()
            val price = binding.priceEditText.text.toString()
            val stock = Integer.parseInt(binding.stockEditText.text.toString())
            val description = binding.descriptionEditText.text.toString()
            val bank = binding.bankEditText.text.toString()
            val account = binding.accountEditText.text.toString()
            val accountName = binding.accountNameEditText.text.toString()

            // 이미지가 있으면 업로드과정 추가
            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener
                uploadPhoto(photoUri,
                        successHandler = {uri ->
                            uploadItem(it, sellerId, title, price, stock, description, uri, bank, account, accountName)
                        },
                        errorHandler = {
                            Toast.makeText(requireContext(), "사진 업로드에 실패했습니다", Toast.LENGTH_SHORT).show()
                        }
                    )
            } else {
                uploadItem(it, sellerId, title, price, stock, description, "", bank, account, accountName)
            }
        }

        binding.backButton.setOnClickListener {
            navController = Navigation.findNavController(it)
            navController.navigate(R.id.action_plusFragment_to_homeFragment)
        }

        return binding.root
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

        if(requestCode == Activity.RESULT_OK){
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

    private fun uploadPhoto(uri: Uri, successHandler:(String) ->Unit, errorHandler:() ->Unit) {
        val fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("items/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    storage.reference.child("items/photo").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                        }.addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    errorHandler()
                }
            }
    }

    private fun uploadItem(view: View, sellerId: String, title: String, price: String, stock: Int, description:String, imageUri:String, bank:String, account:String, accountName:String) {
        val model = ItemListModel(sellerId, title, System.currentTimeMillis(), "$price 원", stock,
            description, imageUri, bank, account, accountName)
        itemsDB.push().setValue(model)

        Toast.makeText(requireContext(), "등록되었습니다", Toast.LENGTH_SHORT).show()

        navController = Navigation.findNavController(view)
        navController.navigate(R.id.action_plusFragment_to_homeFragment)
    }

}