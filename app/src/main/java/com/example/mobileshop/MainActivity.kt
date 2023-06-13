package com.example.mobileshop

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileshop.ApiRecyclerView.ApiState
import com.example.mobileshop.databinding.ActivityMainBinding
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.mobileshop.db.AppDatabase
import com.example.mobileshop.db.DBState
import com.example.mobileshop.db.ProductEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var productAdapter: ProductAdapter
    var refresh= false


//    var appDb = AppDatabase.getDatabase(this)
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeProductData(binding)


        binding.swipeRefresh.setOnRefreshListener {
            mainViewModel.getAllProducts(true)
            binding.swipeRefresh.isRefreshing = false

        }


        mainViewModel.getAllProducts(refresh)

    }

    private fun observeProductData(binding: ActivityMainBinding) {
        println("observeProductData")
        lifecycleScope.launch {

            //launch when X is deprecated hence use .launch{ and then put repeatOnLifecycle(STATE){ Put code here }}
            repeatOnLifecycle(Lifecycle.State.STARTED){
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
        productAdapter=ProductAdapter(productList) { position ->
            val clickedItem = productAdapter.mList[position]
            val intent = Intent(this, SingleView::class.java)
            intent.putExtra("singleItemData", clickedItem)
            startActivity(intent)
        }
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager= LinearLayoutManager(this@MainActivity)
            adapter=productAdapter
        }
    }
}