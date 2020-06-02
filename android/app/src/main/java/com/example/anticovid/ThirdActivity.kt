package com.example.anticovid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.third_activity.*

class ThirdActivity: AppCompatActivity() {

    var radioGroup: RadioGroup? = null
    lateinit var radioButton: RadioButton
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.third_activity)

        var passed = ArrayList<String>()
        var textshow : String = ""
        passed = intent.getStringArrayListExtra("KEY")
        for( i in passed){
            textshow += i + "\n"
        }

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
    }

    fun onRadioButtonClicked(view: View) {
        Log.d("tag", "clicked")
    }

}