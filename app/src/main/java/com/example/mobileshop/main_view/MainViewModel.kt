package com.example.mobileshop.main_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.mobileshop.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {
//    val flow = mainRepository.getProducts()
//        .cachedIn(viewModelScope)

    fun paginatedProduct(insertDB: Boolean, searchQuery: String?)= mainRepository.getProducts(insertDB, searchQuery).cachedIn(viewModelScope)
}