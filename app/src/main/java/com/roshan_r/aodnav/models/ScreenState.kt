package com.roshan_r.aodnav.models

interface ScreenState {
    data object Loading : ScreenState
    data object Error : ScreenState
    data object Success : ScreenState
}