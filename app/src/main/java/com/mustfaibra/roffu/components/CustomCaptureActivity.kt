package com.mustfaibra.roffu.components

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.ViewfinderView
import com.journeyapps.barcodescanner.camera.CameraSettings
import com.mustfaibra.roffu.R

class CustomCaptureActivity : CaptureActivity() {

    private lateinit var barcodeScannerView: DecoratedBarcodeView

    override fun initializeContent(): DecoratedBarcodeView {
        setContentView(R.layout.custom_capture_activity)

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner)

        // Set camera settings for vertical orientation
        val cameraSettings = CameraSettings()
        cameraSettings.isAutoFocusEnabled = true
        barcodeScannerView.cameraSettings = cameraSettings

        // Customize the viewfinder here
        val viewFinderView = barcodeScannerView.viewFinder
        viewFinderView.setLaserVisibility(false)

        return barcodeScannerView
    }

    // These overrides might not be necessary anymore, but let's keep them just in case
    fun getViewFinder(): ViewfinderView {
        return barcodeScannerView.viewFinder
    }

    fun getBarcodeView(): BarcodeView {
        return barcodeScannerView.barcodeView
    }
}

class SquareViewfinderView(context: Context, attrs: AttributeSet) : ViewfinderView(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        setMeasuredDimension(width, width)
    }
}