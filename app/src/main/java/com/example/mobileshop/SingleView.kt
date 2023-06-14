package com.example.mobileshop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.mobileshop.api_recycler_view.Products
import com.example.mobileshop.databinding.ActivitySingleViewBinding
import com.example.mobileshop.db.ProductEntity
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SingleView : AppCompatActivity() {
    private lateinit var binding: ActivitySingleViewBinding
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivitySingleViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var product= intent.getSerializableExtra("singleItemData") as ProductEntity
        if (product!=null){
            binding.txtView.text=product.title.toString()
            binding.txtView1.text=product.brand.toString()
            binding.txtView2.text=product.category.toString()
            binding.txtView3.text=product.description.toString()
            Picasso.get().load(product.images[0]).into(binding.imgView)

        }

        binding.topAppBar.setOnMenuItemClickListener{menuItem->
            when(menuItem.itemId){
                R.id.search-> {
                    pickImageGallery()
                    true}
                else-> false
            }
        }

    }

    private fun pickImageGallery() {
        val intent= Intent(Intent.ACTION_PICK)
        intent.type="image/"
        startActivityForResult(intent,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode== RESULT_OK) {
            Picasso.get().load(data?.data).into(binding.imgView)
            var products = intent.getSerializableExtra("singleItemData") as ProductEntity

            val product = Products(
                id = products.id!!,
                title = products.title,
                description = products.description,
                price = products.price,
                discountPercentage = products.discountPercentage,
                rating = products.rating,
                stock = products.stock,
                brand = products.brand,
                category = products.category,
                thumbnail = products.thumbnail,
                images = arrayListOf(data?.data.toString(),"Blank") as ArrayList<String>
            )

            mainViewModel.insertProduct(product)

            //todo look for copy copyTo func or can we even use map
        }
    }


}