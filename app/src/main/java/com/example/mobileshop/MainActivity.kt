package com.example.mobileshop

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
import androidx.paging.PagingData
import com.example.mobileshop.api_recycler_view.Product
import com.example.mobileshop.db.DBState
import com.example.mobileshop.db.LocalImageEntity
import com.example.mobileshop.db.ProductWithLocalImages
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {


    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var productAdapter: ProductAdapter
    var refresh = false


    private val mainViewModel: MainViewModel by viewModels()

    private val permissionHelper: PermissionHelper = PermissionHelper(this)


    val emptyProduct = Product()

    private var recyclerViewData: PagingData<Product> = PagingData.empty()
    private lateinit var builder: AlertDialog.Builder

    //    private var localImageData: ProductWithLocalImages? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        builder = AlertDialog.Builder(this)
//        setContentView(binding.root)

        initApplication()


        permissionHelper.checkPermissionAvailability()
        permissionHelper.requestPermission()

        observeProductData(binding)

        binding.swipeRefresh.setOnRefreshListener {
            mainViewModel.getAllProducts(true)
            initRecyclerView(recyclerViewData)
            binding.swipeRefresh.isRefreshing = false

        }

        binding.includedMain.mainAppBar.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.infoButton -> showInfoDialog(builder)
                else -> false
            }
        }



        mainViewModel.getAllProducts(refresh)


    }

    private fun initViews() {
        with(binding) {

        }
    }

    private fun initObservers() {

    }

    override fun createBinding(): ActivityMainBinding {
//        return ActivityMainBinding.inflate(layoutInflater)
        return binding
    }


    private fun initApplication() {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val firstRun = sharedPreferences.getBoolean("FIRST_RUN", true)
        val switchBool = sharedPreferences.getBoolean("SWITCH_STATE", false)




        if (firstRun) {

            mainViewModel.getAllProducts(true)

            editor.apply {
                putBoolean("FIRST_RUN", false)
                putBoolean("SWITCH_STATE", false)
            }.apply()

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
                            )
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
        productList: List<Product>
    ) {
        productAdapter = ProductAdapter(productList) { product ->
            val intent = Intent(this, SingleViewActivity::class.java)
            intent.putExtra("singleItemData", product)
            startActivity(intent)
        }
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = productAdapter
        }
    }


}