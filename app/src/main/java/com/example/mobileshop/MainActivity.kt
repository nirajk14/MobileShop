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
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.mobileshop.db.DBState
import com.example.mobileshop.db.LocalImageEntity
import com.example.mobileshop.db.ProductEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var productAdapter: ProductAdapter
    var refresh= false


    private val mainViewModel: MainViewModel by viewModels()

    private val permissionHelper: PermissionHelper = PermissionHelper(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initApplication()


        permissionHelper.checkPermissionAvailability()
        permissionHelper.requestPermission()

        observeProductData(binding)

        binding.swipeRefresh.setOnRefreshListener {
            mainViewModel.getAllProducts(true)
            binding.swipeRefresh.isRefreshing = false

        }

        binding.mainAppBar.setOnMenuItemClickListener{
            // just change it to switch and use sharedPref
            //todo loadFromLocalFile() should be configured in adapter probably
            //probably not needed
            true

        }
        mainViewModel.getAllProducts(refresh)

    }

    private fun initApplication() {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor= sharedPreferences.edit()
        val firstRun =sharedPreferences.getBoolean("FIRST_RUN", true)
        val switchBool= sharedPreferences.getBoolean("SWITCH_STATE", false)
        val switch: SwitchCompat = binding.switch1
        switch.isChecked=switchBool

        switch.setOnCheckedChangeListener { _, b ->
            //TODO setUp function to change view

            editor.apply {
                putBoolean("SWITCH_STATE",b)
            }.apply()
        }


        if(firstRun){
            binding.mainAppBar.title="First run"
            println("First run")
            mainViewModel.getAllProducts(true)

            editor.apply {
                putBoolean("FIRST_RUN",false)
            }.apply()

        }
        else {
            binding.mainAppBar.title="Not first run"
        }



    }

    override fun onResume() {
        super.onResume()
        mainViewModel.getAllProducts(false)
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
                        initRecyclerView(it.data) //This part is now the problem
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
        val localImageBoolean= getLocalImageBoolean()
        val localImageData= getLocalImageData() as MutableMap
        productAdapter=ProductAdapter(productList, localImageBoolean, localImageData) { productEntity ->
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

    private fun getLocalImageData(): MutableMap<Int, String>? {

        var mapData: MutableMap<Int,String>? = mutableMapOf<Int, String>()
        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.RESUMED)  {

                mainViewModel.allLocalImageDataStateFlow.collectLatest {

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

                        is DBState.SuccessLocalImage-> {
                            println("Success")
                            binding.recyclerView.isVisible = true
                            binding.progressBar.isVisible=false
                            mapData =createMapFromDbData(it.data)
//                        productAdapter.setData(it.data)
                        }

                        else-> {
                            println("Empty")
                        }
                    }

                }

            }  // Should come from DB

        }
        return mapData

    }

    private fun createMapFromDbData(data: List<LocalImageEntity>): MutableMap<Int, String> {
        val urlMap = mutableMapOf<Int, String>()
        //TODO Code to create Map the int contains productId and String contains imageUrl which are both attributes of LocalImageEntity

        for (entity in data) {
            val productId = entity.productId as Int
            val imageUrl = entity.imageUrl as String
            if (!urlMap.containsKey(productId)) {
                urlMap[productId] = imageUrl
            }
        }

        return urlMap

    }

    private fun getLocalImageBoolean(): Boolean {
        return getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE).getBoolean("SWITCH_STATE",binding.switch1.isChecked)// Should come from Shared Preferences
    }
}