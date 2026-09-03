package com.example.pickleballscorer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DynamicSignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_signup)

        val playerCount = intent.getIntExtra("PLAYER_COUNT", 2)
        val llPlayers = findViewById<LinearLayout>(R.id.llPlayerInputs)
        val btnStart = findViewById<Button>(R.id.btnStartTournament)

        val editTexts = mutableListOf<EditText>()

        for (i in 1..playerCount) {
            val et = EditText(this)
            et.hint = "Player $i Name"
            et.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 16)
            }
            et.textSize = 20f
            llPlayers.addView(et)
            editTexts.add(et)
        }

        btnStart.setOnClickListener {
            val names = editTexts.map { it.text.toString().trim() }
            
            // Validate no empty names
            if (names.any { it.isEmpty() }) {
                Toast.makeText(this, "Please enter all player names.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Start tournament
            TournamentManager.startTournament(names)

            val intent = Intent(this, TournamentBracketActivity::class.java)
            startActivity(intent)
        }
    }
}