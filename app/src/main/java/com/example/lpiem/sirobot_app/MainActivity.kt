package com.example.lpiem.sirobot_app

import android.Manifest
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ensurePermissions()

        main_activity_grenadine.setOnClickListener(this)
        main_activity_nothing.setOnClickListener(this)

        BLEManager.getInstance().initBLE(application)

        val updateBleConnected = Observer<Boolean> {
            main_activity_body.visibility = View.VISIBLE
        }

        BLEManager.getInstance().getBleconnect().observe(this, updateBleConnected)

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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.main_activity_grenadine -> {
                BLEManager.getInstance().sendData("1")
            }
            R.id.main_activity_nothing -> {
                BLEManager.getInstance().sendData("0")
            }
        }
    }

}


