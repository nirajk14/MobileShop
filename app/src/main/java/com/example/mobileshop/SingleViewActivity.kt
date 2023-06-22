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
class SingleViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingleViewBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var builder: AlertDialog.Builder

    private val localImageAdapter by lazy {LocalImageAdapter()}

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

        val product= intent.getSerializableExtra("singleItemData") as Product
        if (product!=null){
            binding.txtView.text=product.title.toString()
            binding.txtView1.text=product.brand.toString()
            binding.txtView2.text=product.category.toString()
            binding.txtView3.text=product.description.toString()
            Picasso.get().load(product.images[0]).into(binding.imgView)
            initRecyclerView(product.id)

        }
        builder = AlertDialog.Builder(this)
        binding.topAppBar.setOnMenuItemClickListener{menuItem->
            when(menuItem.itemId){
                R.id.infoButton-> {
                    builder.setTitle("This app was created by")
                        .setMessage("Niraj Kushwaha")
                        .setPositiveButton("OK") {dialogInterface, it->
                            dialogInterface.cancel()
                        }.show()
                    true}
                else-> false
            }
        }

        binding.fab.setOnClickListener {
            pickImageGallery(product.id)
        }


    }



    private fun initRecyclerView(id: Int) {
        mainViewModel.getImageUrl(id)


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){

                mainViewModel.localImageSharedFlow.collectLatest {
                    when(it){
                        is DBState.Loading->{
                            println("Loading $id data")
                        }
                        is DBState.Failure->{
                            println("Failed the message is ${it.msg}")
                        }
                        is DBState.SuccessProductWithLocalImage->{

                            if (it.data!=null){
                                println("I think you should see recycler view")
                                adaptToRecyclerView(it.data)}
                        }
                        else ->{
                            println("Local Image Data is empty no recycler view will be displayed")
                        }
                    }
                }
            }
        }

    }

    private fun adaptToRecyclerView(data: List<LocalImageEntity>) {

        if (data.isNotEmpty()){


            binding.recyclerView.also {recyclerView->
                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = LinearLayoutManager(this@SingleViewActivity, LinearLayoutManager.HORIZONTAL,false)
                recyclerView.adapter=localImageAdapter
                localImageAdapter.setData(data)

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
            var product = intent.getSerializableExtra("singleItemData") as Product
            mainViewModel.insertImageToRecyclerView(data?.data.toString(),product.id)
        }
    }

}