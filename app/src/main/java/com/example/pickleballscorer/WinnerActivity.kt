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

        if (TournamentManager.isTournamentActive) {
            val grandChampion = intent.getBooleanExtra("GRAND_CHAMPION", false)
            if (grandChampion) {
                tvWinner.text = "🏆 GRAND CHAMPION 🏆\n$winnerName"
                btnPlayAgain.text = "End Tournament"
                btnPlayAgain.setOnClickListener {
                    TournamentManager.endTournament()
                    val intent = Intent(this, LandingActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            } else {
                tvWinner.text = "Match Winner:\n$winnerName"
                btnPlayAgain.text = "Next Match"
                btnPlayAgain.setOnClickListener {
                    TournamentManager.reportMatchWinner(winnerName)
                    val nextMatch = TournamentManager.getNextMatch()
                    if (nextMatch == null) {
                        val champ = TournamentManager.getTournamentWinner() ?: winnerName
                        val intent = Intent(this, WinnerActivity::class.java)
                        intent.putExtra("WINNER_NAME", champ)
                        intent.putExtra("GRAND_CHAMPION", true)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this, TournamentBracketActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        } else {
            tvWinner.text = winnerName
            btnPlayAgain.text = "Play Again"
            btnPlayAgain.setOnClickListener {
                // Return to the Landing Page and clear the back history
                val intent = Intent(this, LandingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }
}