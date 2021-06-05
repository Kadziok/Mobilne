package com.example.gtamapirl.ui.account

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.scale
import androidx.navigation.fragment.findNavController
import com.example.gtamapirl.MainActivity
import com.example.gtamapirl.R
import com.example.gtamapirl.databinding.FragmentAccountBinding
import com.example.gtamapirl.databinding.FragmentEventBinding
import com.example.gtamapirl.databinding.FragmentIconPickerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File


class IconPickerFragment : Fragment() {

    private lateinit var cUser: FirebaseUser
    private var binding: FragmentIconPickerBinding? = null
    private val storage = Firebase.storage.reference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_icon_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentIconPickerBinding.bind(view)
        cUser = FirebaseAuth.getInstance().currentUser!!

        setIcon(binding!!.i0, "icon0.jpg")
        setIcon(binding!!.i1, "icon1.jpg")
        setIcon(binding!!.i2, "icon2.jpg")
        setIcon(binding!!.i3, "icon3.jpg")
        setIcon(binding!!.i4, "icon4.jpg")
        setIcon(binding!!.i5, "icon5.jpg")
    }
    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IconPickerFragment().apply {}
    }

    private fun setIcon(imageView: ImageView, iconName: String) {
        val db= Firebase.database.reference

        var iconRef = storage.child("icons/${iconName}")
        val localFile: File = File.createTempFile("tmp", "jpg")
        iconRef.getFile(localFile)
            .addOnSuccessListener {
                val bmp = BitmapFactory.decodeFile(localFile.absolutePath).scale(400, 400)
                imageView.setImageBitmap(bmp)
            }

        imageView.setOnClickListener{
            db.child("users").child(cUser!!.uid).child("icon").setValue(iconName)
            (activity as MainActivity).setUserIcon()
            val action = IconPickerFragmentDirections.actionReturnToAccount()
            findNavController().navigate(action)
        }

    }
}