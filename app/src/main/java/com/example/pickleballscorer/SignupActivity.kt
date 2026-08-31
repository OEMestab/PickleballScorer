package com.example.pickleballscorer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // 1. Retrieve the game mode and the coin toss winner from the previous page
        val gameMode = intent.getStringExtra("GAME_MODE") ?: "SINGLES"
        val startingTeam = intent.getIntExtra("STARTING_TEAM", 1) // Added here

        // 2. Link the XML fields
        val etT1P1 = findViewById<EditText>(R.id.etTeam1Player1)
        val etT1P2 = findViewById<EditText>(R.id.etTeam1Player2)
        val etT2P1 = findViewById<EditText>(R.id.etTeam2Player1)
        val etT2P2 = findViewById<EditText>(R.id.etTeam2Player2)
        val btnGenerate = findViewById<Button>(R.id.btnGenerateBracket)

        // 3. Hide Player 2 fields if playing Singles
        if (gameMode == "SINGLES") {
            etT1P2.visibility = View.GONE
            etT2P2.visibility = View.GONE
        }

        // 4. Pass the names and the coin toss winner to Page 3 when clicked
        btnGenerate.setOnClickListener {
            // Change target from BracketActivity to CoinTossActivity
            val nextIntent = Intent(this, CoinTossActivity::class.java)
            nextIntent.putExtra("GAME_MODE", gameMode)
            nextIntent.putExtra("T1P1", etT1P1.text.toString())
            nextIntent.putExtra("T2P1", etT2P1.text.toString())

            if (gameMode == "DOUBLES") {
                nextIntent.putExtra("T1P2", etT1P2.text.toString())
                nextIntent.putExtra("T2P2", etT2P2.text.toString())
            }

            startActivity(nextIntent)
        }
    }
}