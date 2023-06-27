package com.example.mobileshop.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileshop.R
import com.example.mobileshop.api_recycler_view.Product
import com.squareup.picasso.Picasso

class ProductPagingAdapter: PagingDataAdapter<Product, ProductPagingAdapter.ProductViewHolder>(COMPARATOR) {

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product=getItem(position)
        product?.let {
            holder.bind(it)
        }

        


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_card,parent,false)
        return ProductViewHolder(view)
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


        val textView: TextView = itemView.findViewById(R.id.textView)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(product: Product) {
            textView.text = product.title
            tvDescription.text = product.description

            Picasso.get().load(product.images[0]).into(imageView)
        }
    }

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
                oldItem.id==newItem.id


            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
                oldItem.id==newItem.id



        }
    }


}