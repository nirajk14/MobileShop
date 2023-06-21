package com.example.mobileshop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileshop.databinding.LocalImageCardBinding
import com.example.mobileshop.databinding.ProductCardBinding
import com.example.mobileshop.db.ProductEntity
import com.example.mobileshop.db.ProductWithLocalImages
import com.squareup.picasso.Picasso

class LocalImageAdapter(val productWithLocalImages: ProductWithLocalImages):
    RecyclerView.Adapter<LocalImageAdapter.LocalImageViewHolder>() {

    class LocalImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageViewLocal: ImageView = itemView.findViewById(R.id.imageViewLocal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalImageViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.local_image_card,parent,false)
            return LocalImageViewHolder(itemView)
    }

    override fun getItemCount(): Int = productWithLocalImages.localImageEntities.size

    override fun onBindViewHolder(holder: LocalImageViewHolder, position: Int) {
        val currentItem = productWithLocalImages.localImageEntities[position].imageUrl
        Picasso.get().load(currentItem).into(holder.imageViewLocal)
    }


}