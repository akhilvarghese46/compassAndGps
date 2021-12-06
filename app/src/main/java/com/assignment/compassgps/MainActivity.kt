package com.assignment.compassgps

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var compassViewData: CompassView
    private lateinit var senesorManager: SensorManager
    var rtnMatrix: FloatArray = FloatArray(16) { 0f }
    var orientationValue: FloatArray = FloatArray(4) { 0f }
    public var rtnValue: Float = 0.0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        compassViewData = findViewById<View>(R.id.compass_view) as CompassView
        senesorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        addRotationListener()

    }

    private fun addRotationListener() {

        if (senesorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null) {
            senesorManager.registerListener(
                object : SensorEventListener {
                    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                    }

                    override fun onSensorChanged(p0: SensorEvent?) {

                        SensorManager.getRotationMatrixFromVector(rtnMatrix, p0!!.values)
                        SensorManager.getOrientation(rtnMatrix, orientationValue)
                        orientationValue[0] =
                            Math.toDegrees(orientationValue[0].toDouble()).toFloat()
                        orientationValue[1] =
                            Math.toDegrees(orientationValue[1].toDouble()).toFloat()
                        orientationValue[2] =
                            Math.toDegrees(orientationValue[2].toDouble()).toFloat()

                        rtnValue = orientationValue[0]
                        compassViewData.updateCompassRotaion(orientationValue[0])
                    }
                }, senesorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }
}