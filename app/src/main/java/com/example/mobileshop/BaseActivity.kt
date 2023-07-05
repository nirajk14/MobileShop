package com.example.mobileshop

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import com.example.mobileshop.utils.Constants.CHANNEL_ID
import com.google.android.material.chip.Chip


abstract class BaseActivity<T:ViewBinding> : AppCompatActivity() {
    private lateinit var binding: T
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = createBinding()
        setContentView(binding.root)
        createNotificationChannel()

    }

    abstract fun createBinding(): T

    fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showInfoDialog(builder: AlertDialog.Builder): Boolean {

            builder.setTitle("This app was created by")
                .setMessage("Niraj Kushwaha")
                .setPositiveButton("OK") { dialogInterface, it ->
                    dialogInterface.cancel()
                }.show()
            return true

    }

    fun View.hide() {
        visibility = View.GONE
    }

    fun View.show() {
        visibility = View.VISIBLE
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun createChip(chipData: String): Chip {
        val chip = Chip(this)
        chip.text = chipData
        chip.isClickable = true
        chip.setChipBackgroundColorResource(R.color.gray)
        // Set any other customizations for the chip as needed
        return chip

    }

}