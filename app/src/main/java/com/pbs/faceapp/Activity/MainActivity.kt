package com.pbs.faceapp.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.pbs.faceapp.Camera.CameraManager
import com.pbs.faceapp.Modal.FaceModal
import com.pbs.faceapp.R
import com.pbs.faceapp.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val imageCapture = ImageCapture.Builder().build()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(binding.root)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cameraManager = CameraManager(
            this,
            binding.cameraPreview,
            binding.viewGraphicOverlay,
            this,
            imageCapture
        )

        askCameraPermission()

        binding.captureBtn.setOnClickListener {
            captureImage(imageCapture,this@MainActivity)
        }

        binding.switchBtn.setOnClickListener {
            cameraManager.changeCamera()
        }



    }


    private fun askCameraPermission() {
        if (arrayOf(android.Manifest.permission.CAMERA).all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }) {
            cameraManager.cameraStart()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraManager.cameraStart()
        } else {
            Toast.makeText(this, "Camera Permission Denied!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun captureImage(imageCapture: ImageCapture, context: Context){

        imageCapture.takePicture(ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {

                @OptIn(ExperimentalGetImage::class)
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    cameraManager.cameraStop()
                    Toast.makeText(this@MainActivity, "Processing....", Toast.LENGTH_SHORT).show()
                    val bitmap  = image.toBitmap()
                    detectFaces(bitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.d("post", exception.toString())
                }
            })
    }


    private fun detectFaces(bitmap: Bitmap){

        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()


        val detector = FaceDetection.getClient(highAccuracyOpts)
        val image = InputImage.fromBitmap(bitmap, 0)
        var faceNumber = 1
        val faceResultList = mutableListOf<FaceModal>()

        val result = detector.process(image).addOnSuccessListener { faces->
                for (face in faces) {
                    val faceNo = faceNumber
                    val smileProbability = face.smilingProbability?.times(100)
                    val leftEye = face.leftEyeOpenProbability?.times(100)
                    val rightEye = face.rightEyeOpenProbability?.times(100)

                    faceResultList.add(FaceModal(faceNo,smileProbability ?: 1f,leftEye ?: 1f,rightEye ?: 1f))
                    faceNumber++
            }


            val intent = Intent(this@MainActivity, PreviewActivity::class.java)
            intent.putParcelableArrayListExtra("faceList",ArrayList(faceResultList))
            startActivity(intent)
            cameraManager.cameraStart()
        }
    }

}