package com.example.googlevision

import android.app.Application
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo

class VisionApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RxPaparazzo.register(this)
    }
}