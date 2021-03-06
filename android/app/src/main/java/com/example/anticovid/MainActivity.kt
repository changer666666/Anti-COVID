package com.example.anticovid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
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
    private lateinit var auth: FirebaseAuth
    private lateinit var timestamp: Timestamp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //hide action bar
        val actionBar = supportActionBar
        actionBar!!.hide()

        // Initialize Firebase Auth
        auth = Firebase.auth
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        if(mAuth.currentUser != null) {
            logIn()
        }
    }

    fun goClicked(view:View){
        //Check if we can log in the user
        timestamp = Timestamp(System.currentTimeMillis())
        auth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    Firebase.database.getReference().child("Waiting List").child(nameEditText?.text.toString()).child("email").setValue(emailEditText?.text.toString())
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
        timestamp = Timestamp(System.currentTimeMillis())
        auth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
            .addOnCompleteListener(this){task ->
                if (task.isSuccessful){
                    //Add to DB
                    storeUserInfo(task)
                    Firebase.database.getReference().child("Waiting List").child(nameEditText?.text.toString()).child("email").setValue(emailEditText?.text.toString())
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
        var list = emailEditText?.text.toString()

        val intent = Intent(this, SecondActivity::class.java)
        intent. putExtra(KEY, list)
        startActivityForResult(intent, SecondActivity.RCODE)
    }

    fun storeUserInfo(task: Task<AuthResult>){
        Firebase.database.getReference().child("patients").child(nameEditText?.text.toString()).child(task.result?.user!!.uid)
        Firebase.database.getReference().child("patients").child(nameEditText?.text.toString()).child("email").setValue(emailEditText?.text.toString())
        Firebase.database.getReference().child("patients").child(nameEditText?.text.toString()).child("Timestamp").setValue(timestamp.time)
    }

    companion object{
        const val KEY = "Email"
        const val RCODE = 321
    }


}
