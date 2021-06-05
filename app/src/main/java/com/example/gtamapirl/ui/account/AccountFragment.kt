package com.example.gtamapirl.ui.account

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gtamapirl.MainActivity
import com.example.gtamapirl.R
import com.example.gtamapirl.databinding.FragmentAccountBinding
import com.example.gtamapirl.ui.map.MapFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.File


class AccountFragment : Fragment() {
    private lateinit var cUser: FirebaseUser
    private var binding: FragmentAccountBinding? = null
    private val storage = Firebase.storage.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = FirebaseDatabase.getInstance().reference

        binding = FragmentAccountBinding.bind(view)
        cUser = FirebaseAuth.getInstance().currentUser!!

        loadIcon()

        Picasso.get().load(cUser.photoUrl).into(binding!!.userIcon);
        binding!!.changeEmailText.setText(cUser.email)
        binding!!.changeNameText.setText(cUser.displayName)


        binding!!.changeEmail.setOnClickListener{
            val changeEmail = binding!!.changeEmailText.text.toString()
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirm change email")
            builder.setMessage("Are you sure change ${cUser.email} to ${changeEmail}?")
            builder.setPositiveButton("YES") { dialog, _ ->
                cUser.updateEmail(changeEmail)

                db.child("users")
                    .child(cUser.uid)
                    .child("email")
                    .setValue(changeEmail)

                (context as MainActivity).setUserData(cUser.displayName!!, changeEmail)

                Toast.makeText(
                    view.context,
                    getString(R.string.account_email_changed),
                    Toast.LENGTH_LONG
                ).show()

                dialog.dismiss()
            }
            builder.setNegativeButton(
                "NO"
            ) { dialog, which ->
                binding!!.changeEmailText.setText(cUser.email)
                dialog.dismiss()
            }
            val alert = builder.create()
            alert.show()
        }

        binding!!.changeName.setOnClickListener{
            val changeName = binding!!.changeNameText.text.toString()

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirm change name")
            builder.setMessage("Are you sure change ${cUser.displayName} to ${changeName}?")
            builder.setPositiveButton("YES") { dialog, _ ->
                cUser.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(changeName)
                        .build()
                )

                db.child("users")
                    .child(cUser.uid)
                    .child("name")
                    .setValue(changeName)

                (context as MainActivity).setUserData(changeName, cUser.email!!)

                Toast.makeText(
                    view.context,
                    getString(R.string.accout_name_changed),
                    Toast.LENGTH_LONG
                ).show()

                dialog.dismiss()
            }
            builder.setNegativeButton(
                "NO"
            ) { dialog, which ->
                binding!!.changeNameText.setText(cUser.displayName)
                dialog.dismiss()
            }
            val alert = builder.create()
            alert.show()


        }

        binding!!.resetPassword.setOnClickListener{
            FirebaseAuth.getInstance().sendPasswordResetEmail(cUser.email.toString())
            Toast.makeText(
                view.context,
                getString(R.string.account_resetPassword_msg) + " " + cUser.email.toString(),
                Toast.LENGTH_LONG
            ).show()
        }

        binding!!.logout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val action = AccountFragmentDirections.actionToHome()
            //findNavController().popBackStack()
            findNavController().navigate(action)
            (context as MainActivity).login()
        }

        binding!!.changeIcon.setOnClickListener{
            val action = AccountFragmentDirections.actionGoToPicker()
            findNavController().navigate(action)
        }
    }

    fun loadIcon() {
        val db= Firebase.database.reference

        db.child("users").child(cUser.uid).child("icon").get().addOnSuccessListener {
            val icon = it.value.toString()
            Log.e("ICON", icon)

            var defaultIconRef = storage.child("icons/${icon}")
            val localFile: File = File.createTempFile("tmp", "jpg")

            defaultIconRef.getFile(localFile)
                .addOnSuccessListener {
                    val bmp = BitmapFactory.decodeFile(localFile.absolutePath).scale(400, 400)
                    binding!!.userIcon.setImageBitmap(bmp)
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }
}