package com.example.mobileshop.main_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileshop.R
import com.example.mobileshop.databinding.ProductCardBinding
import com.squareup.picasso.Picasso
import com.example.mobileshop.model.Product

class ProductAdapter(var mList: List<Product>,
                     private val onItemClick:(product: Product)->Unit):
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private lateinit var binding: ProductCardBinding



    class ProductViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val textView: TextView=itemView.findViewById(R.id.textView)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val tvStock: TextView = itemView.findViewById(R.id.tvStock)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val tvBrand: TextView = itemView.findViewById(R.id.tvBrand)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        binding= ProductCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProductViewHolder(binding.root)
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product=mList[position]
//        binding.textView.text= product.title
//        binding.tvDescription.text=product.description
//        Picasso.get().load(product.images[0]).into(binding.imageView)
        holder.textView.setText(product.title)
        holder.tvBrand.setText(product.brand)
        holder.tvPrice.setText("$" + product.price.toString())
        if (product.stock!! <= 0)
            holder.tvStock.setText("Out of Stock")
        else
            holder.tvStock.setText("In Stock: ${product.stock}")
        holder.tvRating.setText("\u2B50" +" " + product.rating.toString() +"/5")


        Picasso.get().load(product.images[0]).resize(1000,600).centerCrop().into(holder.imageView)


        holder.itemView.setOnClickListener {

            onItemClick(product)

        }



    }

}