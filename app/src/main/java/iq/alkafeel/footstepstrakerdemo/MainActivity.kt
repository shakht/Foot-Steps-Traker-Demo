package iq.alkafeel.footstepstrakerdemo

import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var stepCount = 0
    private lateinit var stepCountTextView: TextView

    companion object {
        const val ACTIVITY_RECOGNITION_REQUEST_CODE = 101
        const val TAG = "StepCounterDemo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
                    ACTIVITY_RECOGNITION_REQUEST_CODE)
            } else {
                initStepCounter()
            }
        } else {
            initStepCounter()
        }
    }

    private fun initStepCounter() {
        stepCountTextView = findViewById(R.id.stepCountTextView)
        val resetButton: Button = findViewById(R.id.resetButton)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounterSensor == null) {
            stepCountTextView.text = "Step Counter Sensor not available!"
            Log.e(TAG, "Step Counter Sensor not available!")
        } else {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "Step Counter Sensor registered successfully")
        }

        resetButton.setOnClickListener {
            stepCount = 0
            updateStepCount()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACTIVITY_RECOGNITION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                initStepCounter()
            } else {
                Log.e(TAG, "Permission denied to access step counter")
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            Log.d(TAG, "Steps detected: ${event.values[0]}")
            if (stepCount == 0) {
                stepCount = event.values[0].toInt()
            }

            val currentStepCount = event.values[0].toInt() - stepCount
            stepCountTextView.text = "Steps: $currentStepCount"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        Log.d(TAG, "Step Counter Sensor unregistered")
    }

    private fun updateStepCount() {
        stepCountTextView.text = "Steps: 0"
    }
}
