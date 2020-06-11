package com.example.prm_project_ii_camera_gps.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import androidx.core.view.GestureDetectorCompat
import com.example.prm_project_ii_camera_gps.R
import com.example.prm_project_ii_camera_gps.sliderImplementation.MyCustomGestureDetector
import com.example.prm_project_ii_camera_gps.utils.WaterMarkDrawer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.FileOutputStream

class MainCameraActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private lateinit var outputDirectory: File

    private  lateinit var gesture : GestureDetectorCompat
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gesture = GestureDetectorCompat(this, MyCustomGestureDetector(
            this,
            GalleryActivity::class.java,
            ConfigurationActivity::class.java
        ))
       fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        camera_capture_button.setOnClickListener { takePhoto() }

        outputDirectory = getOutputDirectory(this)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if(PHOTOS_LIST.isEmpty())
            loadData()
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        this.gesture.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder()
                .build()

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                cameraProvider.unbindAll()

                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)
                preview?.setSurfaceProvider(viewFinder.createSurfaceProvider())
            } catch(exc: Exception) {
                Log.e("TEST", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))

    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this) , object : ImageCapture.OnImageCapturedCallback() {

                override fun onCaptureSuccess(image: ImageProxy) {
                    image.use {
                        var bitmap: Bitmap = WaterMarkDrawer.imageProxyToBitmap(it)

                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location : Location? ->
                                bitmap = WaterMarkDrawer.putWaterMark(bitmap,  getLocationWaterMark(location))
                                PHOTOS_LIST.add(bitmap)
                                savePhoto(bitmap)
                            }
                        }
                }

                private fun getLocationWaterMark(location: Location?): String {
                    var waterMarkText = ""

                    try {
                        if (location != null) {
                            val geoCoder = Geocoder(baseContext, Locale.getDefault())
                            val address = geoCoder.getFromLocation(location.latitude, location.longitude, 1)

                            if (address.size > 0) {
                                waterMarkText += address[0].locality + " "
                                waterMarkText += address[0].countryName

                            } else throw Exception("Failed to get addresses")
                        } else throw Exception("Failed to get location")
                    } catch (e: Exception) {
                        Toast.makeText(baseContext, "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    return waterMarkText
                }

                private fun savePhoto(bitmap: Bitmap) {
                    val photoFile = File(
                        outputDirectory,
                        SimpleDateFormat(FILENAME_FORMAT, Locale.US
                        ).format(System.currentTimeMillis()) + ".jpg")

                    try {
                        FileOutputStream(photoFile).use {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                        }
                    } catch (e: Exception) {
                        Toast.makeText(baseContext, "Save failed", Toast.LENGTH_SHORT).show()
                        Log.e("TEST", "Save failed", e)
                    }

                    val msg = "Photo capture succeeded: $photoFile"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }

            })

    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    private fun loadData() {
        val outputDirectory = getOutputDirectory(this)
        val options =  BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        outputDirectory.let {
            if (!outputDirectory.listFiles().isNullOrEmpty()) {
                PHOTOS_LIST.addAll(
                    outputDirectory.listFiles()
                        .map {BitmapFactory.decodeFile(it.path, options)}
                        .toMutableList()
                )
            }
        }
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        var PHOTOS_LIST = mutableListOf<Bitmap>()

        fun getOutputDirectory(context :Context): File {
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, context.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else context.filesDir
        }
    }

}
