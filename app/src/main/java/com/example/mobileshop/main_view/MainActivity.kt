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
import com.example.mobileshop.BaseActivity
import com.example.mobileshop.utils.PermissionHelper
import com.example.mobileshop.R
import com.example.mobileshop.product_view.ProductViewActivity
import dagger.hilt.android.AndroidEntryPoint
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

            searchView.clearFocus()
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }


                override fun onQueryTextChange(newText: String?): Boolean {
                    searchQuery=newText
                    observeProductData()
//                    recyclerView.scrollToPosition(0)
                    return true
                }
            })

            swipeRefresh.setOnRefreshListener {
               swipeRefresh.isRefreshing = false
            }
            with(includedMain){
            with(backButton) {
                visibility=View.GONE
            }
            mainAppBar.setOnMenuItemClickListener{ menuItem ->
                when (menuItem.itemId) {
                    R.id.infoButton -> showInfoDialog(builder)
                    else -> false
                }
            }

            }

        }
    }
    private fun initObservers() {
        observeProductData()

    }
    override fun createBinding(): ActivityMainBinding {
        return binding
    }
    private fun observeProductData() {
        lifecycleScope.launch {
            mainViewModel.paginatedProduct(true, searchQuery).collectLatest {
                productAdapter.submitData(lifecycle,it)
                binding.recyclerView.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    adapter = productAdapter

                }
            }
        }
    }
}