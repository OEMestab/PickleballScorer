package com.example.pickleballscorer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BracketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bracket)

        // 1. Retrieve data from Signup
        val gameMode = intent.getStringExtra("GAME_MODE") ?: "SINGLES"
        val startingTeam = intent.getIntExtra("STARTING_TEAM", 1) // Caught the toss winner
        val t1p1 = intent.getStringExtra("T1P1") ?: "Player 1"
        val t2p1 = intent.getStringExtra("T2P1") ?: "Player 1"
        val t1p2 = intent.getStringExtra("T1P2") ?: "Player 2"
        val t2p2 = intent.getStringExtra("T2P2") ?: "Player 2"

        // 2. Link UI
        val tvTeam1 = findViewById<TextView>(R.id.tvBracketTeam1)
        val tvTeam2 = findViewById<TextView>(R.id.tvBracketTeam2)
        val btnStart = findViewById<Button>(R.id.btnStartMatch)

        // 3. Format names based on mode
        if (gameMode == "SINGLES") {
            tvTeam1.text = t1p1
            tvTeam2.text = t2p1
        } else {
            tvTeam1.text = "$t1p1 & $t1p2"
            tvTeam2.text = "$t2p1 & $t2p2"
        }

        btnStart.setOnClickListener {
            val targetActivity = if (gameMode == "SINGLES") {
                SinglesActivity::class.java
            } else {
                DoublesActivity::class.java
            }

            val finalIntent = Intent(this, targetActivity).apply {
                putExtra("GAME_MODE", gameMode)
                putExtra("STARTING_TEAM", startingTeam)
                putExtra("T1P1", t1p1)
                putExtra("T2P1", t2p1)
                putExtra("T1P2", t1p2)
                putExtra("T2P2", t2p2)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(finalIntent)
        }
    }
}