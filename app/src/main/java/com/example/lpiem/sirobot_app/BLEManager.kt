package com.example.lpiem.sirobot_app

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.bluetooth.BluetoothGatt
import android.util.Log
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.scan.BleScanRuleConfig

class BLEManager {

    private var foundDevice: BleDevice? = null
    var bleConnect = MutableLiveData<Boolean>()

    fun initBLE(app: Application) {
        if(foundDevice == null) {
            BleManager.getInstance().init(app)

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
                bleConnect.postValue(true)
            }

            override fun onDisConnected(isActiveDisConnected: Boolean, bleDevice: BleDevice, gatt: BluetoothGatt, status: Int) {
                Log.d("BLEConnectActivity", "Device disconnected... Status: $status")
                foundDevice = null
            }
        })
    }

    fun sendData(dataToSend: String) {

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

    fun getBleconnect(): LiveData<Boolean> = bleConnect

    companion object {
        private var BLEManager = BLEManager()

        fun getInstance(): BLEManager = BLEManager
    }
}