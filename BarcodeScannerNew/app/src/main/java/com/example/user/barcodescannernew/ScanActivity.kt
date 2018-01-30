package com.example.user.barcodescannernew

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException
import kotlinx.android.synthetic.main.activity_scan.cameraView

class ScanActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SCAN ACTIVITY"
    }

    lateinit var surfaceHolder: SurfaceHolder
    lateinit var barcodeDetector: BarcodeDetector
    lateinit var cameraSource: CameraSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        Log.e(TAG, "onCreate: " + "SCAN ACTIVITY")

//        cameraView = findViewById(R.id.cameraView)
        cameraView.setZOrderMediaOverlay(true)

        surfaceHolder = cameraView.holder

        barcodeDetector = BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build()

        if (!barcodeDetector.isOperational) {
            Toast.makeText(baseContext, "Could Not setup the detector", Toast.LENGTH_SHORT).show()
            this.finish()
        }

        cameraSource = CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24f)
                .setRequestedPreviewSize(1920, 1024)
                .build()

        cameraView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
                try {
                    if (ContextCompat.checkSelfPermission(this@ScanActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(cameraView.holder)
                        Log.e(TAG, "surfaceCreated: " + "camera started")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
            }

            override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
            }
        })

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() > 0) {
                    Log.e(TAG, "receiveDetections: " + "received code")
                    val intent = Intent()
                    intent.putExtra("Barcode", barcodes.valueAt(0))
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        })
    }
}
