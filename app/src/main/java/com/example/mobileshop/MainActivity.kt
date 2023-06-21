package com.example.mobileshop

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.mobileshop.db.DBState
import com.example.mobileshop.db.LocalImageEntity
import com.example.mobileshop.db.ProductEntity
import com.example.mobileshop.db.ProductWithLocalImages
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var productAdapter: ProductAdapter
    var refresh = false


    private val mainViewModel: MainViewModel by viewModels()

    private val permissionHelper: PermissionHelper = PermissionHelper(this)


    val emptyProductEntity = ProductEntity()

    private var recyclerViewData: List<ProductEntity> = listOf(emptyProductEntity)
    private var localImageMap: MutableMap<Int, String> = mutableMapOf()
    private lateinit var builder: AlertDialog.Builder

    //    private var localImageData: ProductWithLocalImages? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        builder = AlertDialog.Builder(this)

//        val animationDrawable = binding.constraintMain.background as AnimationDrawable
//
//        animationDrawable.apply {
//            setEnterFadeDuration(2500)
//            setExitFadeDuration(5000)
//            start()
//        }

        initApplication()
        observeLocalData()


        permissionHelper.checkPermissionAvailability()
        permissionHelper.requestPermission()

        observeProductData(binding)

        binding.swipeRefresh.setOnRefreshListener {
            mainViewModel.getAllProducts(true)
            initRecyclerView(recyclerViewData, localImageMap)
            binding.swipeRefresh.isRefreshing = false

        }

        binding.mainAppBar.setOnMenuItemClickListener{menuItem->
            when(menuItem.itemId){
                R.id.infoButton-> {
                    builder.setTitle("This app was created by")
                        .setMessage("Niraj Kushwaha")
                        .setPositiveButton("OK") {dialogInterface, it->
                            dialogInterface.cancel()
                        }.show()
                    true}
                else-> false
            }
        }


        mainViewModel.getAllProducts(refresh)


    }

    private fun observeLocalData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mainViewModel.localImageStateFlow.collectLatest {
                    when (it) {
                        is DBState.Loading -> {
                            println("Loading local data")
                        }

                        is DBState.Failure -> {
                            println("local data failed ${it.msg}")
                        }

                        is DBState.SuccessProductWithLocalImage -> {
                            println("We got local image recycler view should reinit")
                            if (it.data != null) {
                                localImageMap[it.data.productEntity.id] =
                                    it.data.localImageEntities[0].imageUrl.toString()
                                initRecyclerView(recyclerViewData, localImageMap)
                            }
                        }

                        else -> {
                            println("local data Empty")
                        }
                    }
                }
            }

        }
    }

    private fun initApplication() {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val firstRun = sharedPreferences.getBoolean("FIRST_RUN", true)
        val switchBool = sharedPreferences.getBoolean("SWITCH_STATE", false)




        if (firstRun) {
            binding.mainAppBar.title = "First run"
            println("First run")
            mainViewModel.getAllProducts(true)

            editor.apply {
                putBoolean("FIRST_RUN", false)
                putBoolean("SWITCH_STATE", false)
            }.apply()

        } else {
            binding.mainAppBar.title = "Not first run"
        }


    }

    override fun onResume() {
        super.onResume()
        mainViewModel.getAllProducts(false)
    }

    private fun observeProductData(binding: ActivityMainBinding) {

        lifecycleScope.launch {

            //launch when X is deprecated hence use .launch{ and then put repeatOnLifecycle(STATE){ Put code here }}
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mainViewModel.getAllProducts(false)

                mainViewModel.productDataStateFlow.collectLatest {

                    when (it) {
                        is DBState.Loading -> {
                            println("Loading")
                            binding.recyclerView.isVisible = false
                            binding.progressBar.isVisible = true
                        }

                        is DBState.Failure -> {
                            println("Failure")
                            binding.recyclerView.isVisible = false
                            binding.progressBar.isVisible = false
                            Log.d("HEHE YOU GOT AN ERROR", "GET REKT IT'S DB CALL ${it.msg}")

                        }

                        is DBState.SuccessProduct -> {
                            println("Success")
                            binding.recyclerView.isVisible = true
                            binding.progressBar.isVisible = false

                            recyclerViewData = it.data
                            initRecyclerView(
                                recyclerViewData,
                                localImageMap
                            ) //This part is now the problem
//                        productAdapter.setData(it.data)
                        }

                        else -> {
                            println("Empty")
                        }
                    }
                }
            }

        }
    }


    private fun initRecyclerView(
        productList: List<ProductEntity>,
        localImageMap: MutableMap<Int, String>
    ) {
        productAdapter = ProductAdapter(productList, localImageMap) { productEntity ->
            val intent = Intent(this, SingleViewActivity::class.java)
            intent.putExtra("singleItemData", productEntity)
            startActivity(intent)
        }
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = productAdapter
        }
    }


}