package com.example.prm_project_ii_camera_gps.activities

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.prm_project_ii_camera_gps.R
import com.example.prm_project_ii_camera_gps.sliderImplementation.MyCustomGestureDetector
import com.example.prm_project_ii_camera_gps.utils.GalleryRecyclerAdapter
import kotlinx.android.synthetic.main.gallery_activity.*

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
        Log.d("TEST", "SIZZZEEE! ${MainCameraActivity.PHOTOS_LIST.size}")

        val recyclerAdapter = GalleryRecyclerAdapter(this, MainCameraActivity.PHOTOS_LIST)
        recyclerAdapter.setHasStableIds(true)
        recycler_view.setHasFixedSize(true)
        recycler_view.setItemViewCacheSize(20);
        recycler_view.layoutManager = GridLayoutManager(this, 3)
        recycler_view.adapter = recyclerAdapter
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        this.gesture.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

}