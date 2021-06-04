package com.example.gtamapirl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class AccountFragment : Fragment() {
    private lateinit var cUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cUser = FirebaseAuth.getInstance().currentUser!!
        val mailEdit = view.findViewById<EditText>(R.id.editTextTextEmailAddress)
        mailEdit.setText(cUser.email.toString())

        view.findViewById<Button>(R.id.changeMail).setOnClickListener{
            val t = mailEdit.text.toString()
            cUser.updateEmail(t)
        }

        view.findViewById<Button>(R.id.resetPassword).setOnClickListener{
            FirebaseAuth.getInstance().sendPasswordResetEmail(cUser.email.toString())
            Toast.makeText(
                view.context,
                getString(R.string.account_resetPassword_msg) + " " + cUser.email.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}