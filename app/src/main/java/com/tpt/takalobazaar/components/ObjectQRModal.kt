package com.tpt.takalobazaar.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tpt.takalobazaar.models.Object
import com.tpt.takalobazaar.services.generateQRCodeBitmap
import com.tpt.takalobazaar.services.saveBitmapToGallery
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ObjectQRModal(
    obj: Object,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Generate QR Code
    val qrBitmap = remember(obj.id) {
        generateQRCodeBitmap(obj.id.toString())
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            color = Color.White,
            shape = MaterialTheme.shapes.medium,
            elevation = 8.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Object Name
                Text(
                    text = obj.name,
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // QR Code Image
                qrBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            qrBitmap?.let { bitmap ->
                                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                val fileName = "${obj.name}_$timestamp"
                                saveBitmapToGallery(context, bitmap, fileName)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Sauvegarder",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sauvegarder",
                            style = MaterialTheme.typography.subtitle2.copy(color = Color.White)
                        )
                    }

                    OutlinedButton(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.Black)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Fermer",
                            style = MaterialTheme.typography.subtitle2.copy(color = Color.White)
                        )
                    }
                }
            }
        }
    }
}
