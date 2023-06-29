package com.example.mobileshop.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.mobileshop.model.LocalImageEntity

class MyDiffUtil(private val oldList: List<LocalImageEntity>, private val newList: List<LocalImageEntity>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id && oldList[oldItemPosition].productId== newList[newItemPosition].productId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].id != newList[newItemPosition].id -> false
            oldList[oldItemPosition].imageUrl != newList[newItemPosition].imageUrl -> false
            oldList[oldItemPosition].productId != newList[newItemPosition].productId -> false
            else -> true
        }
    }
}