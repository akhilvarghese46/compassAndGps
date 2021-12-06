package com.assignment.compassgps

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
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
    private var red:Paint
    private var dkBlack:Paint
    private var ltblack:Paint
    private var direction = 0f
    private var px = 0
    private var py = 0
    private var textHeight = 0


    init {
        black = Paint(Paint.ANTI_ALIAS_FLAG)
        ltblack = Paint(Paint.ANTI_ALIAS_FLAG)
        red = Paint(Paint.ANTI_ALIAS_FLAG)
        dkBlack= Paint(Paint.ANTI_ALIAS_FLAG)

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

        black.style = Paint.Style.STROKE
        black.strokeWidth = 10f
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
        drawCompassCircle(canvas)
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
           black
       )
        canvas?.restore()
    }

    public fun drawCompassCircle(canvas: Canvas) {
        px = canvas.width / 2
        py = canvas.height / 2
        radius = px -80
        val textWidth = 10
        val cadinalX = px - textWidth / 2
        val cadinalY = py - radius - 60

        canvas?.save()

        canvas.drawCircle(px.toFloat(), py.toFloat(), radius.toFloat(), black)
        canvas.drawCircle(px.toFloat(), py.toFloat(), radius/2.toFloat(), ltblack)


        for (i in 0..3) {
            canvas.drawLine(px.toFloat(), (px - radius).toFloat(), py.toFloat(), (py - radius + 50).toFloat(), red!!)
            canvas.save()
            canvas.translate(0f, 0f)
            when (i) {
                0 -> dirString = north.toString()
                1 -> dirString = east.toString()
                2 -> dirString = south.toString()
                3 -> dirString = west.toString()
                else -> {
                }
            }
          /*  if(i==0){
                canvas.drawText(dirString!!, cadinalX.toFloat(), cadinalY.toFloat(), red!!)
            }
            else{
                canvas.drawText(dirString!!, cadinalX.toFloat(), cadinalY.toFloat(), dkBlack!!)
            }

*/
            if(i==0){
                canvas.drawText(dirString!!,(px + radius * Math.sin((-rotationDegreeValue).toDouble() / 180 * Math.PI)).toFloat(),
                    (py - radius * Math.cos((-rotationDegreeValue).toDouble() / 180 * Math.PI)).toFloat(), red!!)
            }
            else{
                canvas.drawText(dirString!!,(px + radius * Math.sin((-rotationDegreeValue).toDouble() / 180 * Math.PI)).toFloat(),
                    (py - radius * Math.cos((-rotationDegreeValue).toDouble() / 180 * Math.PI)).toFloat(), dkBlack!!)

            }
            canvas.restore()
            canvas.rotate(90f, this.px.toFloat(), this.py.toFloat())
        }
        canvas.restore()

    }


    fun updateCompassRotaion(rotationDegreeValue: Float) {
        this.rotationDegreeValue = rotationDegreeValue
        invalidate()
    }
}


