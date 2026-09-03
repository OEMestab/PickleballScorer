package com.example.pickleballscorer

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DynamicSignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_signup)

        val playerCount = intent.getIntExtra("PLAYER_COUNT", 2)
        val gameMode = intent.getStringExtra("GAME_MODE") ?: "SINGLES"
        
        val llPlayers = findViewById<LinearLayout>(R.id.llPlayerInputs)
        val btnStart = findViewById<Button>(R.id.btnStartTournament)

        val editTexts = mutableListOf<EditText>()

        for (i in 1..playerCount) {
            if (gameMode == "SINGLES") {
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
            } else {
                // Doubles Mode - 2 players per team
                val tvTeam = TextView(this)
                tvTeam.text = "Team $i"
                tvTeam.textSize = 20f
                tvTeam.setTextColor(Color.BLACK)
                tvTeam.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 24, 0, 8)
                }
                llPlayers.addView(tvTeam)

                val et1 = EditText(this)
                et1.hint = "Player 1"
                et1.textSize = 18f
                
                val et2 = EditText(this)
                et2.hint = "Player 2"
                et2.textSize = 18f

                llPlayers.addView(et1)
                llPlayers.addView(et2)
                
                editTexts.add(et1)
                editTexts.add(et2)
            }
        }

        btnStart.setOnClickListener {
            // Validate no empty names
            if (editTexts.any { it.text.toString().trim().isEmpty() }) {
                Toast.makeText(this, "Please enter all player names.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val names = mutableListOf<String>()
            
            if (gameMode == "SINGLES") {
                names.addAll(editTexts.map { it.text.toString().trim() })
            } else {
                // Group pairs of players into "Player 1 & Player 2" formats for the backend
                for (i in 0 until playerCount) {
                    val p1 = editTexts[i * 2].text.toString().trim()
                    val p2 = editTexts[i * 2 + 1].text.toString().trim()
                    names.add("$p1 & $p2")
                }
            }

            // Start tournament
            TournamentManager.startTournament(names, gameMode)

            val intent = Intent(this, TournamentBracketActivity::class.java)
            startActivity(intent)
        }
    }
}