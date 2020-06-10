package com.example.prm_project_ii_camera_gps.activities

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.example.prm_project_ii_camera_gps.R
import com.example.prm_project_ii_camera_gps.sliderImplementation.MyCustomGestureDetector

class GalleryActivity : AppCompatActivity() {

    lateinit var gesture : GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_activity)
        gesture = GestureDetectorCompat(this, MyCustomGestureDetector(
            this,
            ConfigurationActivity::class.java,
            MainCameraActivity::class.java
        ))
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        this.gesture.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

}