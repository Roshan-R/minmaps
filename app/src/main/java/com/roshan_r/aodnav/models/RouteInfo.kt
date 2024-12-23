package com.roshan_r.aodnav.models

import android.graphics.Bitmap

data class RouteInfo(
    var totalTime: String = "",
    var distanceToDestination: String = "",
    var estimatedTimeOfArrival: String = "",
    var distanceToNextTurn: String = "",
    var directionHelpText: String = "",
    var bitmap: Bitmap? = null
)