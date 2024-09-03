package com.kaushik.stepcounter

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null

    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadData()
        resetSteps()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        running = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if(stepSensor == null){
            Toast.makeText(this,"No sensor detected on this device", Toast.LENGTH_SHORT).show()
        }else{
            sensorManager?.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (running && event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val stepsSinceReboot = event.values[0]
            val currentSteps = (stepsSinceReboot - previousTotalSteps).toInt()
            tv_stepsTaken.text = currentSteps.toString()
            progress_circular.setProgressWithAnimation(currentSteps.toFloat())
        }
    }


    private fun resetSteps(){

        tv_stepsTaken.setOnClickListener{
            previousTotalSteps += totalSteps
            totalSteps = 0f
            tv_stepsTaken.text = "0"
            progress_circular.setProgressWithAnimation(0f)
            saveData()
            true

        }
    }
    private fun saveData(){
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }
    private fun loadData(){
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1",0f)
        Log.d("MainActivity","$savedNumber")
        previousTotalSteps = savedNumber
    }

}