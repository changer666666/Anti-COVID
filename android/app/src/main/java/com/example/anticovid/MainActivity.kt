package com.example.anticovid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null
    val mAuth = FirebaseAuth.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initialize Firebase Auth
        auth = Firebase.auth
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        if(mAuth.currentUser != null){

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
                    logIn()
                } else {
                    // If sign in fails, display a message to the user.
                    auth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
                        .addOnCompleteListener(this){task ->
                            if (task.isSuccessful){
                                //Add to DB
                                logIn()
                            }else{
                                Toast.makeText(baseContext, "Authentication failed. Try Again",
                                    Toast.LENGTH_SHORT).show()
                            }

                    }
                    // ...
                }

                // ...
            }

    }
    fun logIn(){
        //Move to NextActivity
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
    }
}
