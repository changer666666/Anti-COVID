package com.example.anticovid

import android.Manifest
import android.content.Intent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import com.google.firebase.database.*
import androidx.core.app.ActivityCompat
import android.widget.TextView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Thread.sleep


const val DEVICE_NAME = "bluet"


//    var radioGroup: RadioGroup? = null
//    lateinit var radioButton: RadioButton
//    private lateinit var button: Button
    private lateinit var emailVali:String

    //add Firebase Database stuff
    private lateinit var database: DatabaseReference// ...

class ThirdActivity: AppCompatActivity(), BLEControl.Callback {

    // Bluetooth
    private var ble: BLEControl? = null
    private var rssiAverage: Double = 0.0
    private var messages: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.third_activity)
        title = "AntiCOVID - WALK IN CLINIC"

        var passed = ArrayList<String>()
        var textshow: String = "Summary:"
        passed = intent.getStringArrayListExtra("KEY")
        var counter = 0
        for (i in passed) {
            if (counter == 3) {
                emailVali = i
            }
            textshow += i + "\n"
            counter++
        }
        //Summary View Text
//        var textshow = "Summary:"
//        summaryTextView.text = textshow

        //Read data from firebase
        database = FirebaseDatabase.getInstance().getReference()
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if (dataSnapshot!!.exists()) {
                    Log.d("hi", dataSnapshot.toString())
                    var isAvailable = dataSnapshot.child("isAvailable").getValue().toString()
                    var emailCheck = dataSnapshot.child("curPatient").getValue().toString()
                    if (isAvailable == "True" && emailCheck == emailVali) {
                            //send signal to Arduino
                            ble!!.send("signal")
                            Log.i("BLE", "Signal sent")
                            writeLine("Your turn...")
                            writeLine("Please confirm with any button on the Arduino board")
                            sleep(2000);
                            Log.d("hi", "it works & delay")

                    }
                    Log.d("hi", "current patient email" + emailVali)

                }
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("loadPost:onCancelled", databaseError.toException())
                Log.d("hi", "")
                // ...
            }
        }
        database.addValueEventListener(postListener)

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        val adapter: BluetoothAdapter?
        adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter != null) {
            if (!adapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)

            }
        }

        // Get Bluetooth
        messages = findViewById(R.id.bluetoothText)
        messages!!.movementMethod = ScrollingMovementMethod()
        ble = BLEControl(applicationContext, DEVICE_NAME)

        // Check permissions
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 1
        )
        startScan()
    }

    override fun onRSSIread(uart: BLEControl, rssi: Int) {
        rssiAverage = rssi.toDouble()
        writeLine("RSSI $rssiAverage")
    }

    override fun onResume() {
        super.onResume()
        //updateButtons(false)
        ble!!.registerCallback(this)
    }

    override fun onStop() {
        super.onStop()
        ble!!.unregisterCallback(this)
        ble!!.disconnect()
    }

    fun connect(v: View) {
        startScan()
    }

    private fun startScan() {
        writeLine("Scanning for devices ...")
        ble!!.connectFirstAvailable()
    }

    /**
     * Writes a line to the messages textbox
     * @param text: the text that you want to write
     */
    private fun writeLine(text: CharSequence) {
        runOnUiThread {
            messages!!.append(text)
            messages!!.append("\n")
        }
    }

    /**
     * Called when a UART device is discovered (after calling startScan)
     * @param device: the BLE device
     */
    override fun onDeviceFound(device: BluetoothDevice) {
        Log.d("tag", "onDeviceFound")
        writeLine("Found device : " + device.name)
        writeLine("Waiting for a connection ...")
    }

    /**
     * Prints the devices information
     */
    override fun onDeviceInfoAvailable() {
        writeLine(ble!!.deviceInfo)
    }

    /**
     * Called when UART device is connected and ready to send/receive data
     * @param ble: the BLE UART object
     */
    override fun onConnected(ble: BLEControl) {
        writeLine("Connected!")
        writeLine("You are on the waiting list...")
    }

    /**
     * Called when some error occurred which prevented UART connection from completing
     * @param ble: the BLE UART object
     */
    override fun onConnectFailed(ble: BLEControl) {
        writeLine("Error connecting to device!")
    }

    /**
     * Called when the UART device disconnected
     * @param ble: the BLE UART object
     */
    override fun onDisconnected(ble: BLEControl) {
        writeLine("Disconnected!")
    }

    /**
     * Called when data is received by the UART
     * @param ble: the BLE UART object
     * @param rx: the received characteristic
     */
    override fun onReceive(ble: BLEControl, rx: BluetoothGattCharacteristic) {
//        writeLine("Received value: " + rx.getStringValue(0))
        if(rx.getStringValue(0) == "m") {
            Log.d("tag", "move current")
            Firebase.database.getReference().child("isAvailable").setValue("False")
            writeLine("Confirmed !")
        }
    }

    companion object {
        private val REQUEST_ENABLE_BT = 0
    }
}

