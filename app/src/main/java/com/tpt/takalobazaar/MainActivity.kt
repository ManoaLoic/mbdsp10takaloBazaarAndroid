package com.tpt.takalobazaar

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.tpt.takalobazaar.providers.LocalNavHost
import com.tpt.takalobazaar.screens.holder.HolderScreen
import com.tpt.takalobazaar.ui.theme.StoreTheme
import com.tpt.takalobazaar.utils.LocalScreenSize
import com.tpt.takalobazaar.utils.getScreenSize
import com.tpt.takalobazaar.api.RetrofitInstance
import com.tpt.takalobazaar.screens.notifications.NotificationViewModel
import com.tpt.takalobazaar.services.SessionService
import com.tpt.takalobazaar.ui.theme.Dimension
import dagger.hilt.android.AndroidEntryPoint
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val REQUEST_CODE_READ_STORAGE = 1001
    private val REQUEST_CODE_NOTIFICATION = 1003
    private val PICK_IMAGE_REQUEST = 1002

    @Inject
    lateinit var sessionService: SessionService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_NOTIFICATION)
        }

        setContent {
            val notificationViewModel: NotificationViewModel = viewModel()
            val context = LocalContext.current

            // Register the broadcast receiver
            DisposableEffect(Unit) {
                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        val title = intent?.getStringExtra("title") ?: ""
                        val message = intent?.getStringExtra("message") ?: ""
                        notificationViewModel.showNotification(title, message)
                    }
                }
                val filter = IntentFilter("com.tpt.takalobazaar.FCM_MESSAGE")
                context.registerReceiver(receiver, filter)

                onDispose {
                    context.unregisterReceiver(receiver)
                }
            }

            /** The status bar color which is dynamic */
            val defaultStatusBarColor = MaterialTheme.colors.background.toArgb()
            var statusBarColor by remember { mutableStateOf(defaultStatusBarColor) }
            window.statusBarColor = statusBarColor

            /** Our navigation controller */
            val navController = rememberNavController()

            /** Getting screen size */
            val size = LocalContext.current.getScreenSize()

            StoreTheme {
                CompositionLocalProvider(
                    LocalScreenSize provides size,
                    LocalNavHost provides navController
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            HolderScreen(
                                onStatusBarColorChange = {
                                    statusBarColor = it.toArgb()
                                },
                                navController = navController
                            )

                            if (notificationViewModel.notificationState.value.isVisible) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .wrapContentHeight()
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                                        .background(
                                            color = MaterialTheme.colors.surface,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp))
                                        .padding(Dimension.pagePadding)
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(Dimension.pagePadding),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = notificationViewModel.notificationState.value.title,
                                            style = MaterialTheme.typography.button,
                                            color = MaterialTheme.colors.onBackground,
                                        )
                                        Text(
                                            text = notificationViewModel.notificationState.value.message,
                                            style = MaterialTheme.typography.body1,
                                            color = MaterialTheme.colors.onBackground,
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Button(
                                                onClick = {
                                                    notificationViewModel.hideNotification()
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    backgroundColor = MaterialTheme.colors.secondary,
                                                    contentColor = MaterialTheme.colors.onSecondary
                                                )
                                            ) {
                                                Text("Cacher")
                                            }
                                            Button(
                                                onClick = {
                                                    notificationViewModel.hideNotification()
                                                    navController.navigate("currentExchange")
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    backgroundColor = MaterialTheme.colors.primary,
                                                    contentColor = MaterialTheme.colors.onPrimary
                                                )
                                            ) {
                                                Text("Voir")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        RetrofitInstance.initialize(sessionService)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "your_channel_id"
            val channelName = "Your Channel Name"
            val channelDescription = "Your channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            // Register the channel with the system
            val notificationManager: NotificationManager? = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_STORAGE)
        } else {
            pickImage()
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_STORAGE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                pickImage()
            } else {
                // Permission denied
            }
        } else if (requestCode == REQUEST_CODE_NOTIFICATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, you can now send notifications
            } else {
                // Permission denied, handle accordingly
                Log.w("MainActivity", "Notification permission denied.")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val uri: Uri = data.data!!
            // Grant URI permission for the duration of the app
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            // Handle the input stream (e.g., convert to Bitmap)
        }
    }
}
