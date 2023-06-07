package com.example.mobileshop

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
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var productAdapter: ProductAdapter
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecyclerView()

        mainViewModel.getProducts()
        lifecycleScope.launchWhenStarted {
            mainViewModel.productStateFlow.collect{
                when (it) {
                    is ApiState.Loading->{
                        binding.recyclerView.isVisible = false
                        binding.progressBar.isVisible = true
                    }
                    is ApiState.Failure-> {
                        binding.recyclerView.isVisible = false
                        binding.progressBar.isVisible = false
                        Log.d("HEHE YOU GOT AN ERROR", "GET REKT IT'S API CALL ${it.msg}")

                    }

                    is ApiState.Success-> {
                        binding.recyclerView.isVisible = true
                        binding.progressBar.isVisible=false
                        productAdapter.setData(it.data.products)
                    }

                    is ApiState.Empty-> {

                    }
                }
            }
        }

    }

    private fun initRecyclerView() {
        productAdapter=ProductAdapter(ArrayList(), {products, position ->
            val clickedItem=productAdapter._mList[position]
            val intent= Intent(this,SingleView::class.java)
            intent.putExtra("singleItemData", clickedItem)
            startActivity(intent)
        } )
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager= LinearLayoutManager(this@MainActivity)
            adapter=productAdapter
        }
    }
}