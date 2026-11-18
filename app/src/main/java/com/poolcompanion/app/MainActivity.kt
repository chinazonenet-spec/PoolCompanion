package com.poolcompanion.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 40, 24, 40)
            setBackgroundColor(Color.rgb(10, 15, 12))
        }

        val title = TextView(this).apply {
            text = "POOL COMPANION"
            textSize = 28f
            setTextColor(Color.rgb(0, 255, 120))
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "SMART POOL ASSISTANT"
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        layout.addView(title)
        layout.addView(subtitle)

        val engines = listOf(
            "SMART AIM ENGINE",
            "TARGET BALL ANALYZER",
            "SHOT ANGLE CALCULATOR",
            "CUSHION PREDICTION",
            "SHOT STATISTICS"
        )

        for (engine in engines) {
            val button = Button(this).apply {
                text = engine
                textSize = 14f
                setTextColor(Color.BLACK)
                setBackgroundColor(Color.rgb(0, 255, 120))

                setOnClickListener {
                    Toast.makeText(
                        this@MainActivity,
                        "$engine SELECTED",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(0, 10, 0, 10)
            layout.addView(button, params)
        }

        setContentView(layout)
    }
}    
