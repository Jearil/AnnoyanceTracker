package com.colintmiller.annoyancetracker

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_data_entry.*

class DataEntryActivity : AppCompatActivity() {
    val RC_SIGN_IN = 0
    var current : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_entry)

        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build())

        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build(),
        RC_SIGN_IN)

        button0.setOnClickListener { setRequest(0) }
        button1.setOnClickListener { setRequest(1) }
        button2.setOnClickListener { setRequest(2) }
        button3.setOnClickListener { setRequest(3) }
        button4.setOnClickListener { setRequest(4) }
        button5.setOnClickListener { setRequest(5) }
        button6.setOnClickListener { setRequest(6) }
        button7.setOnClickListener { setRequest(7) }
        button8.setOnClickListener { setRequest(8) }
        button9.setOnClickListener { setRequest(9) }
        button10.setOnClickListener { setRequest(10) }
        record.setOnClickListener { record() }
    }

    fun setRequest(number : Int) {
        chosen.text = number.toString()
        current = number
    }

    fun record() {
        if (current >= 0) {
            // record data
            val annoyance = Annoyance(current)
            val user = FirebaseAuth.getInstance().currentUser ?: return
            val database = FirebaseDatabase.getInstance().reference
            val ref = database.child("users").child(user.uid)
            ref.child("annoyances").push().setValue(annoyance)
            current = -1
            chosen.text = ""
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                // do the thing
                Toast.makeText(this, "Sign-in worked", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
