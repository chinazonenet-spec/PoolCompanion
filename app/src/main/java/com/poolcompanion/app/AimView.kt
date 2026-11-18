
package com.poolcompanion.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import com.poolcompanion.app.engine.PocketEngine
import com.poolcompanion.app.engine.ShotMode
import com.poolcompanion.app.engine.ShotPathEngine

class AimView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var cueX = 100.0
    private var cueY = 100.0
    private var targetX = 200.0
    private var targetY = 200.0
    private var mode = ShotMode.DIRECT
    private var selectedPocket = "TOP RIGHT"

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

    fun setMode(newMode: ShotMode) {
        mode = newMode
        invalidate()
    }

    fun setPocket(name: String) {
        selectedPocket = name
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

        // Draw six pockets
        for (pocket in PocketEngine.pockets) {
            val px = screenX(pocket.x)
            val py = screenY(pocket.y)

            paint.style = Paint.Style.FILL
            paint.color = Color.BLACK
            canvas.drawCircle(px, py, 13f, paint)

            if (pocket.name == selectedPocket) {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 3f
                paint.color = Color.YELLOW
                canvas.drawCircle(px, py, 17f, paint)
            }
        }

        // Draw selected cushion route
        if (mode != ShotMode.DIRECT) {
            val path = ShotPathEngine.calculate(
                cueX, cueY, targetX, targetY, mode
            )

            if (path != null) {
                val route = mutableListOf<Pair<Float, Float>>()
                route.add(cx to cy)

                for (point in path.bouncePoints) {
                    route.add(
                        screenX(point.x) to screenY(point.y)
                    )
                }

                route.add(tx to ty)

                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 3f
                paint.color = Color.CYAN

                for (i in 0 until route.size - 1) {
                    canvas.drawLine(
                        route[i].first,
                        route[i].second,
                        route[i + 1].first,
                        route[i + 1].second,
                        paint
                    )
                }

                paint.style = Paint.Style.FILL
                paint.color = Color.rgb(255, 140, 0)

                for (point in path.bouncePoints) {
                    canvas.drawCircle(
                        screenX(point.x),
                        screenY(point.y),
                        8f,
                        paint
                    )
                }
            }
        }

        // Calculate selected pocket shot
        val pocketShot = PocketEngine.evaluate(
            cueX, cueY,
            targetX, targetY,
            selectedPocket
        )

        if (pocketShot != null) {

            val gx = screenX(pocketShot.ghostX)
            val gy = screenY(pocketShot.ghostY)

            val px = screenX(pocketShot.pocket.x)
            val py = screenY(pocketShot.pocket.y)

            // Cue ball to ghost ball
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f
            paint.color = Color.CYAN
            canvas.drawLine(cx, cy, gx, gy, paint)

            // Target ball to selected pocket
            paint.color = Color.YELLOW
            paint.strokeWidth = 4f
            canvas.drawLine(tx, ty, px, py, paint)

            // Ghost ball marker
            paint.style = Paint.Style.FILL
            paint.color = Color.BLUE
            canvas.drawCircle(gx, gy, 9f, paint)
        } else {
            paint.style = Paint.Style.FILL
            paint.color = Color.WHITE
            paint.textSize = 17f
            paint.textAlign = Paint.Align.CENTER

            canvas.drawText(
                "NO VALID POCKET SHOT",
                (left + right) / 2f,
                (top + bottom) / 2f,
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
        paint.textSize = 17f
        paint.textAlign = Paint.Align.CENTER

        canvas.drawText("CUE", cx, cy - 22f, paint)
        canvas.drawText("TARGET", tx, ty - 22f, paint)
    }
}
