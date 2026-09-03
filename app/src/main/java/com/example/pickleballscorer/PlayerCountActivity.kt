package com.example.pickleballscorer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PlayerCountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_count)

        val tvPrompt = findViewById<TextView>(R.id.tvPlayerCountPrompt)
        val etCount = findViewById<EditText>(R.id.etPlayerCount)
        val btnNext = findViewById<Button>(R.id.btnNextPlayerCount)

        val gameMode = intent.getStringExtra("GAME_MODE") ?: "SINGLES"
        
        if (gameMode == "DOUBLES") {
            tvPrompt.text = "How many teams?"
            etCount.hint = "Number of teams (e.g. 6)"
        }

        btnNext.setOnClickListener {
            val countStr = etCount.text.toString()
            if (countStr.isNotEmpty()) {
                val count = countStr.toIntOrNull() ?: 0
                if (count < 2) {
                    Toast.makeText(this, "Please enter at least 2 players.", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this, DynamicSignupActivity::class.java)
                    intent.putExtra("PLAYER_COUNT", count)
                    intent.putExtra("GAME_MODE", gameMode)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }
    }
}