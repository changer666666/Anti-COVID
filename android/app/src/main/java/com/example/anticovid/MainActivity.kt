package com.example.anticovid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Timestamp
import java.util.*


class MainActivity : AppCompatActivity() {
    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null
    var nameEditText: EditText? = null
    val mAuth = FirebaseAuth.getInstance()
    var rightNow: Calendar = Calendar.getInstance()
    var timestamp: Timestamp = Timestamp(System.currentTimeMillis())
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initialize Firebase Auth
        auth = Firebase.auth
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        if(mAuth.currentUser != null){
            logIn()
        }
    }

    fun send_second(view: View){
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
    }
    fun goClicked(view:View){
        //Check if we can log in the user
        auth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Firebase.database.getReference().child("Waiting List").child(nameEditText?.text.toString()).child(task.result?.user!!.uid)
                    Firebase.database.getReference().child("Waiting List").child(nameEditText?.text.toString()).child("email").setValue(emailEditText?.text.toString())
                    Firebase.database.getReference().child("Waiting List").child(nameEditText?.text.toString()).child("Time").setValue(rightNow)
                    Firebase.database.getReference().child("Waiting List").child(nameEditText?.text.toString()).child("Timestamp").setValue(timestamp.time)
                    logIn()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed. Try Again",
                        Toast.LENGTH_SHORT).show()
                    }
                    // ...
                }

                // ...
            }

    fun signupClicked(view: View) {
        auth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
            .addOnCompleteListener(this){task ->
                if (task.isSuccessful){
                    //Add to DB
                    storeUserInfo()
                    Firebase.database.getReference().child("Waiting List").child(nameEditText?.text.toString()).child(task.result?.user!!.uid)
                    Firebase.database.getReference().child("Waiting List").child(nameEditText?.text.toString()).child("email").setValue(emailEditText?.text.toString())
                    Firebase.database.getReference().child("Waiting List").child(nameEditText?.text.toString()).child("Time").setValue(rightNow)
                    Firebase.database.getReference().child("Waiting List").child(nameEditText?.text.toString()).child("Timestamp").setValue(timestamp.time)

                    //Login the user
                    logIn()
                }else{
                    Toast.makeText(baseContext, "Authentication failed. Try Again",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    fun logIn(){
        //Move to NextActivity
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
    }
    fun storeUserInfo(){
        //Firebase.database.getReference().child("patients").child(nameEditText?.text.toString()).child(task.result?.user!!.uid)
        Firebase.database.getReference().child("patients").child(nameEditText?.text.toString()).child("email").setValue(emailEditText?.text.toString())
        Firebase.database.getReference().child("patients").child(nameEditText?.text.toString()).child("Time").setValue(rightNow)
        Firebase.database.getReference().child("patients").child(nameEditText?.text.toString()).child("Timestamp").setValue(timestamp.time)
    }


}
