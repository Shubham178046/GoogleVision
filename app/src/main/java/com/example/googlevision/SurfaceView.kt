package com.example.googlevision

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import kotlinx.android.synthetic.main.activity_surface_view.*
import java.lang.Exception
import java.lang.StringBuilder
import kotlin.properties.Delegates

class SurfaceView : AppCompatActivity() {
        var cameraSource: CameraSource? = null
        var REQUETSPERMISSIONID: Int = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surface_view)

        var textRecognizer: TextRecognizer = TextRecognizer.Builder(applicationContext).build()
        if (!textRecognizer.isOperational) {
            Log.d(
                "Surface",
                "onCreate() called with: savedInstanceState = $savedInstanceState" + "Detector Dependency Are Not Yet Avaiable"
            )
        } else {
            cameraSource = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .build()

            surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(p0: SurfaceHolder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(
                                this@SurfaceView,
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this@SurfaceView,
                                arrayOf(Manifest.permission.CAMERA),
                                REQUETSPERMISSIONID
                            )
                            return
                        }
                        cameraSource!!.start(surfaceView.holder)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                }

                override fun surfaceDestroyed(p0: SurfaceHolder) {
                    cameraSource!!.stop()
                }

            })

            textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
                override fun release() {
                    TODO("Not yet implemented")
                }

                override fun receiveDetections(detection: Detector.Detections<TextBlock>?) {
                    val items: SparseArray<TextBlock> = detection!!.detectedItems
                    if (items.size() != 0) {
                        txtReadText.post(object : Runnable {
                            override fun run() {
                                var stringBuilder: StringBuilder = StringBuilder()
                                for (i in 0 until items.size()) {
                                    var item: TextBlock = items.valueAt(i)
                                    stringBuilder.append(item.value)
                                    stringBuilder.append("\n")

                                }
                                txtReadText.setText(stringBuilder.toString())
                            }
                        })
                    }
                }

            })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUETSPERMISSIONID -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(
                            this@SurfaceView,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    cameraSource!!.start(surfaceView.holder)
                }
            }
        }
    }
}