package com.assignment.compassgps

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
import org.json.JSONArray

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
    private lateinit var saveWayPoints: Button
    private lateinit var sanckbarLayout: LinearLayout
    private lateinit var locationManager: LocationManager
    private lateinit var jsonData: String
    var gpsEnabled = false
    var networkEnabled = false
    private lateinit var sharePreferences: SharedPreferences

    val wayPointArray = ArrayList<String>()
    val savedwayPointAry = ArrayList<String>()

    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double= 0.0

    var splocData = arrayListOf<locationData>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        compassViewData = findViewById<View>(R.id.compass_view) as CompassView
        getSharedValue()

       /* var tes = stringToWords(testvalue.toString())
        var d = tes[1].split(",").toList()
        var c = testvalue?.toList()*/



        senesorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        addRotationListener()

        latitude = findViewById<TextView>(R.id.latitude)
        longitude = findViewById<TextView>(R.id.longitude)
        startTracking = findViewById<Button>(R.id.btn_start)
        stopTracking = findViewById<Button>(R.id.btn_stop)
        deleteTracking = findViewById<Button>(R.id.btn_delete)
        saveWayPoints = findViewById<Button>(R.id.btn_savewaypoit)
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



                //var editor: SharedPreferences.Editor = sharePreferences.edit()
               // editor.putStringSet("locData", locDataTest.toArray())


               // editor.apply()
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

        saveWayPoints.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                var wayponitValue = currentLatitude.toString() + '|' + currentLongitude.toString()
                savedwayPointAry.add(wayponitValue)
                updateSharedValue()
                compassViewData.updateLocationPointer(currentLatitude,currentLongitude)

            }
        })
    }



    fun stringToWords(s : String): List<String> = s.trim().splitToSequence("locationData")
        .filter { it.isNotEmpty() } // or: .filter { it.isNotBlank() }
        .toList()

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
        currentLatitude = p0.latitude
        currentLongitude = p0.longitude
        compassViewData.updateCurrentPoint(currentLatitude,currentLongitude)

        var wayponitValue = p0.latitude.toString() + '|' + p0.longitude.toString()
        wayPointArray.add(wayponitValue)
    }

    override fun onSensorChanged(p0: SensorEvent?) {

        SensorManager.getRotationMatrixFromVector(rtnMatrix, p0!!.values)
        SensorManager.getOrientation(rtnMatrix, orientationValue)

        orientationValue[0] = Math.toDegrees(orientationValue[0].toDouble()).toFloat()
        orientationValue[1] = Math.toDegrees(orientationValue[1].toDouble()).toFloat()
        orientationValue[2] = Math.toDegrees(orientationValue[2].toDouble()).toFloat()

        if( orientationValue[0]*-1<0)
        {
            rtnValue = 360+orientationValue[0]*-1
        }
        else{
            rtnValue = orientationValue[0]*-1
        }

        compassViewData.updateCompassRotaion(orientationValue[0])
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    public fun getSharedValue(){
        sharePreferences = getSharedPreferences("locationGps", Activity.MODE_PRIVATE)
        var waypoint = sharePreferences.getString("locData", savedwayPointAry.toString())
        waypoint = waypoint?.replace("[", "")?.replace("]","")
        var waypointary = waypoint?.split(",")?.toList()
        //splocData
        var locNumber = splocData.size
        if (waypointary != null) {
            for (item in waypointary)
            {
                locNumber =locNumber+1
                savedwayPointAry.add(item)
                var newitem = item.split("|")?.toList()

                var obj: locationData = locationData(
                    Id = locNumber,
                    latitude =newitem[0].toDouble() ,
                    longitude=newitem[1].toDouble()
                )
                splocData.add(obj)
            }

        }
        compassViewData.locData  = splocData
    }

    public fun updateSharedValue(){
        var waypoint = sharePreferences.getString("locData", savedwayPointAry.toString())
        var editor: SharedPreferences.Editor = sharePreferences.edit()
        editor.putString("locData", savedwayPointAry.toString())
        editor.apply()
    }
}

