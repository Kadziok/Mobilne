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
                R.string.account_resetPassword_msg.toString() + cUser.email.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
        view.findViewById<Button>(R.id.test).setOnClickListener{
            val db = FirebaseDatabase.getInstance().reference

            db.child("user_events").child(cUser.uid).child("event_1").setValue(EventElement("event_1", "goes"))
            db.child("user_events").child(cUser.uid).child("event_2").setValue(EventElement("event_2", "not"))
            db.child("user_events").child(cUser.uid).child("event_3").setValue(EventElement("event_3", "undecided"))
        }
    }
}