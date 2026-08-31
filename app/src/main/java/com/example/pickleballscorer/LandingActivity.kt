package com.example.pickleballscorer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LandingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val btnSingles = findViewById<Button>(R.id.btnSinglesMode)
        val btnDoubles = findViewById<Button>(R.id.btnDoublesMode)

        btnSingles.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            intent.putExtra("GAME_MODE", "SINGLES")
            startActivity(intent)
        }

        btnDoubles.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            intent.putExtra("GAME_MODE", "DOUBLES")
            startActivity(intent)
        }
    }
}