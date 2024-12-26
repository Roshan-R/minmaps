package com.roshan_r.minmaps

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.roshan_r.minmaps.models.RouteInfo
import com.roshan_r.minmaps.stateholders.screen.HomeScreen
import com.roshan_r.minmaps.stateholders.screen.PermissionRequestState
import com.roshan_r.minmaps.stateholders.viewmodel.MinMapsViewmodel


class MainActivity : ComponentActivity() {

    private val minMapsViewmodel: MinMapsViewmodel by viewModels<MinMapsViewmodel>()

    private val receiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.P)
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getStringExtra("no.maps") != null) {
                minMapsViewmodel.setMapNotFoundState()
                return
            }

            val totalTime = intent.getStringExtra("totalTime") ?: "No Text"
            val distanceToDestination = intent.getStringExtra("distanceToDestination") ?: "No Text"
            val estimatedTimeOfArrival =
                intent.getStringExtra("estimatedTimeOfArrival") ?: "No Text"
            val distanceToNextTurn = intent.getStringExtra("distanceToNextTurn") ?: "No Text"
            val directionHelpText = intent.getStringExtra("directionHelpText") ?: "No Text"
            val icon: Icon? = intent.getParcelableExtra("icon")
            val image = icon?.loadDrawable(context)?.let { Utils.convertIconToBitmap(context, it) }

            minMapsViewmodel.setNavigationState(
                RouteInfo(
                    totalTime,
                    distanceToDestination,
                    estimatedTimeOfArrival,
                    distanceToNextTurn,
                    directionHelpText,
                    bitmap = image
                )
            )
        }
    }

    private fun isNotificationAccessEnabled(context: Context): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver, "enabled_notification_listeners"
        )
        return enabledListeners?.contains(context.packageName) == true
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        when (isNotificationAccessEnabled(this)) {
            true -> {
                val serviceIntent = Intent(applicationContext, GoogleMapsNotificationListenerService::class.java)
                applicationContext.startService(serviceIntent)
                registerReceiver(
                    receiver, IntentFilter("com.roshan-r.MinMaps"), Context.RECEIVER_EXPORTED
                )
                setContent {
                    HomeScreen(viewmodel = minMapsViewmodel)
                }
            }
            false -> {
                setContent {
                    PermissionRequestState()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}

