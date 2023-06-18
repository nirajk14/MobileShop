package com.example.mobileshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileshop.api_recycler_view.ApiState
import com.example.mobileshop.db.DBState
import com.example.mobileshop.db.LocalImageEntity
import com.example.mobileshop.db.ProductEntity
import com.example.mobileshop.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

    private val _productStateFlow: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)
    val productStateFlow: StateFlow<ApiState> = _productStateFlow
    private val _productDataStateFlow: MutableStateFlow<DBState> = MutableStateFlow(DBState.Empty)
    val productDataStateFlow: StateFlow<DBState> = _productDataStateFlow
    private val _localImageDataStateFlow: MutableStateFlow<DBState> = MutableStateFlow(DBState.Empty)
    val localImageDataStateFlow: StateFlow<DBState> = _localImageDataStateFlow

    fun getProducts() = viewModelScope.launch {
        _productStateFlow.value= ApiState.Loading
        mainRepository.getProducts()
            .catch { e->
                _productStateFlow.value= ApiState.Failure(e)
            }
            .collect {
                    data ->
                    _productStateFlow.value = ApiState.Success(data)
            }

    }

    fun getAllProducts(refresh: Boolean) = viewModelScope.launch {
        println("getAllProducts")
        _productDataStateFlow.value= DBState.Loading
        mainRepository.getAllProducts(refresh)
            .catch { e->
                _productDataStateFlow.value= DBState.Failure(e)
            }
            .collect {
                    data ->
                _productDataStateFlow.value = DBState.SuccessProduct(data)
            }
    }

//    fun insertProduct(product : Products) = viewModelScope.launch {
//        mainRepository.insertProduct(product)
//    }

    fun insertLocalImage(localImageEntity: LocalImageEntity, productEntity: ProductEntity)= viewModelScope.launch {
        mainRepository.insertLocalImage(localImageEntity,productEntity)
    }

    fun getCorrectLocalImages(productId: Int)= viewModelScope.launch {
        _localImageDataStateFlow.value=DBState.Loading
        mainRepository.getCorrectLocalImages(productId)
            .catch { e->
                _localImageDataStateFlow.value= DBState.Failure(e)
            }
            .collect{
                data->
                _localImageDataStateFlow.value=DBState.SuccessLocalImage(data)
            }

    }


}