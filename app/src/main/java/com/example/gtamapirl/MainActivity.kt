package com.example.gtamapirl

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.scale
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.gtamapirl.data.UserData
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class MainActivity : AppCompatActivity() {

    private var cUser: FirebaseUser? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    var isLogin = false //TODO sprawdzać w każdym fragmencie czy zalogowany ;d

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_account, R.id.nav_events_list), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        cUser = FirebaseAuth.getInstance().currentUser
        if (cUser == null) {
            login()
        } else {
            isLogin = true
        }
    }

    fun login() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN
        )
    }

    fun updateTitle(title: String) {
        findViewById<Toolbar>(R.id.toolbar).title = title
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onSupportNavigateUp()
        val navController = findNavController(R.id.nav_host_fragment)
        navController.navigateUp(appBarConfiguration)
        if (isLogin){
            setUserData(cUser!!.displayName!!, cUser!!.email!!)
            setUserIcon()
        }
        return super.onSupportNavigateUp()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                cUser = FirebaseAuth.getInstance().currentUser!!
                setUserData(cUser!!.displayName!!, cUser!!.email!!)
                setUserIcon()
                val db = FirebaseDatabase.getInstance().reference
                db.child("users").child(cUser!!.uid).get().addOnSuccessListener {
                    when (it.value) {
                        null -> {
                            val user = UserData(cUser!!.displayName!!, cUser!!.email!!, "icon0.jpg")
                            db.child("users").child(cUser!!.uid).setValue(user)
                        }
                    }
                }.addOnFailureListener{
                    Log.e("USERS", "Error adding user to database")
                }

            }
        }


    }

    fun setUserData(name: String, email: String) {
        findViewById<TextView>(R.id.name).text = name
        findViewById<TextView>(R.id.email).text = email
    }

    fun setUserIcon(){
        val db =Firebase.database.reference
        db.child("users").child(cUser!!.uid).child("icon").get().addOnSuccessListener {
            val icon = it.value.toString()

            val storage = Firebase.storage.reference
            Log.e("ICON", icon)
            var defaultIconRef = storage.child("icons/${icon}")
            val localFile: File = File.createTempFile("tmp", "jpg")

            defaultIconRef.getFile(localFile)
                .addOnSuccessListener {
                    val bmp = BitmapFactory.decodeFile(localFile.absolutePath).scale(200, 200)
                    findViewById<ImageView>(R.id.imageView).setImageBitmap(bmp)
                }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    companion object {
        private const val RC_SIGN_IN = 1234
    }
}