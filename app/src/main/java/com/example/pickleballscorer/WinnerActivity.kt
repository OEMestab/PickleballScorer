package com.example.pickleballscorer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WinnerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_winner)

        val winnerName = intent.getStringExtra("WINNER_NAME") ?: "Unknown"
        val tvWinner = findViewById<TextView>(R.id.tvWinnerName)
        val btnPlayAgain = findViewById<Button>(R.id.btnPlayAgain)

        tvWinner.text = winnerName

        btnPlayAgain.setOnClickListener {
            // Return to the Landing Page and clear the back history
            val intent = Intent(this, LandingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}