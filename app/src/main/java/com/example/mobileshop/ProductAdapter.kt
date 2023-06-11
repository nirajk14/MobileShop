package com.example.mobileshop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileshop.ApiRecyclerView.Products
import com.example.mobileshop.databinding.ProductCardBinding
import com.example.mobileshop.db.ProductEntity
import com.squareup.picasso.Picasso

class ProductAdapter(private var mList: List<ProductEntity>,
                     private val onItemClick:(ProductEntity,position:Int)->Unit):
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    var _mList=mList
    private lateinit var binding: ProductCardBinding

    class ProductViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        binding= ProductCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProductViewHolder(binding.root)
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product=mList[position]

        binding.textView.text= product.title
        binding.tvDescription.text=product.description
        Picasso.get().load(product.images[0]).into(binding.imageView)

        holder.itemView.setOnClickListener {

            onItemClick(product,position)

        }



    }

    fun setData(productList: List<ProductEntity>){
        this.mList=productList
        _mList=productList
        notifyDataSetChanged()
    }
}