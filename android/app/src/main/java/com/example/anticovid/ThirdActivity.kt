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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.third_activity)

        var passed = ArrayList<String>()
        var textshow = "Summary:"

        passed = intent.getStringArrayListExtra("KEY")
        for( i in passed){
            textshow += "\n" + i
        }

//        summaryTextView.text = textshow
    }

}