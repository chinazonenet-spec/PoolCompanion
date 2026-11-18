
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
import com.poolcompanion.app.engine.PocketEngine
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
    private lateinit var pocketButtons: List<Button>

    private var selectedMode = ShotMode.DIRECT
    private var selectedPocket = "TOP RIGHT"

    private val green = Color.rgb(0, 255, 120)
    private val dark = Color.rgb(10, 15, 12)

    private val pocketNames = listOf(
        "TOP LEFT",
        "TOP MIDDLE",
        "TOP RIGHT",
        "BOTTOM LEFT",
        "BOTTOM MIDDLE",
        "BOTTOM RIGHT"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(16, 12, 16, 12)
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
            text = "POCKET INTELLIGENCE ENGINE"
            textSize = 15f
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

        val modeLabel = createLabel("SELECT SHOT MODE")
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
                    setMargins(2, 2, 2, 2)
                }
            )
        }

        layout.addView(modeLayout)

        layout.addView(createLabel("SELECT TARGET POCKET"))

        val firstPocketRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val secondPocketRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val buttons = pocketNames.map { name ->
            createPocketButton(name)
        }

        pocketButtons = buttons

        for (i in buttons.indices) {
            val row = if (i < 3) {
                firstPocketRow
            } else {
                secondPocketRow
            }

            row.addView(
                buttons[i],
                LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    setMargins(2, 2, 2, 2)
                }
            )
        }

        layout.addView(firstPocketRow)
        layout.addView(secondPocketRow)

        aimView = AimView(this)

        layout.addView(
            aimView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                230
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
            text = "ANGLE: --\nDISTANCE: --\nDIFFICULTY: --"
            textSize = 16f
            setTextColor(green)
            gravity = Gravity.CENTER
            setPadding(8, 10, 8, 10)
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
                selectedPocket = "TOP RIGHT"

                aimView.setMode(selectedMode)
                aimView.setPocket(selectedPocket)
                aimView.setPoints(
                    100.0, 100.0, 200.0, 200.0
                )

                updateModeButtons()
                updatePocketButtons()

                result.text =
                    "ANGLE: --\nDISTANCE: --\nDIFFICULTY: --"
            }
        }

        layout.addView(reset)

        aimView.setPoints(100.0, 100.0, 200.0, 200.0)
        aimView.setMode(selectedMode)
        aimView.setPocket(selectedPocket)

        updateModeButtons()
        updatePocketButtons()

        setContentView(layout)
    }

    private fun createLabel(value: String): TextView {
        return TextView(this).apply {
            text = value
            textSize = 13f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 5)
        }
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

            setPadding(15, 4, 15, 4)
        }
    }

    private fun createModeButton(
        label: String,
        mode: ShotMode
    ): Button {
        return Button(this).apply {
            text = label
            textSize = 10f

            setOnClickListener {
                selectedMode = mode
                aimView.setMode(mode)
                updateModeButtons()
                calculateShot(false)
            }
        }
    }

    private fun createPocketButton(name: String): Button {
        return Button(this).apply {
            text = name
            textSize = 8f

            setOnClickListener {
                selectedPocket = name
                aimView.setPocket(name)
                updatePocketButtons()
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

    private fun updatePocketButtons() {
        for (i in pocketButtons.indices) {
            val selected = pocketNames[i] == selectedPocket

            pocketButtons[i].setBackgroundColor(
                if (selected) Color.YELLOW else Color.DKGRAY
            )

            pocketButtons[i].setTextColor(
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
                showMessage("X: 0-1000, Y: 0-500")
            }
            return
        }

        val pocketShot = PocketEngine.evaluate(
            x1, y1, x2, y2, selectedPocket
        )

        aimView.setPoints(x1, y1, x2, y2)

        if (pocketShot == null) {
            result.text =
                "NO VALID POCKET SHOT\nTry another pocket or position"
            return
        }

        val aim = ShotCalculator.calculate(
            x1, y1,
            pocketShot.ghostX,
            pocketShot.ghostY
        )

        val modePath = ShotPathEngine.calculate(
            x1, y1, x2, y2, selectedMode
        )

        val routeText = modePath?.routeName
            ?: "NO VALID CUSHION ROUTE"

        val totalDistance =
            pocketShot.cueDistance + pocketShot.objectDistance

        result.text = String.format(
            Locale.US,
            "AIM ANGLE: %.2f°\n" +
                "POCKET: %s\n" +
                "TOTAL DISTANCE: %.2f units\n" +
                "CUT ANGLE: %.2f°\n" +
                "DIFFICULTY: %s\n" +
                "CUSHION MODE: %s",
            aim?.angle ?: 0.0,
            pocketShot.pocket.name,
            totalDistance,
            pocketShot.cutAngle,
            pocketShot.difficulty,
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
