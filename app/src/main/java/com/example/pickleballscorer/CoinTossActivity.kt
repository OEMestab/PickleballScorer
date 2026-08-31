package com.example.pickleballscorer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CoinTossActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_toss)

        val gameMode = intent.getStringExtra("GAME_MODE") ?: "SINGLES"
        val t1p1 = intent.getStringExtra("T1P1") ?: "Player 1"
        val t2p1 = intent.getStringExtra("T2P1") ?: "Player 1"
        val t1p2 = intent.getStringExtra("T1P2") ?: "Player 2"
        val t2p2 = intent.getStringExtra("T2P2") ?: "Player 2"

        // Format names correctly based on Singles vs Doubles
        val team1Name = if (gameMode == "SINGLES") t1p1 else "$t1p1\n&\n$t1p2"
        val team2Name = if (gameMode == "SINGLES") t2p1 else "$t2p1\n&\n$t2p2"

        val coinView = findViewById<TextView>(R.id.tvCoin)
        val btnToss = findViewById<Button>(R.id.btnToss)
        val tvResult = findViewById<TextView>(R.id.tvTossResult)

        // Set starting face
        coinView.text = team1Name

        btnToss.setOnClickListener {
            btnToss.isEnabled = false
            tvResult.text = "Flipping..."

            // 1. Pick the winner BEFORE the animation starts
            val startingTeam = (1..2).random()

            // 2. Set exact rotations. 360 = lands on front (Team 1). 180 = lands on back (Team 2).
            // We do 8 full spins as a base for a long 3-second animation.
            val baseRotations = 360f * 8
            val targetRotation = if (startingTeam == 1) baseRotations else baseRotations + 180f

            // 3. Animate over 3 seconds (3000ms) with a natural slow-down at the end
            val animator = ValueAnimator.ofFloat(0f, targetRotation)
            animator.duration = 3000
            animator.interpolator = AccelerateDecelerateInterpolator()

            animator.addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                coinView.rotationY = value

                // Check which side of the coin is currently facing the user
                val mod = value % 360
                if (mod in 90f..270f) {
                    // Back of the coin
                    coinView.text = team2Name
                    coinView.scaleX = -1f // Flips view horizontally to un-mirror the 3D text
                } else {
                    // Front of the coin
                    coinView.text = team1Name
                    coinView.scaleX = 1f // Normal orientation
                }
            }

            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    val winnerText = if (startingTeam == 1) team1Name.replace("\n", " ") else team2Name.replace("\n", " ")
                    tvResult.text = "$winnerText Wins the Toss!"

                    val finalIntent = Intent(this@CoinTossActivity, BracketActivity::class.java).apply {
                        putExtra("GAME_MODE", gameMode)
                        putExtra("STARTING_TEAM", startingTeam)
                        putExtra("T1P1", t1p1)
                        putExtra("T2P1", t2p1)
                        putExtra("T1P2", t1p2)
                        putExtra("T2P2", t2p2)
                    }
                    startActivity(finalIntent)
                    finish()
                }
            })

            animator.start()
        }
    }
}