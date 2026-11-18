
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
import com.poolcompanion.app.engine.ShotMode
import com.poolcompanion.app.engine.ShotPathEngine
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var cueX: EditText
    private lateinit var cueY: EditText
    private lateinit var targetX: EditText
    private lateinit var targetY: EditText
    private lateinit var result: TextView
    private lateinit var aimView: AimView
    private lateinit var modeButtons: List<Button>

    private var selectedMode = ShotMode.DIRECT

    private val green = Color.rgb(0, 255, 120)
    private val dark = Color.rgb(10, 15, 12)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(20, 16, 20, 16)
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
            text = "ADVANCED AIM ENGINE"
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

        val modeLabel = TextView(this).apply {
            text = "SELECT SHOT MODE"
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 8)
        }
        layout.addView(modeLabel)

        val modeLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val directButton = createModeButton(
            "DIRECT", ShotMode.DIRECT
        )

        val oneButton = createModeButton(
            "1 CUSHION", ShotMode.ONE_CUSHION
        )

        val twoButton = createModeButton(
            "2 CUSHION", ShotMode.TWO_CUSHION
        )

        modeButtons = listOf(
            directButton,
            oneButton,
            twoButton
        )

        for (button in modeButtons) {
            modeLayout.addView(
                button,
                LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    setMargins(3, 3, 3, 3)
                }
            )
        }

        layout.addView(modeLayout)

        aimView = AimView(this)

        layout.addView(
            aimView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                260
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
            text = "ANGLE: --\nPATH DISTANCE: --\nROUTE: --"
            textSize = 17f
            setTextColor(green)
            gravity = Gravity.CENTER
            setPadding(8, 14, 8, 14)
        }
        layout.addView(result)

        val reset = Button(this).apply {
            text = "RESET"

            setOnClickListener {
                cueX.text.clear()
                cueY.text.clear()
                targetX.text.clear()
                targetY.text.clear()

                selectedMode = ShotMode.DIRECT
                aimView.setMode(selectedMode)
                aimView.setPoints(
                    100.0, 100.0, 200.0, 200.0
                )

                updateModeButtons()

                result.text =
                    "ANGLE: --\nPATH DISTANCE: --\nROUTE: --"
            }
        }
        layout.addView(reset)

        aimView.setPoints(100.0, 100.0, 200.0, 200.0)
        aimView.setMode(selectedMode)

        updateModeButtons()

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

            setPadding(15, 5, 15, 5)
        }
    }

    private fun createModeButton(
        label: String,
        mode: ShotMode
    ): Button {
        return Button(this).apply {
            text = label
            textSize = 11f

            setOnClickListener {
                selectedMode = mode
                aimView.setMode(mode)
                updateModeButtons()
                calculateShot(false)
            }
        }
    }

    private fun updateModeButtons() {
        val modes = listOf(
            ShotMode.DIRECT,
            ShotMode.ONE_CUSHION,
            ShotMode.TWO_CUSHION
        )

        for (i in modeButtons.indices) {
            val selected = modes[i] == selectedMode

            modeButtons[i].setBackgroundColor(
                if (selected) green else Color.DKGRAY
            )

            modeButtons[i].setTextColor(
                if (selected) Color.BLACK else Color.WHITE
            )
        }
    }

    private fun calculateShot(showErrors: Boolean = true) {

        val x1 = cueX.text.toString().toDoubleOrNull()
        val y1 = cueY.text.toString().toDoubleOrNull()
        val x2 = targetX.text.toString().toDoubleOrNull()
        val y2 = targetY.text.toString().toDoubleOrNull()

        if (x1 == null || y1 == null ||
            x2 == null || y2 == null) {

            if (showErrors) {
                showMessage("Please enter all coordinates")
            }
            return
        }

        if (x1 !in 0.0..1000.0 ||
            x2 !in 0.0..1000.0 ||
            y1 !in 0.0..500.0 ||
            y2 !in 0.0..500.0) {

            if (showErrors) {
                showMessage(
                    "X: 0-1000, Y: 0-500"
                )
            }
            return
        }

        val shot = ShotCalculator.calculate(
            x1, y1, x2, y2
        )

        if (shot == null) {
            if (showErrors) {
                showMessage(
                    "Ball positions must be different"
                )
            }
            return
        }

        val path = ShotPathEngine.calculate(
            x1, y1, x2, y2, selectedMode
        )

        if (path == null) {
            aimView.setPoints(x1, y1, x2, y2)

            result.text =
                "NO VALID ROUTE\nTry another mode or position"

            return
        }

        aimView.setPoints(x1, y1, x2, y2)

        val firstPoint = path.bouncePoints.firstOrNull()

        val angle = if (firstPoint != null) {
            ShotCalculator.calculate(
                x1, y1, firstPoint.x, firstPoint.y
            )?.angle ?: shot.angle
        } else {
            shot.angle
        }

        val routeText = if (path.bouncePoints.isEmpty()) {
            "DIRECT"
        } else {
            path.routeName
        }

        result.text = String.format(
            Locale.US,
            "SHOT ANGLE: %.2f°\nPATH DISTANCE: %.2f units\nROUTE: %s",
            angle,
            path.distance,
            routeText
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

