package com.example.mobileshop

import android.content.Context
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
import kotlinx.coroutines.flow.collectLatest
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

        initApplication()


        checkPermissionAvailability()
        requestPermission()

        observeProductData(binding)

        binding.swipeRefresh.setOnRefreshListener {
            mainViewModel.getAllProducts(true)
            binding.swipeRefresh.isRefreshing = false

        }

        binding.mainAppBar.setOnMenuItemClickListener{
            // just change it to switch and use sharedPref
            //todo loadfromLocalFile() should be configured in adapter probably
            true

        }
        mainViewModel.getAllProducts(refresh)

    }

    private fun initApplication() {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor= sharedPreferences.edit()
        var firstRun =sharedPreferences.getBoolean("FIRST_RUN", true)
        var switchBool= sharedPreferences.getBoolean("SWITCH_STATE", false)

        if(firstRun){
            binding.mainAppBar.title="First run"
            println("FIrst run")
            mainViewModel.getAllProducts(true)

            editor.apply {
                putBoolean("FIRST_RUN",false)
            }.apply()

        }
        else {
            binding.mainAppBar.title="Not first run"
        }



    }

    private fun checkPermissionAvailability() {
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
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.getAllProducts(false)
    }

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

        lifecycleScope.launch {

            //launch when X is deprecated hence use .launch{ and then put repeatOnLifecycle(STATE){ Put code here }}
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                mainViewModel.getAllProducts(false)

                mainViewModel.productDataStateFlow.collectLatest{

                when (it) {
                    is DBState.Loading->{
                        println("Loading")
                        binding.recyclerView.isVisible = false
                        binding.progressBar.isVisible = true
                    }
                    is DBState.Failure-> {
                        println("Failure")
                        binding.recyclerView.isVisible = false
                        binding.progressBar.isVisible = false
                        Log.d("HEHE YOU GOT AN ERROR", "GET REKT IT'S DB CALL ${it.msg}")

                    }

                    is DBState.SuccessProduct-> {
                        println("Success")
                        binding.recyclerView.isVisible = true
                        binding.progressBar.isVisible=false
                        initRecyclerView(it.data)
//                        productAdapter.setData(it.data)
                    }

                    else-> {
                        println("Empty")
                    }
                }
            }
            }

        }
    }


    private fun initRecyclerView(productList: List<ProductEntity>) {
//        val localImageBoolean= getLocalImageBoolean()
//        val localImageData= getLocalImageData()
        productAdapter=ProductAdapter(productList) { productEntity ->
            val intent = Intent(this, SingleViewActivity::class.java)
            intent.putExtra("singleItemData", productEntity)
            startActivity(intent)
        }
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager= LinearLayoutManager(this@MainActivity)
            adapter=productAdapter
        }
    }

//    private fun getLocalImageData(): Any {
//        TODO("Not yet implemented") // Should come from DB
//
//    }
//
//    private fun getLocalImageBoolean(): Any {
//        TODO("Not yet implemented") // Should come from Shared Preferences
//    }
}