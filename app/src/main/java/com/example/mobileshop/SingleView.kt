package com.example.mobileshop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobileshop.ApiRecyclerView.Products
import com.example.mobileshop.databinding.ActivitySingleViewBinding
import com.example.mobileshop.db.ProductEntity
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SingleView : AppCompatActivity() {
    private lateinit var binding: ActivitySingleViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivitySingleViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var product= intent.getSerializableExtra("singleItemData") as ProductEntity
        if (product!=null){
            binding.txtView.text=product?.title.toString()
            binding.txtView1.text=product?.brand.toString()
            binding.txtView2.text=product?.category.toString()
            binding.txtView3.text=product?.description.toString()
            Picasso.get().load(product.images[0]).into(binding.imgView)

        }

    }
}