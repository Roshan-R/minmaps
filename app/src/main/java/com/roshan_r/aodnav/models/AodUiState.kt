package com.roshan_r.aodnav.models

data class AodUiState(
    val state: ScreenState = ScreenState.Loading,
    val aodState: RouteInfo = RouteInfo()
)
