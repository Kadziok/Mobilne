package com.example.gtamapirl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun getMyLocation(view: View) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragment) as MapFragment
        mapFragment.setLocation()
    }
}