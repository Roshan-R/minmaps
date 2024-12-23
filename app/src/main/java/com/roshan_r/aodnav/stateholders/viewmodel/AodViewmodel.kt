package com.roshan_r.aodnav.stateholders.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roshan_r.aodnav.models.RouteInfo
import com.roshan_r.aodnav.models.AodUiState
import com.roshan_r.aodnav.models.ScreenState
import kotlinx.coroutines.launch

class AodViewmodel : ViewModel() {

    // ui state
    var uiState by mutableStateOf(AodUiState())
        private set

    fun setAodState(routeInfo: RouteInfo) = viewModelScope.launch {
        uiState = uiState.copy(aodState = routeInfo, state = ScreenState.Success)
    }

    fun setMapNotFoundState() = viewModelScope.launch {
        uiState = uiState.copy( state = ScreenState.Error)
    }

}