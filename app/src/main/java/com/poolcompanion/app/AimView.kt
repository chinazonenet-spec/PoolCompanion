package com.poolcompanion.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View

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

        if (w <= 0 || h <= 0) return

        // Pool table background
        paint.color = Color.rgb(5, 90, 55)
        paint.style = Paint.Style.FILL

        canvas.drawRoundRect(
            RectF(8f, 8f, w - 8f, h - 8f),
            24f, 24f, paint
        )

        // Table border
        paint.color = Color.rgb(150, 100, 40)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 12f
        canvas.drawRoundRect(
            RectF(8f, 8f, w - 8f, h - 8f),
            24f, 24f, paint
        )

        val minX = minOf(cueX, targetX)
        val maxX = maxOf(cueX, targetX)
        val minY = minOf(cueY, targetY)
        val maxY = maxOf(cueY, targetY)

        val rangeX = maxOf(1.0, maxX - minX)
        val rangeY = maxOf(1.0, maxY - minY)

        val margin = 55f

        val usableW = (w - margin * 2).coerceAtLeast(1f)
        val usableH = (h - margin * 2).coerceAtLeast(1f)

        val ballX = { x: Double ->
            margin + ((x - minX) / rangeX * usableW).toFloat()
        }

        val ballY = { y: Double ->
            margin + ((y - minY) / rangeY * usableH).toFloat()
        }

        val cx = ballX(cueX)
        val cy = ballY(cueY)
        val tx = ballX(targetX)
        val ty = ballY(targetY)

        // Aiming line
        paint.style = Paint.Style.STROKE
        paint.color = Color.YELLOW
        paint.strokeWidth = 5f

        canvas.drawLine(cx, cy, tx, ty, paint)

        // Cue ball
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        canvas.drawCircle(cx, cy, 19f, paint)

        // Target ball
        paint.color = Color.RED
        canvas.drawCircle(tx, ty, 19f, paint)

        // Ball labels
        paint.color = Color.WHITE
        paint.textSize = 25f
        paint.textAlign = Paint.Align.CENTER

        canvas.drawText("CUE", cx, cy - 28f, paint)
        canvas.drawText("TARGET", tx, ty - 28f, paint)
    }
}
