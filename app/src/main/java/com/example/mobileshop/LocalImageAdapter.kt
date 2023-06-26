package com.example.mobileshop

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileshop.databinding.LocalImageCardBinding
import com.example.mobileshop.databinding.ProductCardBinding
import com.example.mobileshop.db.LocalImageEntity
import com.example.mobileshop.db.ProductWithLocalImages
import com.squareup.picasso.Picasso

class LocalImageAdapter():
    RecyclerView.Adapter<LocalImageAdapter.LocalImageViewHolder>() {

    private var listLocalImages = emptyList<LocalImageEntity>()

    class LocalImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageViewLocal: ImageView = itemView.findViewById(R.id.imageViewLocal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalImageViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.local_image_card,parent,false)
            return LocalImageViewHolder(itemView)
    }

    override fun getItemCount(): Int = listLocalImages.size

    override fun onBindViewHolder(holder: LocalImageViewHolder, position: Int) {

        Picasso.get().load("https://www.bollywoodhungama.com/wp-content/uploads/2019/06/Anil-Kapoor-cant-stop-laughing-after-Majnu-Bhais-painting-from-Welcome-travels-to-Buckingham-Palace.jpg").resize(600,600).centerCrop().into(holder.imageViewLocal)
        val currentItem = listLocalImages[position].imageUrl
        println("I think you should see recycler view coz look $currentItem")
        Picasso.get().load(currentItem).resize(600,600).centerCrop().into(holder.imageViewLocal)
    }

    fun setData(newLocalImageList: List<LocalImageEntity>){
        val diffUtil = MyDiffUtil(listLocalImages, newLocalImageList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        listLocalImages = newLocalImageList
        diffResults.dispatchUpdatesTo(this)
    }


}