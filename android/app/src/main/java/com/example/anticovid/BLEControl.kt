package com.example.anticovid

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.content.Context
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.Queue
import java.util.UUID
import java.util.WeakHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Semaphore

class BLEControl(private val context: Context, deviceName: String):BluetoothGattCallback(), BluetoothAdapter.LeScanCallback {

    private val callbacks: WeakHashMap<Callback, Any>
    private var mRSSI:Int=0

    private val adapter: BluetoothAdapter?
    // Return instance of BluetoothGatt.
    var gatt: BluetoothGatt? = null
        private set
    private var tx: BluetoothGattCharacteristic? = null
    private var rx: BluetoothGattCharacteristic? = null
    private var connectFirst: Boolean = false
    //private boolean writeInProgress; // Flag to indicate a write is currently in progress
    private val writeInProgress: Semaphore

    // Device Information state.
    private var disManuf: BluetoothGattCharacteristic? = null
    private var disModel: BluetoothGattCharacteristic? = null
    private var disHWRev: BluetoothGattCharacteristic? = null
    private var disSWRev: BluetoothGattCharacteristic? = null
    private var disAvailable: Boolean = false

    // Queues for characteristic read (synchronous)
    private val readQueue: Queue<BluetoothGattCharacteristic>


    val deviceInfo: String
        get() {
            if (tx == null || !disAvailable) {
                return ""
            }
            val sb = StringBuilder()
            sb.append("Manufacturer : " + disManuf!!.getStringValue(0) + "\n")
            sb.append("Model        : " + disModel!!.getStringValue(0) + "\n")
            sb.append("Firmware     : " + disSWRev!!.getStringValue(0) + "\n")
            return sb.toString()
        }

    // Interface for a BluetoothLeUart client to be notified of UART actions.
    interface Callback {
        fun onConnected(uart: BLEControl)
        fun onConnectFailed(uart: BLEControl)
        fun onDisconnected(uart: BLEControl)
        fun onReceive(uart: BLEControl, rx: BluetoothGattCharacteristic)
        fun onDeviceFound(device: BluetoothDevice)
        fun onDeviceInfoAvailable()
        fun onRSSIread(uart: BLEControl, rssi:Int)

    }

    init {
        this.callbacks = WeakHashMap()
        this.adapter = BluetoothAdapter.getDefaultAdapter()
        this.gatt = null
        this.tx = null
        this.rx = null
        this.disManuf = null
        this.disModel = null
        this.disHWRev = null
        this.disSWRev = null
        this.disAvailable = false
        this.connectFirst = false
        this.writeInProgress = Semaphore(0)
        this.readQueue = ConcurrentLinkedQueue()
    }


    // Send data to connected UART device.
    fun send(data: ByteArray?) {
        if (tx == null || data == null || data.size == 0) {
            // Do nothing if there is no connection or message to send.
            return
        }
        // Update TX characteristic value.  Note the setValue overload that takes a byte array must be used.
        tx!!.value = data
        //        writeInProgress = true; // Set the write in progress flag
        Log.d("BLE", "writing")
        gatt!!.writeCharacteristic(tx)
        try {
            writeInProgress.acquire()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }


    // Send data to connected UART device.
    fun send(data: String?) {
        if (data != null && !data.isEmpty()) {
            send(data.toByteArray(Charset.forName("UTF-8")))
        }
    }

    // Register the specified callback to receive UART callbacks.
    fun registerCallback(callback: Callback) {
        callbacks[callback] = null
    }

    // Unregister the specified callback.
    fun unregisterCallback(callback: Callback) {
        callbacks.remove(callback)
    }

    // Disconnect to a device if currently connected.
    fun disconnect() {
        if (gatt != null) {
            gatt!!.disconnect()
        }
        gatt = null
        tx = null
        rx = null
    }

    // Stop any in progress UART device scan.
    fun stopScan() {
        Log.d("BLE", "stop scan")
        adapter?.stopLeScan(this)
    }

    // Start scanning for BLE UART devices.  Registered callback's onDeviceFound method will be called
    // when devices are found during scanning.
    fun startScan() {
        Log.d("BLE", "start scan")

        if (adapter != null) {
            adapter.startLeScan(this)
        }

    }

    // Connect to the first available UART device.
    fun connectFirstAvailable() {
        // Disconnect to any connected device.
        disconnect()
        // Stop any in progress device scan.
        stopScan()
        // Start scan and connect to first available device.
        connectFirst = true
        startScan()
    }

    // Handlers for BluetoothGatt and LeScan events.
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        if (newState == BluetoothGatt.STATE_CONNECTED) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Connected to device, start discovering services.
                if (!gatt.discoverServices()) {
                    // Error starting service discovery.
                    Log.e("BLE", "Error starting service discovery")
                    connectFailure()
                }
            } else {
                // Error connecting to device.
                Log.e("BLE", "Error connecting to device")
                connectFailure()
            }
        } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
            // Disconnected, notify callbacks of disconnection.
            rx = null
            tx = null
            notifyOnDisconnected(this)
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {

        Log.d("BLE", "onServicesDiscovered")

        super.onServicesDiscovered(gatt, status)
        // Notify connection failure if service discovery failed.
        if (status == BluetoothGatt.GATT_FAILURE) {
            Log.e("BLE", "service discovery failed")
            connectFailure()
            return
        }

        // Save reference to each UART characteristic.
        tx = gatt.getService(UART_UUID).getCharacteristic(TX_UUID)
        rx = gatt.getService(UART_UUID).getCharacteristic(RX_UUID)

        // Save reference to each DIS characteristic.
        disManuf = gatt.getService(DIS_UUID).getCharacteristic(DIS_MANUF_UUID)
        disModel = gatt.getService(DIS_UUID).getCharacteristic(DIS_MODEL_UUID)
        disHWRev = gatt.getService(DIS_UUID).getCharacteristic(DIS_HWREV_UUID)
        disSWRev = gatt.getService(DIS_UUID).getCharacteristic(DIS_SWREV_UUID)

        // Add device information characteristics to the read queue
        // These need to be queued because we have to wait for the response to the first
        // read request before a second one can be processed (which makes you wonder why they
        // implemented this with async logic to begin with???)
        readQueue.offer(disManuf)
        readQueue.offer(disModel)
        readQueue.offer(disHWRev)
        readQueue.offer(disSWRev)


        // Setup notifications on RX characteristic changes (i.e. data received).
        // First call setCharacteristicNotification to enable notification.
        if (!gatt.setCharacteristicNotification(rx, true)) {
            // Stop if the characteristic notification setup failed.
            Log.e("BLE", "characteristic notification setup failed")
            connectFailure()
            return
        }
        // Next update the RX characteristic's client descriptor to enable notifications.
        val desc = rx!!.getDescriptor(CLIENT_UUID)
        if (desc == null) {
            // Stop if the RX characteristic has no client descriptor.
            Log.e("BLE", "RX characteristic has no client descriptor")
            connectFailure()
            return
        }
        desc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        if (!gatt.writeDescriptor(desc)) {
            // Stop if the client descriptor could not be written.
            Log.e("BLET", "client descriptor could not be written")
            connectFailure()
            return
        }
        // Notify of connection completion.
        notifyOnConnected(this)
        // Request a dummy read to get the device information queue going
        gatt.readCharacteristic(disManuf)
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        super.onCharacteristicChanged(gatt, characteristic)
        notifyOnReceive(this, characteristic)
    }

    override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
        super.onCharacteristicRead(gatt, characteristic, status)

        if (status == BluetoothGatt.GATT_SUCCESS) {
            // Check if there is anything left in the queue
            val nextRequest = readQueue.poll()
            if (nextRequest != null) {
                // Send a read request for the next item in the queue
                gatt.readCharacteristic(nextRequest)
            } else {
                // We've reached the end of the queue
                disAvailable = true
                notifyOnDeviceInfoAvailable()
            }
        } else {
            Log.w("DIS", "Failed reading characteristic " + characteristic.uuid.toString())
        }
    }

    override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
        super.onCharacteristicWrite(gatt, characteristic, status)

        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d("BLE", "Characteristic write successful")
        }
        //        writeInProgress = false;
        writeInProgress.release()
    }

    override fun onLeScan(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray) {
        Log.d("BLE", "override fun onLeScan")
        // Stop if the device doesn't have the UART service.
        if (!parseUUIDs(scanRecord).contains(UART_UUID)) {
            return
        }
        Log.d("BLE", "Found device: " + device.name)
        if (device.name != DEVICE_NAME) {
            Log.d("BLE", "Target device name  $DEVICE_NAME")
            return
        }
        // Notify registered callbacks of found device.
        notifyOnDeviceFound(device)
        // Connect to first found device if required.
        if (connectFirst) {

            Log.d("BLE", "Connecting to: " + device.name)
            // Stop scanning for devices.
            stopScan()
            // Prevent connections to future found devices.
            connectFirst = false
            // Connect to device.
            Log.d("BLE", "connectFirst")

            gatt = device.connectGatt(context, true, this)
        }
    }

    fun getRSSI(){
        gatt!!.readRemoteRssi()
    }
    override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status:Int) {
        super.onReadRemoteRssi(gatt, rssi, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            mRSSI = rssi
            notifyRSSIread(this, mRSSI)
        } else {

        }
    }


    //*************************************************************************//
    //PRIVATE FUNCTIONS TO SIMPLIFY THE NOTIFICATION OF ALL THE CALLBACKS OF THE DIFFERENT EVENTS
    private fun notifyRSSIread(uart: BLEControl, rssi:Int){
        for (cb in callbacks.keys){
            cb?.onRSSIread(uart,rssi)
        }
    }

    private fun notifyOnConnected(uart: BLEControl) {
        for (cb in callbacks.keys) {
            cb?.onConnected(uart)
        }
    }

    private fun notifyOnConnectFailed(uart: BLEControl) {
        for (cb in callbacks.keys) {
            cb?.onConnectFailed(uart)
        }
    }

    private fun notifyOnDisconnected(uart: BLEControl) {
        for (cb in callbacks.keys) {
            cb?.onDisconnected(uart)
        }
    }

    private fun notifyOnReceive(uart: BLEControl, rx: BluetoothGattCharacteristic) {
        for (cb in callbacks.keys) {
            cb?.onReceive(uart, rx)
        }
    }

    private fun notifyOnDeviceFound(device: BluetoothDevice) {
        for (cb in callbacks.keys) {
            cb?.onDeviceFound(device)
        }
    }

    private fun notifyOnDeviceInfoAvailable() {
        for (cb in callbacks.keys) {
            cb?.onDeviceInfoAvailable()
        }
    }

    // Notify callbacks of connection failure, and reset connection state.
    private fun connectFailure() {
        rx = null
        tx = null
        notifyOnConnectFailed(this)
    }

    // Filtering by custom UUID is broken in Android 4.3 and 4.4, see:
    //   http://stackoverflow.com/questions/18019161/startlescan-with-128-bit-uuids-doesnt-work-on-native-android-ble-implementation?noredirect=1#comment27879874_18019161
    // This is a workaround function from the SO thread to manually parse advertisement data.
    private fun parseUUIDs(advertisedData: ByteArray): List<UUID> {
        val uuids = ArrayList<UUID>()

        var offset = 0
        while (offset < advertisedData.size - 2) {
            var len = advertisedData[offset++].toInt()
            if (len == 0)
                break

            val type = advertisedData[offset++].toInt()
            when (type) {
                0x02 // Partial list of 16-bit UUIDs
                    , 0x03 // Complete list of 16-bit UUIDs
                -> while (len > 1) {
                    var uuid16 = advertisedData[offset++].toInt()
                    uuid16 += (advertisedData[offset++]).toInt() shl 8
                    len -= 2
                    uuids.add(UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", uuid16)))
                }
                0x06// Partial list of 128-bit UUIDs
                    , 0x07// Complete list of 128-bit UUIDs
                ->
                    // Loop through the advertised 128-bit UUID's.
                    while (len >= 16) {
                        try {
                            // Wrap the advertised bits and order them.
                            val buffer = ByteBuffer.wrap(advertisedData, offset++, 16).order(ByteOrder.LITTLE_ENDIAN)
                            val mostSignificantBit = buffer.long
                            val leastSignificantBit = buffer.long
                            uuids.add(UUID(leastSignificantBit,
                                mostSignificantBit))
                        } catch (e: IndexOutOfBoundsException) {
                            // Defensive programming.
                            continue
                        } finally {
                            // Move the offset to read the next uuid.
                            offset += 15
                            len -= 16
                        }
                    }
                else -> offset += len - 1
            }
        }
        return uuids
    }
    companion object {

        // UUIDs for UART service and associated characteristics.
        var UART_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
        var TX_UUID = UUID.fromString  ("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")
        var RX_UUID = UUID.fromString  ("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")

        // UUID for the UART BTLE client characteristic which is necessary for notifications.
        var CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

        // UUIDs for the Device Information service and associated characteristics.
        var DIS_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb")
        var DIS_MANUF_UUID = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb")
        var DIS_MODEL_UUID = UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb")
        var DIS_HWREV_UUID = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb")
        var DIS_SWREV_UUID = UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb")

    }
}
