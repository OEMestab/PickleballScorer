package com.example.pickleballscorer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TournamentBracketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tournament_bracket)
        updateBracketUI()
    }

    override fun onResume() {
        super.onResume()
        // Refresh the visual tree in case we navigated back here after a match finishes
        updateBracketUI()
    }

    private fun updateBracketUI() {
        val tvUpcomingPlayers = findViewById<TextView>(R.id.tvUpcomingPlayers)
        val btnStart = findViewById<Button>(R.id.btnStartTournamentMatch)
        val bracketView = findViewById<BracketView>(R.id.bracketView)

        // Inject the data into our custom visual tree
        bracketView.setBracket(TournamentManager.tournamentRounds)

        val match = TournamentManager.getNextMatch()

        if (match == null) {
            // Tournament is entirely complete
            tvUpcomingPlayers.text = "Tournament Complete!"
            btnStart.text = "See Results"
            btnStart.setOnClickListener {
                val champ = TournamentManager.getTournamentWinner() ?: "Unknown"
                val intent = Intent(this, WinnerActivity::class.java)
                intent.putExtra("WINNER_NAME", champ)
                intent.putExtra("GRAND_CHAMPION", true)
                startActivity(intent)
                finish()
            }
        } else {
            // Display the current live match
            tvUpcomingPlayers.text = "Next: ${match.p1} vs ${match.p2}"
            btnStart.setOnClickListener {
                val intent = Intent(this, CoinTossActivity::class.java).apply {
                    putExtra("GAME_MODE", "SINGLES")
                    putExtra("T1P1", match.p1)
                    putExtra("T2P1", match.p2)
                }
                startActivity(intent)
            }
        }
    }
}