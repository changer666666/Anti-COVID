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
        var textshow = "Summary:"

        passed = intent.getStringArrayListExtra("KEY")
        for( i in passed){
            textshow += "\n" + i
        }

        summaryTextView.text = textshow

        // Is the button now checked
        radioGroup = findViewById(R.id.radio_group)
        button = findViewById(R.id.button_pay)
        buCredit.isChecked = true

        button.setOnClickListener {
            val intSelectButton: Int = radioGroup!!.checkedRadioButtonId
            radioButton = findViewById(intSelectButton)

            var forprint = radioButton.text
            Log.d("tag", "$forprint")

//            Toast.makeText(this, radioButton.text, Toast.LENGTH_SHORT).show()

        }
    }

    fun onRadioButtonClicked(view: View) {
        Log.d("tag", "clicked")
    }

}