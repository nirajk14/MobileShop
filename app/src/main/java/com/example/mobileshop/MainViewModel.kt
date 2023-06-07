package com.example.mobileshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileshop.ApiRecyclerView.ApiState
import com.example.mobileshop.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

    private val _productStateFlow: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Empty)

    val productStateFlow: StateFlow<ApiState> = _productStateFlow

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

}