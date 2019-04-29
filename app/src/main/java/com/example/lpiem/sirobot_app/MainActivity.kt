package com.example.lpiem.sirobot_app

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.scan.BleScanRuleConfig
import java.util.*
import android.bluetooth.BluetoothGatt
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import com.clj.fastble.exception.BleException
import com.clj.fastble.callback.BleGattCallback
import kotlinx.android.synthetic.main.activity_main.*
import com.clj.fastble.callback.BleWriteCallback
import android.R.attr.data




class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var foundDevice: BleDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ensurePermissions()

        main_activity_grenadine.setOnClickListener(this)
        main_activity_nothing.setOnClickListener(this)

        BleManager.getInstance().init(application)

        val scanRuleConfig = BleScanRuleConfig.Builder()
                .setDeviceMac("F0:30:D5:4A:18:A2")
                .setScanTimeOut(10000)
                .build()

        BleManager.getInstance().initScanRule(scanRuleConfig)

        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(20)
                .setConnectOverTime(10000)
                .setOperateTimeout(5000)



        BleManager.getInstance().scan(scanCallback)

    }

    private fun ensurePermissions() {

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            val buttonListener = DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        0)
            }

            val introDialog = AlertDialog.Builder(this)
                    .setTitle("permmisions")
                    .setMessage("accept plz")
                    .setPositiveButton("accept", buttonListener)
                    .create()

            introDialog.show()

        }
    }


    private val scanCallback = object : BleScanCallback() {
        override fun onScanFinished(scanResultList: MutableList<BleDevice>?) {
            Log.d("BLEConnectActivity", "Scan finished !")
            for (device in scanResultList!!) {
                Log.d("ScanFinished", device.getName())

                    connectDevice(device)
            }
        }

        override fun onScanStarted(success: Boolean) {
            Log.d("BLEConnectActivity", "Scan started ? " + success);

        }

        override fun onScanning(bleDevice: BleDevice?) {
            Log.d("BLEConnectActivity", "Currently scanning, found " + bleDevice!!.name)
        }

    }

    fun connectDevice(bleDevice: BleDevice) {
        Log.d("BLEConnectActivity", "Connection")
        BleManager.getInstance().connect(bleDevice, object : BleGattCallback() {
            override fun onStartConnect() {
                Log.d("BLEConnectActivity", "Connection started")
            }

            override fun onConnectFail(bleDevice: BleDevice, exception: BleException) {
                Log.e("BLEConnectActivity", "Connection failed ! " + exception.description)
                Log.d("BLEConnectActivity", "Reconnecting...")
                connectDevice(bleDevice)
            }

            override fun onConnectSuccess(bleDevice: BleDevice, gatt: BluetoothGatt, status: Int) {
                Log.d("BLEConnectActivity", "Connection Succeeded ! Status: $status")
                Log.d("BLEConnectActivity", "Connecter!!")
                foundDevice = bleDevice
                main_activity_body.visibility = View.VISIBLE
            }

            override fun onDisConnected(isActiveDisConnected: Boolean, bleDevice: BleDevice, gatt: BluetoothGatt, status: Int) {
                Log.d("BLEConnectActivity", "Device disconnected... Status: $status")
            }
        })
    }

    private fun sendData(dataToSend: String) {

        BleManager.getInstance().write(
                foundDevice,
                "6E400001-B5A3-F393-E0A9-E50E24DCCA9E",
                "6E400002-B5A3-F393-E0A9-E50E24DCCA9E",
                dataToSend.toByteArray(),
                object : BleWriteCallback() {
                    override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray) {
                        Log.d("BLEConnectActivity", "Write success -> wrote current: $current which is $justWrite on a total of $total")
                    }

                    override fun onWriteFailure(exception: BleException) {
                        Log.e("BLEConnectActivity", "Write failed -> " + exception.description)
                    }
                })
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.main_activity_grenadine -> {
                sendData("1")
            }
            R.id.main_activity_nothing -> {
                sendData("0")
            }
        }
    }

}


