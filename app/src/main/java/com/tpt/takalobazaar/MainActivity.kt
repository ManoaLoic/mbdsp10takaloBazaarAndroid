package com.tpt.takalobazaar

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.tpt.takalobazaar.providers.LocalNavHost
import com.tpt.takalobazaar.screens.holder.HolderScreen
import com.tpt.takalobazaar.ui.theme.StoreTheme
import com.tpt.takalobazaar.utils.LocalScreenSize
import com.tpt.takalobazaar.utils.getScreenSize
import com.tpt.takalobazaar.api.RetrofitInstance
import com.tpt.takalobazaar.services.SessionService
import dagger.hilt.android.AndroidEntryPoint
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val REQUEST_CODE_READ_STORAGE = 1001
    private val PICK_IMAGE_REQUEST = 1002

    @Inject
    lateinit var sessionService: SessionService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
                    // A surface container using the 'background' color from the theme
                    Surface(modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background) {
                        HolderScreen(
                            onStatusBarColorChange = {
                                statusBarColor = it.toArgb()
                            },
                            navController = navController
                        )
                    }
                }
            }
        }
        RetrofitInstance.initialize(sessionService)
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
