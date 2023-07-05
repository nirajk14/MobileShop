package com.example.mobileshop.product_view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileshop.BaseActivity
import com.example.mobileshop.R
import com.example.mobileshop.databinding.ActivityProductViewBinding
import com.example.mobileshop.model.LocalImageEntity
import com.example.mobileshop.model.Product
import com.example.mobileshop.utils.Constants.CHANNEL_ID
import com.example.mobileshop.utils.FlowState
import com.example.mobileshop.utils.PermissionHelper
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class ProductViewActivity : BaseActivity<ActivityProductViewBinding>() {
    private lateinit var binding: ActivityProductViewBinding
    private val productViewModel: ProductViewModel by viewModels()
    private val localImageAdapter by lazy { LocalImageAdapter() }
    private var aBoolean = true

    var notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.notification_bar_icon)
        .setContentTitle("LOL")
        .setContentText("It's me DIO")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)


    private lateinit var product: Product
    private val permissionHelper: PermissionHelper = PermissionHelper(this)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=createBinding()
        setContentView(binding.root)
        binding.includedPBP.progressBar.show()


        val productId = intent.getIntExtra("singleItemData",1)

        initObservers(productId)
        binding.fabNotif.setOnClickListener {
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                permissionHelper.requestNotificationPermission()
                notify(1, notificationBuilder.build())
            }
        }


    }




    private fun initObservers(productId: Int) {
        productViewModel.getProductById(productId)
        lifecycleScope.launch {
            productViewModel.productStateFlow.collectLatest {
                when(it){
                    is FlowState.Loading-> Timber.i("Loading Product Data")
                    is FlowState.Failure->Timber.i("Product Data load failed Reason: ${it.msg}")
                    is FlowState.SuccessProduct-> {
                        Timber.i("Loading Success")
                        product=it.data
                        initViews(product)
                    }
                    else -> Timber.d("Empty Product Data")
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
                fabNotif.hide()
                fabGallery.hide()

                Picasso.get().load(product.images[0]).resize(1000, 800).placeholder(R.drawable.progress_animation).centerInside().into(imgView)
                initRecyclerView(product.id)
            fab.setOnClickListener {
                if (aBoolean){
                    fabNotif.show()
                    fabGallery.show()
                    aBoolean=!aBoolean
                }
                else {
                    fabNotif.hide()
                    fabGallery.hide()
                    aBoolean=!aBoolean
                }
            }

            fabGallery.setOnClickListener {
                pickImageGallery()
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
            includedPBP.progressBar.hide()

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
                            Timber.i("Loading $id data")
                        }

                        is FlowState.Failure -> {
                            Timber.i("Failed the message is ${it.msg}")
                        }

                        is FlowState.SuccessProductWithLocalImage -> {

                            if (it.data != null) {
                                Timber.i("I think you should see recycler view")
                                adaptToRecyclerView(it.data)
                            }
                        }

                        else -> {
                            Timber.i("Local Image Data is empty no recycler view will be displayed")
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

    private fun pickImageGallery() {
        galleryLauncher.launch("image/*")
    }


}