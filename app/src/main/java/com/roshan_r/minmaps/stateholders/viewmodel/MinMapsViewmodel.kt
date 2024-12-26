package com.roshan_r.minmaps.stateholders.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roshan_r.minmaps.models.RouteInfo
import com.roshan_r.minmaps.models.UiState
import com.roshan_r.minmaps.models.ScreenState
import kotlinx.coroutines.launch

class MinMapsViewmodel : ViewModel() {

    // ui state
    var uiState by mutableStateOf(UiState())
        private set

    fun setNavigationState(routeInfo: RouteInfo) = viewModelScope.launch {
        uiState = uiState.copy(minMapsState = routeInfo, state = ScreenState.Navigation)
    }

    fun setMapNotFoundState() = viewModelScope.launch {
        uiState = uiState.copy( state = ScreenState.Error)
    }

    fun setLoadingState() = viewModelScope.launch {
        uiState = uiState.copy( state = ScreenState.Loading)
    }

}