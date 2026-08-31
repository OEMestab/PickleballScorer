package com.example.pickleballscorer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class CoinTossActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_toss)

        // 1. Catch the game mode and player names passed from Signup
        val gameMode = intent.getStringExtra("GAME_MODE") ?: "SINGLES"
        val t1p1 = intent.getStringExtra("T1P1") ?: "Player 1"
        val t2p1 = intent.getStringExtra("T2P1") ?: "Player 1"
        val t1p2 = intent.getStringExtra("T1P2") ?: "Player 2"
        val t2p2 = intent.getStringExtra("T2P2") ?: "Player 2"

        var startingTeam = 1

        val tvResult = findViewById<TextView>(R.id.tvTossResult)
        val btnToss = findViewById<Button>(R.id.btnTossCoin)
        val btnContinue = findViewById<Button>(R.id.btnContinueToss)

        btnToss.setOnClickListener {
            // Generate a random number: 1 or 2
            startingTeam = Random.nextInt(1, 3)

            tvResult.text = "Team $startingTeam\nWins Toss!"
            tvResult.textSize = 40f

            btnToss.visibility = View.GONE
            btnContinue.visibility = View.VISIBLE
        }

        // 2. Pass everything forward to the Bracket page when clicked
        btnContinue.setOnClickListener {
            val nextIntent = Intent(this, BracketActivity::class.java)
            nextIntent.putExtra("GAME_MODE", gameMode)
            nextIntent.putExtra("STARTING_TEAM", startingTeam)
            nextIntent.putExtra("T1P1", t1p1)
            nextIntent.putExtra("T2P1", t2p1)
            nextIntent.putExtra("T1P2", t1p2)
            nextIntent.putExtra("T2P2", t2p2)
            startActivity(nextIntent)
        }
    }
}