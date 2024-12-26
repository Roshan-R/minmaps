package com.roshan_r.minmaps.models

data class UiState(
    val state: ScreenState = ScreenState.Loading,
    val minMapsState: RouteInfo = RouteInfo()
)
