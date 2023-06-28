package com.example.mobileshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.mobileshop.api_recycler_view.ApiState
import com.example.mobileshop.db.DBState
import com.example.mobileshop.db.LocalImageEntity
import com.example.mobileshop.paging.ProductPagingSource
import com.example.mobileshop.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {


    private val _productDataStateFlow: MutableStateFlow<DBState> = MutableStateFlow(DBState.Empty)
    val productDataStateFlow: StateFlow<DBState> = _productDataStateFlow
    private val _localImageSharedFlow: MutableSharedFlow<DBState> = MutableSharedFlow(2)
    val localImageSharedFlow: SharedFlow<DBState> = _localImageSharedFlow


    val flow = Pager(
        config = PagingConfig(pageSize = 1, prefetchDistance = 0, maxSize = 2),
        pagingSourceFactory ={
            ProductPagingSource(mainRepository.productDao,mainRepository.apiServiceImpl)
        }
    ).flow
        .cachedIn(viewModelScope)




    fun getAllProducts(refresh: Boolean) = viewModelScope.launch {
        println("getAllProducts")
        _productDataStateFlow.emit( DBState.Loading)
        mainRepository.getProducts()
            .catch { e->
                _productDataStateFlow.emit(DBState.Failure(e))
            }
            .collect {
                    data ->
                _productDataStateFlow.emit(DBState.SuccessProduct(data))
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