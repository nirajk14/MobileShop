package com.example.mobileshop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding


abstract class BaseActivity<T:ViewBinding> : AppCompatActivity() {
    private lateinit var binding: T
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = createBinding()
        setContentView(binding.root)

    }

    abstract fun createBinding(): T

    fun showInfoDialog(builder: AlertDialog.Builder): Boolean {

            builder.setTitle("This app was created by")
                .setMessage("Niraj Kushwaha")
                .setPositiveButton("OK") { dialogInterface, it ->
                    dialogInterface.cancel()
                }.show()
            return true

    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}