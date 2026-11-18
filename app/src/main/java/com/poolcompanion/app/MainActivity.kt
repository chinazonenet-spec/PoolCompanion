package com.poolcompanion.app

import android.app.Activity
import android.text.InputType
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import com.poolcompanion.app.calculator.ShotCalculator
import com.poolcompanion.app.engine.CushionEngine
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var cueX: EditText
    private lateinit var cueY: EditText
    private lateinit var targetX: EditText
    private lateinit var targetY: EditText
    private lateinit var result: TextView
    private lateinit var aimView: AimView

    private val green = Color.rgb(0, 255, 120)
    private val dark = Color.rgb(10, 15, 12)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 20, 24, 20)
            setBackgroundColor(dark)
        }

        val title = TextView(this).apply {
            text = "POOL COMPANION"
            textSize = 25f
            setTextColor(green)
            gravity = Gravity.CENTER
        }
        layout.addView(title)

        val subtitle = TextView(this).apply {
            text = "SMART AIM + CUSHION ENGINE"
            textSize = 16f
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

        aimView = AimView(this)

        layout.addView(
            aimView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                300
            )
        )

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
            text = "SHOT ANGLE: --\nDISTANCE: --\nBANK SHOT: --"
            textSize = 17f
            setTextColor(green)
            gravity = Gravity.CENTER
            setPadding(10, 20, 10, 20)
        }
        layout.addView(result)

        val reset = Button(this).apply {
            text = "RESET"
            setOnClickListener {
                cueX.text.clear()
                cueY.text.clear()
                targetX.text.clear()
                targetY.text.clear()

                result.text =
                    "SHOT ANGLE: --\nDISTANCE: --\nBANK SHOT: --"

                aimView.setPoints(
                    100.0, 100.0, 200.0, 200.0
                )
            }
        }
        layout.addView(reset)

        aimView.setPoints(100.0, 100.0, 200.0, 200.0)

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

            setPadding(15, 8, 15, 8)
        }
    }

    private fun calculateShot() {
        val x1 = cueX.text.toString().toDoubleOrNull()
        val y1 = cueY.text.toString().toDoubleOrNull()
        val x2 = targetX.text.toString().toDoubleOrNull()
        val y2 = targetY.text.toString().toDoubleOrNull()

        if (x1 == null || y1 == null ||
            x2 == null || y2 == null) {
            showMessage("Please enter all coordinates")
            return
        }

        if (x1 !in 0.0..1000.0 ||
            x2 !in 0.0..1000.0 ||
            y1 !in 0.0..500.0 ||
            y2 !in 0.0..500.0) {
            showMessage(
                "X must be 0-1000 and Y must be 0-500"
            )
            return
        }

        val shot = ShotCalculator.calculate(
            x1, y1, x2, y2
        )

        if (shot == null) {
            showMessage("Ball positions must be different")
            return
        }

        val bank = CushionEngine.predict(
            x1, y1, x2, y2
        )

        aimView.setPoints(x1, y1, x2, y2)

        val bankText = bank?.let {
            "${it.cushion} cushion"
        } ?: "No valid one-cushion route"

        result.text = String.format(
            Locale.US,
            "SHOT ANGLE: %.2f°\nDISTANCE: %.2f units\nBANK SHOT: %s",
            shot.angle,
            shot.distance,
            bankText
        )
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}
