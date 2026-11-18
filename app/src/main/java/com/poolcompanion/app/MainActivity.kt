package com.poolcompanion.app

import android.app.Activity
import android.text.InputType
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.inputmethod.InputType
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import kotlin.math.atan2
import kotlin.math.hypot

class MainActivity : Activity() {

    private lateinit var cueX: EditText
    private lateinit var cueY: EditText
    private lateinit var targetX: EditText
    private lateinit var targetY: EditText
    private lateinit var result: TextView

    private val green = Color.rgb(0, 255, 120)
    private val dark = Color.rgb(10, 15, 12)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 30, 24, 30)
            setBackgroundColor(dark)
        }

        val title = TextView(this).apply {
            text = "SMART AIM ENGINE"
            textSize = 25f
            setTextColor(green)
            gravity = Gravity.CENTER
        }

        layout.addView(title)

        val subtitle = TextView(this).apply {
            text = "ENTER BALL COORDINATES"
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        layout.addView(subtitle)

        cueX = createInput("Cue Ball X")
        cueY = createInput("Cue Ball Y")
        targetX = createInput("Target Ball X")
        targetY = createInput("Target Ball Y")

        layout.addView(cueX)
        layout.addView(cueY)
        layout.addView(targetX)
        layout.addView(targetY)

        val calculate = Button(this).apply {
            text = "CALCULATE SHOT"
            textSize = 17f
            setTextColor(Color.BLACK)
            setBackgroundColor(green)

            setOnClickListener {
                calculateShot()
            }
        }

        layout.addView(calculate)

        result = TextView(this).apply {
            text = "SHOT ANGLE: --\nDISTANCE: --"
            textSize = 19f
            setTextColor(green)
            gravity = Gravity.CENTER
            setPadding(10, 35, 10, 35)
        }

        layout.addView(result)

        val reset = Button(this).apply {
            text = "RESET"
            setOnClickListener {
                cueX.text.clear()
                cueY.text.clear()
                targetX.text.clear()
                targetY.text.clear()
                result.text = "SHOT ANGLE: --\nDISTANCE: --"
            }
        }

        layout.addView(reset)

        setContentView(layout)
    }

    private fun createInput(label: String): EditText {
        return EditText(this).apply {
            hint = label
            textSize = 16f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
            inputType = InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_FLAG_DECIMAL or
                InputType.TYPE_NUMBER_FLAG_SIGNED
            setPadding(15, 10, 15, 10)
        }
    }

    private fun calculateShot() {
        val x1 = cueX.text.toString().toDoubleOrNull()
        val y1 = cueY.text.toString().toDoubleOrNull()
        val x2 = targetX.text.toString().toDoubleOrNull()
        val y2 = targetY.text.toString().toDoubleOrNull()

        if (x1 == null || y1 == null ||
            x2 == null || y2 == null) {
            Toast.makeText(
                this,
                "Please enter all coordinates",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val dx = x2 - x1
        val dy = y2 - y1

        if (dx == 0.0 && dy == 0.0) {
            Toast.makeText(
                this,
                "Ball positions must be different",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val angle = Math.toDegrees(atan2(dy, dx))
        val distance = hypot(dx, dy)

        result.text = String.format(
            java.util.Locale.US,
            "SHOT ANGLE: %.2f°\nDISTANCE: %.2f units",
            angle,
            distance
        )
    }
}
