package com.roshan_r.minmaps.models

interface ScreenState {
    data object Loading : ScreenState
    data object Error : ScreenState
    data object Navigation : ScreenState
    data object RequestPermission: ScreenState
}