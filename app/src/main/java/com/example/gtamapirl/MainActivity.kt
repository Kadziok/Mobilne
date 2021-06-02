package com.example.gtamapirl

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference

const val RC_SIGN_IN = 1234

class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var cUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .build(),
            RC_SIGN_IN
        )

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                cUser = FirebaseAuth.getInstance().currentUser!!
            }
        }
    }





    fun getMyLocation(view: View) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragment) as MapFragment
        mapFragment.setLocation()
    }
}