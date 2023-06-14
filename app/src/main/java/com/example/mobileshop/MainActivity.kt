package com.example.mobileshop

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileshop.databinding.ActivityMainBinding
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.mobileshop.db.DBState
import com.example.mobileshop.db.ProductEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false
    private var isWritePermissionGranted=false
    private var isManagePermissionGranted=false


    private lateinit var binding: ActivityMainBinding
    private lateinit var productAdapter: ProductAdapter
    var refresh= false


//    var appDb = AppDatabase.getDatabase(this)
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        permissionLauncher= registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                permissions ->
            isReadPermissionGranted = permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE]?: isReadPermissionGranted
            isWritePermissionGranted = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]?: isWritePermissionGranted
            isManagePermissionGranted = permissions[if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
            } else {
                TODO("VERSION.SDK_INT < R")
            }]?: isManagePermissionGranted


        }

        requestPermission()

        observeProductData(binding)


        binding.swipeRefresh.setOnRefreshListener {
            mainViewModel.getAllProducts(true)
            binding.swipeRefresh.isRefreshing = false

        }




        mainViewModel.getAllProducts(refresh)

    }

//    override fun onResume() {
//        super.onResume()
//        mainViewModel.getAllProducts(false)
//    }

    private fun requestPermission() {
        isReadPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        isWritePermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            isManagePermissionGranted = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }



        val permissionRequest: MutableList<String> = ArrayList()

        if (!isReadPermissionGranted){
            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!isWritePermissionGranted){
            permissionRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!isManagePermissionGranted){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                permissionRequest.add(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            }
        }


        if (permissionRequest.isNotEmpty()){
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    private fun observeProductData(binding: ActivityMainBinding) {
        println("observeProductData")
        lifecycleScope.launch {

            //launch when X is deprecated hence use .launch{ and then put repeatOnLifecycle(STATE){ Put code here }}
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                mainViewModel.productDataStateFlow.collect{
                when (it) {
                    is DBState.Loading->{
                        binding.recyclerView.isVisible = false
                        binding.progressBar.isVisible = true
                    }
                    is DBState.Failure-> {
                        binding.recyclerView.isVisible = false
                        binding.progressBar.isVisible = false
                        Log.d("HEHE YOU GOT AN ERROR", "GET REKT IT'S DB CALL ${it.msg}")

                    }

                    is DBState.Success-> {
                        binding.recyclerView.isVisible = true
                        binding.progressBar.isVisible=false
                        initRecyclerView(it.data)
//                        productAdapter.setData(it.data)
                    }

                    is DBState.Empty-> {

                    }
                }
            }
            }

        }
    }


    private fun initRecyclerView(productList: List<ProductEntity>) {
        productAdapter=ProductAdapter(productList) { product ->
            val intent = Intent(this, SingleView::class.java)
            intent.putExtra("singleItemData", product)
            startActivity(intent)
        }
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager= LinearLayoutManager(this@MainActivity)
            adapter=productAdapter
        }
    }
}