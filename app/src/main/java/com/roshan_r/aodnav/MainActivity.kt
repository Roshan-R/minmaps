package com.roshan_r.aodnav

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.roshan_r.aodnav.models.RouteInfo
import com.roshan_r.aodnav.stateholders.screen.AodHomeScreen
import com.roshan_r.aodnav.stateholders.viewmodel.AodViewmodel


class MainActivity : ComponentActivity() {

    private val aodViewmodel: AodViewmodel by viewModels<AodViewmodel>()

    private val receiver = object : BroadcastReceiver() {

        @RequiresApi(Build.VERSION_CODES.P)
        override fun onReceive(context: Context, intent: Intent) {

            if (intent.getStringExtra("no.maps") != null) {
                aodViewmodel.setMapNotFoundState()
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

            aodViewmodel.setAodState(
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


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AodHomeScreen(viewmodel = aodViewmodel)
        }


        if (!isNotificationAccessEnabled(this)) {
            AlertDialog.Builder(this)
                .setTitle("Notification Access Required")
                .setMessage("This app needs access to your notifications to function properly. Please enable it in the settings.")
                .setPositiveButton("Allow") { _, _ ->
                    requestNotificationAccess()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        val serviceIntent = Intent(applicationContext, GoogleMapsNotificationListenerService::class.java)
        applicationContext.startService(serviceIntent)

        registerReceiver(
            receiver,
            IntentFilter("com.roshan-r.AODNav"),
            Context.RECEIVER_EXPORTED
        )

    }

    private fun requestNotificationAccess() {
        val intent =
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        startActivity(intent)
    }

    private fun isNotificationAccessEnabled(context: Context): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        return enabledListeners?.contains(context.packageName) == true
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}

