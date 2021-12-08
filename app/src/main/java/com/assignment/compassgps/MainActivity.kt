package com.assignment.compassgps

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar

class MainActivity<SomeException> : AppCompatActivity(), LocationListener,SensorEventListener  {

    private lateinit var compassViewData: CompassView
    private lateinit var senesorManager: SensorManager
    var rtnMatrix: FloatArray = FloatArray(16) { 0f }
    var orientationValue: FloatArray = FloatArray(4) { 0f }
    var rtnValue: Float = 0.0f

    private lateinit var latitude: TextView
    private lateinit var longitude: TextView
    private lateinit var startTracking: Button
    private lateinit var stopTracking: Button
    private lateinit var deleteTracking: Button
    private lateinit var sanckbarLayout: LinearLayout
    private lateinit var locationManager: LocationManager
    var gpsEnabled = false
    var networkEnabled = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        compassViewData = findViewById<View>(R.id.compass_view) as CompassView
        senesorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        addRotationListener()

        latitude = findViewById<TextView>(R.id.latitude)
        longitude = findViewById<TextView>(R.id.longitude)
        startTracking = findViewById<Button>(R.id.btn_start)
        stopTracking = findViewById<Button>(R.id.btn_stop)
        deleteTracking = findViewById<Button>(R.id.btn_delete)
        sanckbarLayout = findViewById<LinearLayout>(R.id.linear_layout)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


        startTracking.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                try {
                    gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                }
                catch(exe: Exception ) {

                }
                checkGpsEnable()

                if (gpsEnabled && networkEnabled) {
                    addLocationListener()
                }
            }
        })

        stopTracking.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                locationManager.removeUpdates(this@MainActivity)
                latitude.setText("Latitude: NA" )
                longitude.setText("Longitude: NA ")
            }
        })

        deleteTracking.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

            }
        })
    }

    private fun checkGpsEnable() {
        if (!gpsEnabled && !networkEnabled) {
            // notify user
            AlertDialog.Builder(this)
                .setMessage(R.string.gps_network_not_enabled)
                .setPositiveButton(R.string.open_location_settings,
                    DialogInterface.OnClickListener { paramDialogInterface, paramInt ->
                        this.startActivity(
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        )
                    })
                .setNegativeButton(R.string.Cancel, null)
                .show()

        }
    }

    private fun addRotationListener() {

        if (senesorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null) {
            senesorManager.registerListener(this,senesorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_UI)
                /* senesorManager.registerListener( object : SensorEventListener {
                         override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                         }

                         override fun onSensorChanged(p0: SensorEvent?) {

                             SensorManager.getRotationMatrixFromVector(rtnMatrix, p0!!.values)
                             SensorManager.getOrientation(rtnMatrix, orientationValue)

                             orientationValue[0] = Math.toDegrees(orientationValue[0].toDouble()).toFloat()
                             orientationValue[1] = Math.toDegrees(orientationValue[1].toDouble()).toFloat()
                             orientationValue[2] = Math.toDegrees(orientationValue[2].toDouble()).toFloat()

                             rtnValue = orientationValue[0]
                             compassViewData.updateCompassRotaion(orientationValue[0])
                         }
                     }, senesorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                     SensorManager.SENSOR_DELAY_UI
                 )*/
        }
    }


    private fun addLocationListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(arrayOf( Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f,this)

     /*  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f, object :
            LocationListener {
            override fun onLocationChanged(p0: Location) {

                latitude.setText("Latitude: " + p0.latitude)
                longitude.setText("Longitude: " + p0.longitude)
                compassViewData.updateLocationPointer(p0.latitude,p0.longitude)
            }
        })*/
       /* locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0f
        ) { p0 ->
            latitude.setText("Latitude: " + p0.latitude)
            longitude.setText("Longitude: " + p0.longitude)
            compassViewData.updateLocationPointer(p0.latitude,p0.longitude)
        }*/


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1) {
            if(grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] ==
                PackageManager.PERMISSION_DENIED) {
                var snackbar: Snackbar = Snackbar.make(sanckbarLayout, "App will not work without location permissions", Snackbar.LENGTH_LONG)
                snackbar.show()
            } else {
                var snackbar: Snackbar = Snackbar.make(sanckbarLayout, "location permissions granted", Snackbar.LENGTH_LONG)
                snackbar.show()
            }
        }
    }


    override fun onLocationChanged(p0: Location) {
        latitude.setText("Latitude: " + p0.latitude)
        longitude.setText("Longitude: " + p0.longitude)
        compassViewData.updateLocationPointer(p0.latitude,p0.longitude)
    }

    override fun onSensorChanged(p0: SensorEvent?) {

        SensorManager.getRotationMatrixFromVector(rtnMatrix, p0!!.values)
        SensorManager.getOrientation(rtnMatrix, orientationValue)

        orientationValue[0] = Math.toDegrees(orientationValue[0].toDouble()).toFloat()
        orientationValue[1] = Math.toDegrees(orientationValue[1].toDouble()).toFloat()
        orientationValue[2] = Math.toDegrees(orientationValue[2].toDouble()).toFloat()

        rtnValue = orientationValue[0]
        compassViewData.updateCompassRotaion(orientationValue[0])
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}

