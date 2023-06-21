package com.example.mobileshop

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileshop.databinding.ActivitySingleViewBinding
import com.example.mobileshop.db.DBState
import com.example.mobileshop.db.LocalImageEntity
import com.example.mobileshop.db.ProductEntity
import com.example.mobileshop.db.ProductWithLocalImages
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SingleViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingleViewBinding
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivitySingleViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val animationDrawable = binding.constraintSingle.background as AnimationDrawable
//
//        animationDrawable.apply {
//            setEnterFadeDuration(2500)
//            setExitFadeDuration(5000)
//            start()
//        }

        var product= intent.getSerializableExtra("singleItemData") as ProductEntity
        if (product!=null){
            binding.txtView.text=product.title.toString()
            binding.txtView1.text=product.brand.toString()
            binding.txtView2.text=product.category.toString()
            binding.txtView3.text=product.description.toString()
            Picasso.get().load(product.images[0]).into(binding.imgView)
            initRecyclerView(product.id)

        }

        binding.topAppBar.setOnMenuItemClickListener{menuItem->
            when(menuItem.itemId){
                R.id.search-> {
                    pickImageGallery(product.id)
                    true}
                else-> false
            }
        }

        binding.fab.setOnClickListener {
            pickImageGallery(product.id)
        }

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            mainViewModel.getImageUrl(product.id)

        }

    }



    private fun initRecyclerView(id: Int) {
        mainViewModel.getImageUrl(id)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                mainViewModel.localImageStateFlow.collectLatest {
                    when(it){
                        is DBState.Loading->{
                            println("Loading $id data")
                        }
                        is DBState.Failure->{
                            println("Failed the message is ${it.msg}")
                        }
                        is DBState.SuccessProductWithLocalImage->{
                            println("We got the data data exists")
                            adaptToRecyclerView(it.data)

                        }
                        else ->{
                            println("Local Image Data is empty no recycler view will be displayed")
                        }
                    }
                }
            }
        }

    }

    private fun adaptToRecyclerView(data: ProductWithLocalImages?) {
        if (data!=null){
            val localImageAdapter = LocalImageAdapter(data)
            binding.recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@SingleViewActivity, LinearLayoutManager.HORIZONTAL,false)
                adapter=localImageAdapter

            }

        }
    }

    private fun pickImageGallery(productId: Int) {
        val intent= Intent(Intent.ACTION_PICK)
        intent.type="image/*"  //  */* means all  application/pdf allows only pdf file to be selected
        startActivityForResult(intent,100)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode== RESULT_OK) {
            Picasso.get().load(data?.data).into(binding.imgView)
            var productEntity = intent.getSerializableExtra("singleItemData") as ProductEntity
            val insertJob = mainViewModel.insertImageToRecyclerView(data?.data.toString(),productEntity.id)
            insertJob.invokeOnCompletion { mainViewModel.getImageUrl(productEntity.id) }
        }
    }



}