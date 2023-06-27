package com.example.mobileshop

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileshop.api_recycler_view.Product
import com.example.mobileshop.databinding.ActivitySingleViewBinding
import com.example.mobileshop.db.DBState
import com.example.mobileshop.db.LocalImageEntity
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SingleViewActivity : BaseActivity<ActivitySingleViewBinding>() {
    private lateinit var binding: ActivitySingleViewBinding
    private val mainViewModel: MainViewModel by viewModels()


    private val localImageAdapter by lazy { LocalImageAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=createBinding()
        setContentView(binding.root)



//        val animationDrawable = binding.constraintSingle.background as AnimationDrawable
//
//        animationDrawable.apply {
//            setEnterFadeDuration(2500)
//            setExitFadeDuration(5000)
//            start()
//        }

        val product = intent.getSerializableExtra("singleItemData") as Product
        if (product != null) {
            binding.txtView.text = product.title.toString()
            binding.txtView1.text = product.brand.toString()
            binding.txtView2.text = product.category.toString()
            binding.txtView3.text = product.description.toString()
            Picasso.get().load(product.images[0]).into(binding.imgView)
            initRecyclerView(product.id)

        }


        binding.fab.setOnClickListener {
            pickImageGallery(product.id)
        }
        val builder = AlertDialog.Builder(this)

        binding.includedSingle.mainAppBar.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.infoButton -> showInfoDialog(builder)
                else -> false
            }
        }


    }

    override fun createBinding(): ActivitySingleViewBinding {
        return ActivitySingleViewBinding.inflate(layoutInflater)
    }


    private fun initRecyclerView(id: Int) {
        mainViewModel.getImageUrl(id)


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {

                mainViewModel.localImageSharedFlow.collectLatest {
                    when (it) {
                        is DBState.Loading -> {
                            println("Loading $id data")
                        }

                        is DBState.Failure -> {
                            println("Failed the message is ${it.msg}")
                        }

                        is DBState.SuccessProductWithLocalImage -> {

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
                    this@SingleViewActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = localImageAdapter.apply { setData(data) }

            }

        }
    }

    private fun pickImageGallery(productId: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type =
            "image/*"  //  */* means all  application/pdf allows only pdf file to be selected
        startActivityForResult(intent, 100)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Picasso.get().load(data?.data).into(binding.imgView)
            var product = intent.getSerializableExtra("singleItemData") as Product
            mainViewModel.insertImageToRecyclerView(data?.data.toString(), product.id)
        }
    }

}