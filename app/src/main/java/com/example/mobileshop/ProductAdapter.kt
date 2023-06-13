package com.example.mobileshop

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileshop.ApiRecyclerView.Products
import com.example.mobileshop.databinding.ProductCardBinding
import com.example.mobileshop.db.ProductEntity
import com.squareup.picasso.Picasso

class ProductAdapter(var mList: List<ProductEntity>,
                     private val onItemClick:(position:Int)->Unit):
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private lateinit var binding: ProductCardBinding

    class ProductViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val textView: TextView=itemView.findViewById(R.id.textView)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)

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
        holder.tvDescription.setText(product.description)
        Picasso.get().load(product.images[0]).into(holder.imageView)

        //Conclusion Don't use bindings

        holder.itemView.setOnClickListener {

            onItemClick(position)

        }



    }

//    fun setData(productList: List<ProductEntity>){
//        this.mList=productList
//        notifyDataSetChanged()
//    }
}