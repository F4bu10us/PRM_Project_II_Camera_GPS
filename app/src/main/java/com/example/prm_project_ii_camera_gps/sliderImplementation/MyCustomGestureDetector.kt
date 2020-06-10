package com.example.prm_project_ii_camera_gps.sliderImplementation

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity

private const val DEBUG_TAG = "Gestures"

class MyCustomGestureDetector<L,R>(
    private val appActivity: AppCompatActivity,
    private val leftDestination: Class<L>,
    private val rightDestination: Class<R>
) : GestureDetector.SimpleOnGestureListener() {

    override fun onFling(
        event1: MotionEvent?,
        event2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if(velocityX>3000)onSwipeLeft()
        else if(velocityX<-3000)onSwipeRight()
//        Log.d(DEBUG_TAG, "onFling: $velocityX $velocityY")
        return true
    }

    private fun onSwipeLeft(){
        leftDestination.let { appActivity.startActivity(Intent(appActivity, it)) }
        appActivity.overridePendingTransition(
            appActivity.resources.getIdentifier("slide_in_left", "anim", appActivity.packageName)
            , appActivity.resources.getIdentifier("slide_out_right", "anim", appActivity.packageName)
        )
    }
    private fun onSwipeRight(){
        rightDestination.let { appActivity.startActivity(Intent(appActivity, it)) }
        appActivity.overridePendingTransition(
            appActivity.resources.getIdentifier("slide_in_right", "anim", appActivity.packageName)
            , appActivity.resources.getIdentifier("slide_out_left", "anim", appActivity.packageName)
        )
    }
}