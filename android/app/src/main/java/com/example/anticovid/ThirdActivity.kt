package com.example.anticovid

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.third_activity.*

class ThirdActivity: AppCompatActivity() {

    var radioGroup: RadioGroup? = null
    lateinit var radioButton: RadioButton
    private lateinit var button: Button
    private lateinit var emailVali:String


    //add Firebase Database stuff
    private lateinit var database: DatabaseReference// ...


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.third_activity)



        var passed = ArrayList<String>()
        var textshow: String = ""
        passed = intent.getStringArrayListExtra("KEY")
        var counter = 0
        for (i in passed) {
            if (counter == 3){
                emailVali = i
            }
            textshow += i + "\n"
            counter ++
        }
        //Read data from firebase
        Log.d("hi","readdata")
        database = FirebaseDatabase.getInstance().getReference()
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot!!.exists()){
                    Log.d("hi", dataSnapshot.toString())
                    var isAvailable = dataSnapshot.child("isAvailable").getValue().toString()
                    var emailCheck = dataSnapshot.child("curPatient").getValue().toString()
                    if (isAvailable == "True"){
                        if (emailCheck == emailVali ){
                            Log.d("hi", "it works")

                        }
                    }
                    Log.d("hi", "current patient email" + emailVali)

                }
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w( "loadPost:onCancelled", databaseError.toException())
                Log.d("hi","")
                // ...
            }
        }
        database.addValueEventListener(postListener)

        summaryTextView.text = textshow

        // Is the button now checked
        title = "KotlinApp"
        radioGroup = findViewById(R.id.radio_group)
        button = findViewById(R.id.button_pay)
        button.setOnClickListener {
            val intSelectButton: Int = radioGroup!!.checkedRadioButtonId
            radioButton = findViewById(intSelectButton)

            var forprint = radioButton.text
            Log.d("tag", "$forprint")

//            Toast.makeText(this, radioButton.text, Toast.LENGTH_SHORT).show()

        }
/*
        val checked = view.isChecked

        // Check which radio button was clicked
        when (view.getId()) {
            R.id.buCredit -> if (checked) {
                Log.d("tag", "buCredit")
                Toast.makeText(this,"Credit",Toast.LENGTH_SHORT).show()
            }
            R.id.buOthers -> if (checked) {
                Log.d("tag", "buOthers")
                Toast.makeText(this,"Others",Toast.LENGTH_SHORT).show()
            }

//        if (view is RadioButton){}

 */

        /*
        // Get radio group selected item using on checked change listener
        radio_group.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                Toast.makeText(this," On checked change : ${radio.text}", Toast.LENGTH_SHORT).show()
            })
        // Get radio group selected status and text using button click event
        button.setOnClickListener{
            // Get the checked radio button id from radio group
            var id: Int = radio_group.checkedRadioButtonId
            if (id!=-1){ // If any radio button checked from radio group
                // Get the instance of radio button using id
                val radio:RadioButton = findViewById(id)
                Toast.makeText(this,"On button click : ${radio.text}", Toast.LENGTH_SHORT).show()
            }else{
                // If no radio button checked in this radio group
                Toast.makeText(this,"On button click : nothing selected", Toast.LENGTH_SHORT).show()
            }
        }
        */
        //Datalistener
        //if (post.vale.curPatient == "Name"){
                 //ble.sent;
        }
    }

    fun onRadioButtonClicked(view: View) {
        Log.d("tag", "clicked")
    }

