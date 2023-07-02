package com.example.mobileshop.product_view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileshop.BaseActivity
import com.example.mobileshop.R
import com.example.mobileshop.databinding.ActivityProductViewBinding
import com.example.mobileshop.model.Product
import com.example.mobileshop.utils.FlowState
import com.example.mobileshop.model.LocalImageEntity
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProductViewActivity : BaseActivity<ActivityProductViewBinding>() {
    private lateinit var binding: ActivityProductViewBinding
    private val productViewModel: ProductViewModel by viewModels()
    private val localImageAdapter by lazy { LocalImageAdapter() }

    private lateinit var product: Product
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=createBinding()
        setContentView(binding.root)
        val productId = intent.getIntExtra("singleItemData",1)

        initObservers(productId)


    }


    private fun initObservers(productId: Int) {
        productViewModel.getProductById(productId)
        lifecycleScope.launch {
            productViewModel.productStateFlow.collectLatest {
                when(it){
                    is FlowState.Loading-> println("Loading Product Data")
                    is FlowState.Failure->println("Product Data load failed Reason: ${it.msg}")
                    is FlowState.SuccessProduct-> {
                        println("Loading Success")
                        product=it.data
                        initViews(product)
                    }
                    else -> println("Empty Product Data")
                }
            }
        }
    }

    private fun initViews(product: Product) {
        val builder = AlertDialog.Builder(this)
        with(binding){
                title.text = product.title.toString()
                brand.text = product.brand.toString()
                category.text =product.category.toString()
                description.text = product.description.toString()
                Picasso.get().load(product.images[0]).resize(1000, 800).centerCrop().into(imgView)
                initRecyclerView(product.id)

            fab.setOnClickListener {
                pickImageGallery(product.id)
            }
            includedSingle.mainAppBar.setOnMenuItemClickListener{ menuItem ->
                when (menuItem.itemId) {
                    R.id.infoButton -> showInfoDialog(builder)
                    else -> false
                }
            }
            includedSingle.backButton.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            includedSingle.titleText.text="  Mobile Shop Application"

        }
    }

    override fun createBinding(): ActivityProductViewBinding {
        return ActivityProductViewBinding.inflate(layoutInflater)
    }
    private fun initRecyclerView(id: Int) {
        productViewModel.getImageUrl(id)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {

                productViewModel.localImageSharedFlow.collectLatest {
                    when (it) {
                        is FlowState.Loading -> {
                            println("Loading $id data")
                        }

                        is FlowState.Failure -> {
                            println("Failed the message is ${it.msg}")
                        }

                        is FlowState.SuccessProductWithLocalImage -> {

                            if (it.data != null) {
                                println("I think you should see recycler view")
                                adaptToRecyclerView(it.data)
                            }
                        }

                        else -> {
                            println("Local Image Data is empty no recycler view will be displayed")
                        }
                    }
                }
            }
        }
    }

    private fun adaptToRecyclerView(data: List<LocalImageEntity>) {

        if (data.isNotEmpty()) {


            binding.recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(
                    this@ProductViewActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = localImageAdapter.apply { setData(data) }
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            Picasso.get().load(uri).resize(600, 600).centerCrop().into(binding.imgView)
            productViewModel.insertImageToRecyclerView(uri.toString(), product.id)
        }
    }

    private fun pickImageGallery(productId: Int) {
        galleryLauncher.launch("image/*")
    }


}