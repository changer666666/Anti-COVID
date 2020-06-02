package com.example.anticovid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    private lateinit var serviceSpinner: Spinner
    private lateinit var doctorSpinner: Spinner
    private lateinit var insuranceSpinner: Spinner

    private lateinit var pickedService : String
    private lateinit var pickedDr: String
    private lateinit var pickedInsurance: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity)

        serviceSpinner = findViewById(R.id.spinner1)
        doctorSpinner = findViewById(R.id.spinner2)
        insuranceSpinner = findViewById(R.id.spinner3)

        val serviceOp = resources.getStringArray(R.array.service_option)
        val doctorOp = resources.getStringArray(R.array.doctor_option)
        val insuranceOp = resources.getStringArray(R.array.insurance_option)

        serviceSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, serviceOp)
        doctorSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, doctorOp)
        insuranceSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, insuranceOp)

        serviceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
//                Toast.makeText( this@SecondActivity, "Select one to continue", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long){
                pickedService = serviceOp.get(position)
                Log.d("tag", "service: $pickedService")
//                Toast.makeText( this@SecondActivity, "option: $pickedService", Toast.LENGTH_SHORT).show()
            }
        }

        doctorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //do nothing
            }

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                pickedDr = doctorOp.get(position)
//                Toast.makeText(this@SecondActivity, "option: $pickedDr", Toast.LENGTH_SHORT).show()
            }
        }

        insuranceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //do nothing
            }

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                pickedInsurance = insuranceOp.get(position)
//                Toast.makeText(this@SecondActivity, "option: $pickedInsurance", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun confirmed(view: View){
        if(pickedService == "select a service"){
            Toast.makeText(this@SecondActivity, "Please select a service", Toast.LENGTH_SHORT).show()
        }else if(pickedDr == "select a Doctor"){
            Toast.makeText(this@SecondActivity, "Please select a Doctor", Toast.LENGTH_SHORT).show()
        }
        else if(pickedInsurance == "select your insurance"){
            Toast.makeText(this@SecondActivity, "Please select your insurance", Toast.LENGTH_SHORT).show()
        }else{
            var list = ArrayList<String>()
            list.add(pickedService)
            list.add(pickedDr)
            list.add(pickedInsurance)

            val intent = Intent(this, ThirdActivity::class.java)
            intent.putExtra(KEY, list)
            startActivityForResult(intent, RCODE)
        }

    }

    companion object{
        const val KEY = "KEY"
        const val RCODE = 123
    }
}