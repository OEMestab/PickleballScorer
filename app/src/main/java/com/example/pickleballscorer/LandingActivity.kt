package com.example.pickleballscorer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class LandingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val btnSingles = findViewById<Button>(R.id.btnSinglesMode)
        val btnDoubles = findViewById<Button>(R.id.btnDoublesMode)
        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        btnSingles.setOnClickListener {
            val intent = Intent(this, PlayerCountActivity::class.java)
            intent.putExtra("GAME_MODE", "SINGLES")
            startActivity(intent)
        }

        btnDoubles.setOnClickListener {
            val intent = Intent(this, PlayerCountActivity::class.java)
            intent.putExtra("GAME_MODE", "DOUBLES")
            startActivity(intent)
        }
    }
}