package com.example.prm_project_ii_camera_gps.utils

import android.graphics.*
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer

object WaterMarkDrawer {

    fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val matrix = Matrix()
        matrix.postRotate(90f)
        val notRotated = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        return Bitmap.createBitmap(notRotated,0,0, notRotated.width, notRotated.height, matrix, true)
    }

    fun putWaterMark(src : Bitmap, watermark : String) : Bitmap{
        val result = Bitmap.createBitmap(src.width, src.height, src.config)
        val paint = Paint()

        val canvas = Canvas(result)
        canvas.drawBitmap(src, 0F, 0F, null)

        paint.color = Color.GRAY
        paint.alpha = 40
        paint.textSize = 300F
        paint.isAntiAlias = true
        paint.isUnderlineText = true
        canvas.drawText(watermark, 150F, 350F, paint)

        return result
    }


}