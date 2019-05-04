package com.example.lpiem.sirobot_app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_parameters.*

class ParametersActivity : AppCompatActivity(), View.OnClickListener {

    private var waterParameters: Int? = null
    private var siropParmeters: Int? = null
    private var sirop = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parameters)

        setSupportActionBar(activity_parameters_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        title = "Dosage"

        sirop = intent.getIntExtra("sirop",0)

        if(sirop == 0){
            actitvity_parameters_view_sirop.visibility = View.GONE
            siropParmeters = 0
        } else if(sirop == 9) {
            actitvity_parameters_view_water.visibility = View.GONE
            waterParameters = 0
        }

        actitvity_parameters_button_sirop_little.setOnClickListener(this)
        actitvity_parameters_button_sirop_way.setOnClickListener(this)
        actitvity_parameters_button_sirop_lot.setOnClickListener(this)

        actitvity_parameters_button_water_little.setOnClickListener(this)
        actitvity_parameters_button_water_way.setOnClickListener(this)
        actitvity_parameters_button_water_lot.setOnClickListener(this)

        activity_parameters_button_service.setOnClickListener(this)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.actitvity_parameters_button_sirop_little -> {
                siropParmeters = 1
                setBackgroundColorButtonSirop(actitvity_parameters_button_sirop_little)
            }

            R.id.actitvity_parameters_button_sirop_way -> {
                siropParmeters = 2
                setBackgroundColorButtonSirop(actitvity_parameters_button_sirop_way)

            }

            R.id.actitvity_parameters_button_sirop_lot -> {
                siropParmeters = 3
                setBackgroundColorButtonSirop(actitvity_parameters_button_sirop_lot)

            }

            R.id.actitvity_parameters_button_water_little -> {
                waterParameters = 1
                setBackgroundColorButtonWatter(actitvity_parameters_button_water_little)
            }

            R.id.actitvity_parameters_button_water_way -> {
                waterParameters = 2
                setBackgroundColorButtonWatter(actitvity_parameters_button_water_way)

            }

            R.id.actitvity_parameters_button_water_lot -> {
                waterParameters = 3
                setBackgroundColorButtonWatter(actitvity_parameters_button_water_lot)

            }

            R.id.activity_parameters_button_service -> {
                if(waterParameters != null && siropParmeters != null){
                    val dataToSent = sirop.toString() + siropParmeters.toString() +  waterParameters.toString()
                    BLEManager.getInstance().sendData(dataToSent)
                    Toast.makeText(this,"Votre boisson est entrain d'étre servi", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this,"Un dosage n'a pas été choisis", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun setBackgroundColorButtonSirop(button: Button) {
        val colorButtonNotSelect = ContextCompat.getDrawable(this, R.drawable.button_not_select)
        actitvity_parameters_button_sirop_lot.background = colorButtonNotSelect
        actitvity_parameters_button_sirop_way.background = colorButtonNotSelect
        actitvity_parameters_button_sirop_little.background = colorButtonNotSelect


        val colorButtonSelect =  ContextCompat.getDrawable(this, R.drawable.button_select)
        button.background = colorButtonSelect
    }

    fun setBackgroundColorButtonWatter(button: Button) {
        val colorButtonNotSelect = ContextCompat.getDrawable(this, R.drawable.button_not_select)
        actitvity_parameters_button_water_lot.background = colorButtonNotSelect
        actitvity_parameters_button_water_way.background = colorButtonNotSelect
        actitvity_parameters_button_water_little.background = colorButtonNotSelect


        val colorButtonSelect =  ContextCompat.getDrawable(this, R.drawable.button_select)
        button.background = colorButtonSelect
    }
}
