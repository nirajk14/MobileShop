package com.example.mobileshop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileshop.databinding.ProductCardBinding
import com.example.mobileshop.db.LocalImageEntity
import com.example.mobileshop.db.ProductEntity
import com.squareup.picasso.Picasso
import androidx.activity.viewModels
import com.example.mobileshop.db.ProductWithLocalImages

class ProductAdapter(var mList: List<ProductEntity>, var localImageMap: MutableMap<Int, String>,
                     private val onItemClick:(product: ProductEntity)->Unit):
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

        if (localImageMap[position]!=null)
            Picasso.get().load(localImageMap[position]).into(holder.imageView)
        else
            Picasso.get().load(product.images[0]).into(holder.imageView)


        holder.itemView.setOnClickListener {

            onItemClick(product)

        }



    }

}