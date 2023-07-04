package com.example.mobileshop.product_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileshop.utils.FlowState
import com.example.mobileshop.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

    private val _productStateFlow: MutableStateFlow<FlowState> = MutableStateFlow(FlowState.Empty)
    val productStateFlow: StateFlow<FlowState> = _productStateFlow

    private val _localImageSharedFlow: MutableSharedFlow<FlowState> = MutableSharedFlow(2)
    val localImageSharedFlow: SharedFlow<FlowState> = _localImageSharedFlow



    fun insertImageToRecyclerView(url: String, productId: Int) = viewModelScope.launch {
        mainRepository.insertImageToRecyclerView(url,productId)
    }

    fun getProductById(productId: Int) = viewModelScope.launch {
        _productStateFlow.value= FlowState.Loading
        mainRepository.getProductById(productId)
            .catch {e->
                _productStateFlow.value= FlowState.Failure(e)
            }
            .collectLatest { data->
                _productStateFlow.value= FlowState.SuccessProduct(data)
            }
    }

    fun getImageUrl(productId: Int) = viewModelScope.launch {
        _localImageSharedFlow.emit(FlowState.Loading)
        mainRepository.localImageDao.getLocalImagesForProduct(productId)
            .catch { e ->
                _localImageSharedFlow.emit(FlowState.Failure(e))
            }
            .collect {
                    data->
                _localImageSharedFlow.emit(FlowState.SuccessProductWithLocalImage(data))
            }


    }
}