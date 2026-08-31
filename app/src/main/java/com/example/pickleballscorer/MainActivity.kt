package com.example.pickleballscorer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val btnOpenSingles = findViewById<Button>(R.id.btnOpenSingles)
        val btnOpenDoubles = findViewById<Button>(R.id.btnOpenDoubles)

        btnOpenSingles.setOnClickListener {
            // For now, pass test player names or hook up your pre-game input screen later
            val intent = Intent(this, SinglesActivity::class.java).apply {
                putExtra("STARTING_TEAM", 1)
                putExtra("T1P1", "Player 1")
                putExtra("T2P1", "Player 2")
            }
            startActivity(intent)
        }

        btnOpenDoubles.setOnClickListener {
            // Placeholder for when you build out your Doubles activity next
            // val intent = Intent(this, DoublesActivity::class.java)
            // startActivity(intent)
        }
    }
}