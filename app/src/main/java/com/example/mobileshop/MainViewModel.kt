package com.example.mobileshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileshop.api_recycler_view.ApiState
import com.example.mobileshop.db.DBState
import com.example.mobileshop.db.LocalImageEntity
import com.example.mobileshop.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {


    private val _productDataStateFlow: MutableStateFlow<DBState> = MutableStateFlow(DBState.Empty)
    val productDataStateFlow: StateFlow<DBState> = _productDataStateFlow
    private val _localImageSharedFlow: MutableSharedFlow<DBState> = MutableSharedFlow(replay = 5)
    val localImageSharedFlow: SharedFlow<DBState> = _localImageSharedFlow





    fun getAllProducts(refresh: Boolean) = viewModelScope.launch {
        println("getAllProducts")
        _productDataStateFlow.value= DBState.Loading
        mainRepository.getProducts()
            .catch { e->
                _productDataStateFlow.value= DBState.Failure(e)
            }
            .collect {
                    data ->
                _productDataStateFlow.value = DBState.SuccessProduct(data)
            }
    }


    fun insertLocalImage(url: String, productId: Int)= viewModelScope.launch {
        val localImageEntity = LocalImageEntity(
            id=0,
            imageUrl = url,
            productId= productId
        )
        mainRepository.insertLocalImage(localImageEntity)
    }

    fun insertImageToRecyclerView(url: String, productId: Int) = viewModelScope.launch {
        mainRepository.insertImageToRecyclerView(url,productId)
    }

    fun getImageUrl(productId: Int) = viewModelScope.launch {
        _localImageSharedFlow.emit(DBState.Loading)
        mainRepository.getLocalImagesForProduct(productId)
                .catch { e ->
                    _localImageSharedFlow.emit(DBState.Failure(e))
                }
                .collect {
                    data->
                    _localImageSharedFlow.emit(DBState.SuccessProductWithLocalImage(data))
                }


        }




}