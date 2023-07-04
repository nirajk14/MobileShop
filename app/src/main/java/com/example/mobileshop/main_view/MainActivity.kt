package com.example.mobileshop.main_view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileshop.databinding.ActivityMainBinding
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.RecyclerView
import androidx.room.util.query
import com.example.mobileshop.BaseActivity
import com.example.mobileshop.utils.PermissionHelper
import com.example.mobileshop.R
import com.example.mobileshop.product_view.ProductViewActivity
import com.example.mobileshop.utils.FlowState
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {


    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val productAdapter = ProductPagingAdapter { product->
        val intent = Intent(this, ProductViewActivity::class.java)
        intent.putExtra("singleItemData", product.id)
        startActivity(intent)
    }


    private val mainViewModel: MainViewModel by viewModels()
    private val permissionHelper: PermissionHelper = PermissionHelper(this)
    private lateinit var builder: AlertDialog.Builder
    private var searchQuery: String? = null
    private var chipQuery: MutableList<String> = mutableListOf()
    private var searchJob: Job? = null

    private val queryTextChanges: MutableSharedFlow<String?> = MutableSharedFlow()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setSupportActionBar(findViewById(R.id.mainAppBar))
        builder = AlertDialog.Builder(this)
        permissionHelper.requestPermission()
        initViews()
        initObservers()
    }

    private fun initViews() {
        with(binding) {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = productAdapter

            }

            searchView.clearFocus()
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }


                override fun onQueryTextChange(newText: String?): Boolean {
//                    setSearchQuery()
                    searchJob?.cancel()
                    searchQuery=newText
                    searchJob= CoroutineScope(Dispatchers.Main).launch {
                        delay(1200)
                        observeProductData()
                    }


//                    recyclerView.scrollToPosition(0)
                    return true
                }
            })

            swipeRefresh.setOnRefreshListener {
               swipeRefresh.isRefreshing = false
            }
            with(includedMain){
//            with(backButton) {
//                visibility=View.GONE
//                //done use extension function for visibility
//            }
                backButton.hide()
            mainAppBar.setOnMenuItemClickListener{ menuItem ->
                when (menuItem.itemId) {
                    R.id.infoButton -> showInfoDialog(builder)
                    R.id.camera -> {
                        permissionHelper.requestCameraPermission()
                        true
                    }
                    else -> false
                }
            }

            }

        }
    }
    private fun initObservers() {
        observeProductData()
        observeChipGroupData()

    }

    //todo sonarlint

    private fun observeChipGroupData() {
        lifecycleScope.launch {
            mainViewModel.getCategory()
            mainViewModel.productCategoryStateFlow.collectLatest {
                //Initialize chip group here
                when(it){
                    is FlowState.SuccessProductCategory-> {
                        for(chipData in it.category){
                            val chip = createChip(chipData)
                            chip.isCheckable=true
                            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                                if (isChecked){
                                    chip.setChipBackgroundColorResource(R.color.orange)
                                    println(chip.text)
                                    chipQuery.add(chip.text as String)
                                    observeProductData()
                                }
                                else{
                                    chip.setChipBackgroundColorResource(R.color.gray)
                                    chipQuery.remove(chip.text as String)
                                    observeProductData()
                                }
                            }
                            binding.chipGroupMain.addView(chip)
                        }
                    }
                    else -> println("Something went wrong, for detailed debugging add failure and loading states")
                }
            }

        }
    }
//todo expandable fab notif stopcount workmanager
    override fun createBinding(): ActivityMainBinding {
        return binding
    }
    //todo use timber instead of println
    private fun observeProductData() {
        lifecycleScope.launch {
            println(searchQuery)
            mainViewModel.paginatedProduct(true, searchQuery, chipQuery).collectLatest {
                productAdapter.submitData(lifecycle,it)
                }
            }
        }

}