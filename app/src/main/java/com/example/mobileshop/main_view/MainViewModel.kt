package com.example.mobileshop.main_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.mobileshop.repository.MainRepository
import com.example.mobileshop.utils.FlowState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {
//    val flow = mainRepository.getProducts()
//        .cachedIn(viewModelScope)
//
//    val countDownFlow = flow {
//        val startingValue= 10
//        var currentValue= startingValue
//        emit(startingValue)
//        while (currentValue > 0){
//            kotlinx.coroutines.delay(1000L)
//            currentValue--
//            emit(currentValue)
//        }
//
//    }

    private val _productCategoryStateFlow: MutableStateFlow<FlowState> = MutableStateFlow(FlowState.Empty)
    val productCategoryStateFlow: StateFlow<FlowState> = _productCategoryStateFlow

    fun getCategory() = viewModelScope.launch {
        _productCategoryStateFlow.value= FlowState.Loading
        mainRepository.categoryList()
            .catch {e->
                e.printStackTrace()
                _productCategoryStateFlow.value= FlowState.Failure(e)
            }
            .collectLatest { category->
                _productCategoryStateFlow.value= FlowState.SuccessProductCategory(category)
            }
    }



    fun paginatedProduct(insertDB: Boolean, searchQuery: String?, chipQuery: List<String>)= mainRepository.getProducts(insertDB, searchQuery, chipQuery).cachedIn(viewModelScope)
}