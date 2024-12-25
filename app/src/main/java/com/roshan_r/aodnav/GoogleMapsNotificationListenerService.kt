package com.roshan_r.aodnav

import android.content.Intent
import android.graphics.drawable.Icon
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class GoogleMapsNotificationListenerService : NotificationListenerService() {

    companion object {
        private const val TAG = "GoogleMapsNotification"
        private const val MAPS_PACKAGE_NAME = "com.google.android.apps.maps"
        private const val BROADCAST_ACTION = "com.roshan-r.AODNav"
    }

    /**
     * Extracts information from a Google Maps notification and creates an intent.
     *
     * @param sbn The notification to process.
     * @return An Intent with extracted data, or null if data is incomplete.
     */
    private fun getIntentFromNotification(sbn: StatusBarNotification): Intent? {
        val intent = Intent(BROADCAST_ACTION)

        // Extract additional data from notification
        val subText = sbn.notification.extras.getString("android.subText")
        if (subText == null) {
            Log.d(TAG, "Unexpected subText format: $subText")
            return null // Exit early if subText format is invalid

        }

        val splitInfo = subText.split(" · ")
        if (splitInfo.size == 3) {
            intent.putExtra("totalTime", splitInfo[0].trim())
            intent.putExtra("distanceToDestination", splitInfo[1].trim())
            intent.putExtra("estimatedTimeOfArrival", splitInfo[2].split(" ETA")[0].trim())
        }

        // Extract distance to next turn, direction help text, and icon
        val distanceToNextTurn =
            sbn.notification.extras.getCharSequence("android.title")?.toString()
        val directionHelpText = sbn.notification.extras.getCharSequence("android.text")?.toString()
        val icon = sbn.notification.extras.getParcelable<Icon>("android.largeIcon")
        if (icon != null){
            intent.putExtra("icon", icon)
        }

        // Add extracted data to the intent
        if (distanceToNextTurn != null) intent.putExtra("distanceToNextTurn", distanceToNextTurn)
        if (directionHelpText != null) intent.putExtra("directionHelpText", directionHelpText)
        return intent
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // Check if the notification belongs to Google Maps
        if (sbn.packageName == MAPS_PACKAGE_NAME) {
            Log.d(TAG, "Notification received: ${sbn.notification.extras}")
            val intent = getIntentFromNotification(sbn)
            if (intent != null) {
                sendBroadcast(intent)
            }
        }
    }

    override fun onListenerConnected() {
        Log.d(TAG, "Notification Listener Connected")

        // Get active notifications from Google Maps
        val activeNotifications = activeNotifications
        var hasActiveMapsNotification = false

        for (sbn in activeNotifications) {
            if (sbn.packageName == MAPS_PACKAGE_NAME) {
                hasActiveMapsNotification = true
                val intent = getIntentFromNotification(sbn)
                if (intent != null) {
                    sendBroadcast(intent)
                }
                break // Process the first relevant notification only
            }
        }

        // If no active Google Maps notifications, send a default intent
        if (!hasActiveMapsNotification) {
            val noMapsIntent = Intent(BROADCAST_ACTION)
            noMapsIntent.putExtra("no.maps", true)
            sendBroadcast(noMapsIntent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Notification Listener Service Created")
    }
}
