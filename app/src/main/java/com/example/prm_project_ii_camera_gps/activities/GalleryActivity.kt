package com.example.prm_project_ii_camera_gps.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.prm_project_ii_camera_gps.R
import com.r0adkll.slidr.Slidr

class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_activity)
        Slidr.attach(this)
    }

}