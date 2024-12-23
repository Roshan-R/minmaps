package com.roshan_r.aodnav

import android.content.Intent
import android.graphics.drawable.Icon
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

fun getIntentFromNotification(sbn: StatusBarNotification): Intent? {
    val intent = Intent("com.roshan-r.AODNav")

    if (sbn.notification.extras.getString("android.subText") != null){
        val subText = sbn.notification.extras.getString("android.subText").toString()
        val splitInfo = subText.split(" · ")

        if (splitInfo.size != 3){
            Log.d("UniqueNotification", sbn.notification.extras.toString())
            return null
        }

        val totalTime = splitInfo[0].trim() // "5 hr 58 min"
        val distanceToDestination = splitInfo[1].trim() // "215 km"
        val estimatedTimeOfArrival = splitInfo[2].split(" ETA")[0].trim() // "10:30 pm"

        intent.putExtra("totalTime", totalTime)
        intent.putExtra("distanceToDestination", distanceToDestination)
        intent.putExtra("estimatedTimeOfArrival", estimatedTimeOfArrival)
    }

    val distanceToNextTurn = sbn.notification.extras.getCharSequence("android.title")?.toString()
    val directionHelpText = sbn.notification.extras.getCharSequence("android.text")?.toString() // Head South
    val icon = sbn.notification.extras.getParcelable<Icon>("android.largeIcon")

    intent.putExtra("distanceToNextTurn", distanceToNextTurn)
    intent.putExtra("directionHelpText", directionHelpText)
    intent.putExtra("icon", icon)

    return intent
}

class GoogleMapsNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName.isNotEmpty() && sbn.packageName == "com.google.android.apps.maps") {
            Log.d("NotificationService", sbn.notification.extras.toString())
            val intent = getIntentFromNotification(sbn = sbn)
            sendBroadcast(intent)
        }
    }

    override fun onListenerConnected(){
        Log.d("Listened", "THE LISTENER IS CONNECTED!!")
        val notifications = getActiveNotifications().filter { it.packageName == "com.google.android.apps.maps" }
        if (notifications.isNotEmpty()){
            val intent = getIntentFromNotification(sbn = notifications[0])
            if (intent != null){
                sendBroadcast(intent)
            }
        }
        else{
            val intent = Intent("com.roshan-r.AODNav")
            intent.putExtra("no.maps", "True")
            sendBroadcast(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("NotificationListener", "Service Created")
    }
}