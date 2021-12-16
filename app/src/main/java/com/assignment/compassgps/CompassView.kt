package com.assignment.compassgps

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.location.Location
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import kotlin.math.min

class CompassView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var rotationDegreeValue = 0f
    private var north: String? = null
    private var south: String? = null
    private var east: String? = null
    private var west: String? = null
    private var dirString: String  = "N"
    private var radius: Int =0
    private var black:Paint
    private var blacktext:Paint
    private var red:Paint
    private var dkBlack:Paint
    private var ltblack:Paint
    private var direction = 0f
    private var px = 0
    private var py = 0

    private var textHeight = 0
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double= 0.0
    private var locNumber: Int= 0
    var locData = arrayListOf<locationData>()



    init {

        blacktext= Paint(Paint.ANTI_ALIAS_FLAG)
        black = Paint(Paint.ANTI_ALIAS_FLAG)
        ltblack = Paint(Paint.ANTI_ALIAS_FLAG)
        red = Paint(Paint.ANTI_ALIAS_FLAG)
        dkBlack= Paint(Paint.ANTI_ALIAS_FLAG)

        blacktext.setColor(Color.argb(255, 0, 0, 0))

        red.setColor(Color.argb(255, 255, 0, 0))
        black.setColor(Color.argb(255, 0, 0, 0))
        ltblack.setColor(Color.argb(255, 0, 0, 0))
        dkBlack.setColor(Color.argb(255, 0, 0, 0))

        red.setTextSize(40F)
        red.style = Paint.Style.STROKE
        red.strokeWidth = 5f

        dkBlack.setTextSize(40F)
        dkBlack.style = Paint.Style.STROKE
        dkBlack.strokeWidth = 5f

        //black.style = Paint.Style.STROKE
        //black.strokeWidth = 10f
        black.color = Color.GRAY

        ltblack.style = Paint.Style.STROKE
        ltblack.strokeWidth = 3f
        ltblack.color = Color.GRAY

        north = this.resources.getString(R.string.compass_north)
        east = this.resources.getString(R.string.compass_east)
        south = this.resources.getString(R.string.compass_south)
        west = this.resources.getString(R.string.compass_west)

 }
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val smaller = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(smaller, smaller)
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        var width: Int = canvas!!.width
        var height: Int = canvas!!.height
        canvas?.save()
        canvas.rotate(rotationDegreeValue, px.toFloat(), py.toFloat())
        canvas?.restore()
        drawCompassCircleTwo(canvas)
        //drawCompassCircle(canvas)
        drawFaceLine(canvas)

    }



    private fun drawFaceLine(canvas: Canvas) {

        canvas?.save()
        /*canvas.drawLine(px.toFloat(), py.toFloat(),
            (px + radius * Math.sin(-direction.toDouble())).toFloat(),
            (py - radius * Math.cos(-direction.toDouble())).toFloat(),
            black
        )*/
    canvas.drawLine(px.toFloat(), py.toFloat(),
           (px + radius * Math.sin(-direction.toDouble())).toFloat(),
           (py - radius * Math.cos(-direction.toDouble())).toFloat(),
        dkBlack
       )
        canvas?.restore()
    }



    public fun drawCompassCircleTwo(canvas: Canvas) {
        px = canvas.width / 2
        py = canvas.height / 2
        radius = px -80
        val textWidth = 10
        val cadinalX = px - textWidth / 2
        val cadinalY = py - radius - 60

        /*val bitmap = Bitmap.createBitmap(
            canvas.width,
            canvas.height,
            Bitmap.Config.ARGB_8888
        )
        val canvasbitmap = Canvas(bitmap)*/
        canvas.save()

        canvas.rotate(-rotationDegreeValue, px.toFloat(), py.toFloat())

        canvas.drawCircle(px.toFloat(), py.toFloat(), radius.toFloat(), black)
        canvas.drawCircle(px.toFloat(), py.toFloat(), radius/2.toFloat(), dkBlack)
        //canvas.drawCircle(px.toFloat(), py.toFloat(), 5.toFloat(), red)



        canvas.drawCircle(( Math.sin((0).toDouble() / 180 * Math.PI)).toFloat(),
            ( Math.cos((0).toDouble() / 180 * Math.PI)).toFloat(), 5.toFloat(), red)

        if(!locData.isNullOrEmpty()) {
            val bitmap = Bitmap.createBitmap(
                canvas.width,
                canvas.height,
                Bitmap.Config.ARGB_8888
            )
            val canvasTwo = Canvas(bitmap)
            for (obj in locData) {
                var distance = distanceFromCurrentLocation(currentLatitude.toDouble(),currentLongitude.toDouble(),obj.latitude.toDouble(),obj.longitude.toDouble())

                var bearing = getBearingValue(currentLatitude, currentLongitude, obj.latitude, obj.longitude)

                canvasTwo.rotate(bearing.toFloat(), px.toFloat(), py.toFloat())
                canvasTwo.save()
                canvas.drawText(
                    obj.Id.toString(),(width / 2)+distance.toFloat(), (width / 2)+distance.toFloat(), blacktext)
                canvasTwo.drawCircle((width / 2)+distance.toFloat(), (width / 2)+distance.toFloat(), 5.toFloat(), red)
                canvasTwo.restore()
                  /*  canvas.drawText(
                    obj.Id.toString(),(width / 2)+distance.toFloat(), width / 2.toFloat(), red!!)  */              /*canvas.drawText(
                    obj.Id.toString(),(px * Math.sin((30).toDouble() / 180 * Math.PI)).toFloat(),
                    (py  * Math.cos((30).toDouble() / 180 * Math.PI)).toFloat(), red!!)*/
            }
            canvas.drawBitmap(bitmap, px - width/2 .toFloat(), px - width/2.toFloat(), null)
        }
        for (i in 0..3) {
            canvas.drawLine(px.toFloat(), (px - radius).toFloat(), py.toFloat(), (py - radius + 50).toFloat(), dkBlack!!)
            canvas.save()
            canvas.translate(-10f, -10f)
            when (i) {
                0 -> dirString = north.toString()
                1 -> dirString = east.toString()
                2 -> dirString = south.toString()
                3 -> dirString = west.toString()
                else -> {
                }
            }
            /*
            if(i==0){canvas.drawText(dirString!!, cadinalX.toFloat(), cadinalY.toFloat(), red!!)}
              else{canvas.drawText(dirString!!, cadinalX.toFloat(), cadinalY.toFloat(), dkBlack!!)}
             */

            if(i==0){
                canvas.drawText(dirString!!,(px + radius * Math.sin((-direction).toDouble() / 180 * Math.PI)).toFloat(),
                    (py - radius * Math.cos((-direction).toDouble() / 180 * Math.PI)).toFloat(), red!!)
            }
            else{
                canvas.drawText(dirString!!,(px + radius * Math.sin((-direction).toDouble() / 180 * Math.PI)).toFloat(),
                    (py - radius * Math.cos((-direction).toDouble() / 180 * Math.PI)).toFloat(), dkBlack!!)
            }

            canvas.restore()
            canvas.rotate(90f, this.px.toFloat(), this.py.toFloat())
        }
        canvas.restore()

        canvas.save()
        //canvas.drawBitmap(bitmap, px - width/2 .toFloat(), px - width/2.toFloat(), null)
        canvas.restore()

    }


    fun updateCompassRotaion(rotationDegreeValue: Float) {
        this.rotationDegreeValue = rotationDegreeValue
        invalidate()
    }

    fun updateLocationPointer(latitudeValue: Double, longitudeValue:Double) {
        this.currentLatitude = latitudeValue
        this.currentLongitude = longitudeValue
        if(latitudeValue != 0.0 && longitudeValue != 0.0)
        {
            locNumber = locData.size
            locNumber=locNumber+1

            var obj: locationData = locationData(
                Id =locNumber,
                latitude =latitudeValue ,
                longitude=longitudeValue
            )

            locData.add(obj)
        }
        invalidate()
    }

    fun updateCurrentPoint(latitudeValue: Double, longitudeValue:Double) {
        this.currentLatitude = latitudeValue
        this.currentLongitude = longitudeValue

        invalidate()
    }

    fun distanceFromCurrentLocation(curntLat: Double, curntLon: Double, lat2: Double, lon2: Double): Double {

        var locationA = Location("point A")
        locationA.setLatitude(curntLat)
        locationA.setLongitude(curntLon)
        var locationB = Location("point B")
        locationB.setLatitude(lat2)
        locationB.setLongitude(lon2)

        var distance: Double = locationA.distanceTo(locationB).toDouble()

        return distance
    }

    protected fun getBearingValue(startLat: Double, startLng: Double, endLat: Double, endLng: Double): Double {
        val latitude1 = Math.toRadians(startLat)
        val latitude2 = Math.toRadians(endLat)
        val longDiff = Math.toRadians(endLng - startLng)
        val y = Math.sin(longDiff) * Math.cos(latitude2)
        val x =
            Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(
                longDiff
            )
        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360
    }
}


