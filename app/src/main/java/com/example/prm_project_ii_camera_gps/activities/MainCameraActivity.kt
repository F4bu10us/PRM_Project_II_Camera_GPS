package com.example.prm_project_ii_camera_gps.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.prm_project_ii_camera_gps.R
import com.r0adkll.slidr.Slidr

class MainCameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Slidr.attach(this)
    }
}
