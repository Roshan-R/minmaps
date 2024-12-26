package com.roshan_r.minmaps.stateholders.screen

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roshan_r.minmaps.MainActivity
import com.roshan_r.minmaps.models.RouteInfo
import com.roshan_r.minmaps.models.ScreenState
import com.roshan_r.minmaps.stateholders.viewmodel.MinMapsViewmodel


fun openGoogleMaps(context: Context, tryMiniView: Boolean) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setPackage("com.google.android.apps.maps")  // Specify Google Maps package
    }
    try {
        context.startActivity(intent)
        Thread.sleep(2000)
    } catch (e: Exception) {/* no-op */
    }
    if (tryMiniView) {
        Log.d("OpenGoogleMaps", "Called with tryMiniView")

        val selfIntent = Intent(context, MainActivity::class.java)
        selfIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) // Use already opened intent
        context.startActivity(selfIntent)
    }
}

@Composable
fun HomeScreen(viewmodel: MinMapsViewmodel) {
    val uiState = viewmodel.uiState

    AnimatedContent(targetState = uiState.state,
        modifier = Modifier.fillMaxSize(),
        label = "screen content transition",
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        }) {
        when (it) {
            ScreenState.Loading -> LoadingState()
            ScreenState.Error -> ErrorState()
            ScreenState.Navigation -> NavigationState(uiState.minMapsState)
            ScreenState.RequestPermission -> PermissionRequestState()
        }
    }
}

@Composable
fun PermissionRequestState() {
    val context = LocalContext.current
    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

        ) {
            Text(
                "MinMaps uses the data from Google Maps notifications when it is in navigation mode. " +
                        "To ensure the app functions correctly, please enable notification access for MinMaps.",
                fontSize = TextUnit(4.3F, TextUnitType.Em)
            )
            Spacer(modifier = Modifier.size(21.dp))

            Text(
                "In the next screen, tap 'Allow Notification Access' for MinMaps to proceed.",
                fontSize = TextUnit(4.3F, TextUnitType.Em)
            )
            Spacer(modifier = Modifier.size(21.dp))
            Button(onClick = {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                context.startActivity(intent)

            }) {
                Text("Click here to request permission")
            }
        }
    }
}


@Composable
fun ErrorState() {
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "MinMaps needs Google Maps to be in navigation mode to work. Please start a navigation and come back.",
                fontSize = TextUnit(4.3F, TextUnitType.Em)
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                openGoogleMaps(context, false)
            }) { Text("Open Maps") }

        }
    }
}

@Composable
fun LoadingState() {

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Checking for Navigation",
                fontSize = TextUnit(4.3F, TextUnitType.Em),
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator()

            Spacer(Modifier.height(30.dp))

        }
    }
}


@Composable
fun NavigationState(state: RouteInfo) {

    Surface(color = Color.Black, modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
        ) {

//            MapButtons()

            // Route Info Section
            Box(modifier = Modifier.weight(12F)) {
                RouteInfoSection(state)
            }

            // Bottom Row Section
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                BottomRow(state)
            }
        }
    }
}

@Composable
fun RouteInfoSection(state: RouteInfo) {
    val context = LocalContext.current
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        state.bitmap?.let {

            Box(
                Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.45f),
                contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = { openGoogleMaps(context, true) },
                    border = BorderStroke(width = 1.dp, Color.Gray),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonColors(
                        containerColor = Color.Black,
                        contentColor = Color.Black,
                        disabledContentColor = Color.Black,
                        disabledContainerColor = Color.Black
                    ),
                ) {
                    Image(
                        it.asImageBitmap(),
                        modifier = Modifier.fillMaxSize(0.4f),
                        contentDescription = "Route Icon",
                    )
                }
            }
        }

        Text(
            text = state.distanceToNextTurn,
            color = Color.White,
            style = TextStyle(fontSize = 35.sp)
        )

        HorizontalDivider(
            Modifier
                .padding(vertical = 20.dp, horizontal = 20.dp)
                .fillMaxWidth(),
            color = Color.DarkGray
        )

        Text(
            text = state.directionHelpText,
            style = TextStyle(fontSize = 18.sp, color = Color.White),
            modifier = Modifier.padding(horizontal = 10.dp)
        )
    }
}

@Composable
fun BottomRow(state: RouteInfo) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround, // Changed to SpaceAround for better spacing
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 30.dp)
    ) {
        // Distance Column
        InfoColumn(
            label = "Distance", value = state.distanceToDestination
        )

        // ETA Column
        InfoColumn(
            label = "Eta", value = state.estimatedTimeOfArrival
        )

        InfoColumn(
            label = "Total Time", value = state.totalTime
        )

    }
}

@Composable
fun InfoColumn(label: String, value: String) {
    Column(modifier = Modifier.padding(22.dp)) {
        Text(
            text = label, color = Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = value, color = Color.White, fontSize = 17.sp
        )
    }
}
