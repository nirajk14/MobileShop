package com.example.mobileshop.main_view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileshop.BaseActivity
import com.example.mobileshop.R
import com.example.mobileshop.databinding.ActivityMainBinding
import com.example.mobileshop.product_view.ProductViewActivity
import com.example.mobileshop.utils.FlowState
import com.example.mobileshop.utils.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {


    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val productAdapter = ProductPagingAdapter { product->
//        hideOtherViews()
        showProgressBar()

        val intent = Intent(this, ProductViewActivity::class.java)
        intent.putExtra("singleItemData", product.id)
//        lifecycleScope.launch { delay(100L)  }. invokeOnCompletion {
            startActivity(intent)
    }

    private fun hideOtherViews() {
        with(binding){
            recyclerView.hide()
        }
    }

    private fun showProgressBar() {
        binding.includedPB.progressBar.show()
    }


    private val mainViewModel: MainViewModel by viewModels()
    private val permissionHelper: PermissionHelper = PermissionHelper(this)
    private lateinit var builder: AlertDialog.Builder
    private var searchQuery: String? = null
    private var chipQuery: MutableList<String> = mutableListOf()
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        builder = AlertDialog.Builder(this)
        permissionHelper.requestPermission()
        initViews()
        initObservers()

    }

    override fun onResume() {
        super.onResume()
        binding.includedPB.progressBar.hide()
        binding.recyclerView.show()
    }

    private fun initViews() {
        with(binding) {
            includedPB.progressBar.hide()
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
//                   Contains debouncer implementation
                    searchJob?.cancel()
                    searchQuery=newText
                    searchJob= CoroutineScope(Dispatchers.Main).launch {
                        delay(1200)
                        observeProductData()
                    }

                    return true
                }
            })

            swipeRefresh.setOnRefreshListener {
               swipeRefresh.isRefreshing = false
            }
            with(includedMain){
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
                            chip.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked){
                                    chip.setChipBackgroundColorResource(R.color.orange)
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
                    else -> Timber.i("Something went wrong, for detailed debugging add failure and loading states")
                }
            }

        }
    }
//todo expandable fab notif stopcount workmanager
    override fun createBinding(): ActivityMainBinding {
        return binding
    }
    private fun observeProductData() {
        lifecycleScope.launch {
            Timber.i("this is a search %s",searchQuery)
            mainViewModel.paginatedProduct(true, searchQuery, chipQuery).collectLatest {
                productAdapter.submitData(lifecycle,it)
                }
            }
        }

}