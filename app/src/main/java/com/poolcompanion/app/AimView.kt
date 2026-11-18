package com.poolcompanion.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import com.poolcompanion.app.engine.CushionEngine

class AimView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var cueX = 100.0
    private var cueY = 100.0
    private var targetX = 200.0
    private var targetY = 200.0

    fun setPoints(
        cx: Double,
        cy: Double,
        tx: Double,
        ty: Double
    ) {
        cueX = cx
        cueY = cy
        targetX = tx
        targetY = ty
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        if (w <= 0f || h <= 0f) return

        val left = 25f
        val top = 25f
        val right = w - 25f
        val bottom = h - 25f

        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(5, 90, 55)
        canvas.drawRoundRect(
            RectF(left, top, right, bottom),
            20f, 20f, paint
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        paint.color = Color.rgb(150, 100, 40)
        canvas.drawRoundRect(
            RectF(left, top, right, bottom),
            20f, 20f, paint
        )

        fun screenX(x: Double): Float =
            left + (x / 1000.0 * (right - left)).toFloat()

        fun screenY(y: Double): Float =
            top + (y / 500.0 * (bottom - top)).toFloat()

        val cx = screenX(cueX)
        val cy = screenY(cueY)
        val tx = screenX(targetX)
        val ty = screenY(targetY)

        // Direct aiming line
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        paint.color = Color.YELLOW
        canvas.drawLine(cx, cy, tx, ty, paint)

        // One-cushion prediction
        val prediction = CushionEngine.predict(
            cueX, cueY, targetX, targetY
        )

        if (prediction != null) {
            val bx = screenX(prediction.bounceX)
            val by = screenY(prediction.bounceY)

            // Cue ball to cushion
            paint.color = Color.CYAN
            paint.strokeWidth = 5f
            canvas.drawLine(cx, cy, bx, by, paint)

            // Cushion to target ball
            canvas.drawLine(bx, by, tx, ty, paint)

            // Bounce point
            paint.style = Paint.Style.FILL
            paint.color = Color.rgb(255, 140, 0)
            canvas.drawCircle(bx, by, 11f, paint)

            // Cushion label
            paint.color = Color.WHITE
            paint.textSize = 18f
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(
                prediction.cushion,
                bx,
                by - 18f,
                paint
            )
        }

        // Cue ball
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        canvas.drawCircle(cx, cy, 16f, paint)

        // Target ball
        paint.color = Color.RED
        canvas.drawCircle(tx, ty, 16f, paint)

        paint.color = Color.WHITE
        paint.textSize = 18f
        paint.textAlign = Paint.Align.CENTER

        canvas.drawText("CUE", cx, cy - 22f, paint)
        canvas.drawText("TARGET", tx, ty - 22f, paint)
    }
}
