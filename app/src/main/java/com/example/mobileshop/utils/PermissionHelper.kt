package com.example.mobileshop.utils

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mobileshop.main_view.MainActivity
import timber.log.Timber

class PermissionHelper(private val activity: MainActivity)  {
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    var isReadPermissionGranted = false
    var isWritePermissionGranted = false
    var isCameraPermissionGranted = false

    fun requestCameraPermission() {

        isCameraPermissionGranted = (ContextCompat.checkSelfPermission(activity,
            android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)
        if(!isCameraPermissionGranted)
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.CAMERA),100)
    }



    fun requestPermission() {

        permissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            isReadPermissionGranted = permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
            isWritePermissionGranted = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionGranted
        }


        isReadPermissionGranted = if (Build.VERSION.SDK_INT >= 33){
            ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
                    &&
                    ContextCompat.checkSelfPermission(
                        activity,
                        android.Manifest.permission.READ_MEDIA_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED

                    &&
                    ContextCompat.checkSelfPermission(
                        activity,
                        android.Manifest.permission.READ_MEDIA_VIDEO
                    ) == PackageManager.PERMISSION_GRANTED
        } else

            ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        isWritePermissionGranted = ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED





        val permissionRequest: MutableList<String> = ArrayList()

        if (!isReadPermissionGranted){

            if (Build.VERSION.SDK_INT >= 33) {
                permissionRequest.add(android.Manifest.permission.READ_MEDIA_AUDIO)
                permissionRequest.add(android.Manifest.permission.READ_MEDIA_IMAGES)
                permissionRequest.add(android.Manifest.permission.READ_MEDIA_VIDEO)
            } else{
            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)}
        }
        if (!isWritePermissionGranted){
            permissionRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }


//todo        Timber.d(permissionRequest)


        if (permissionRequest.isNotEmpty()){
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

}