package com.example.pickleballscorer

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

data class SinglesGameState(
    val servingTeam: Int,
    val team1Score: Int,
    val team2Score: Int,
    val isGameOver: Boolean
)

class SinglesActivity : AppCompatActivity() {

    var servingTeam = 1
    var team1Score = 0
    var team2Score = 0
    var isGameOver = false

    private val history = mutableListOf<SinglesGameState>()

    private var t1p1 = ""
    private var t2p1 = ""

    private lateinit var tvTeam1Score: TextView
    private lateinit var tvTeam2Score: TextView
    private lateinit var tvPlayerTop: TextView
    private lateinit var tvPlayerBottom: TextView
    private lateinit var btnTeam1: Button
    private lateinit var btnTeam2: Button
    private lateinit var btnFault: Button
    private lateinit var btnRecall: Button

    private var defaultTextColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_singles) // Fixed to point to activity_singles.xml

        servingTeam = intent.getIntExtra("STARTING_TEAM", 1)
        t1p1 = intent.getStringExtra("T1P1") ?: "P1"
        t2p1 = intent.getStringExtra("T2P1") ?: "P1"

        tvTeam1Score = findViewById(R.id.tvTeam1Score)
        tvTeam2Score = findViewById(R.id.tvTeam2Score)
        defaultTextColor = tvTeam1Score.currentTextColor

        tvPlayerTop = findViewById(R.id.tvPlayerTop)
        tvPlayerBottom = findViewById(R.id.tvPlayerBottom)

        btnTeam1 = findViewById(R.id.btnTeam1)
        btnTeam2 = findViewById(R.id.btnTeam2)
        btnFault = findViewById(R.id.btnFault)
        btnRecall = findViewById(R.id.btnRecall)

        btnTeam1.setOnClickListener {
            if (servingTeam == 1 && !isGameOver) {
                saveState()
                team1Score++
                checkWinCondition()
                updateScoreboardUI()
            }
        }

        btnTeam2.setOnClickListener {
            if (servingTeam == 2 && !isGameOver) {
                saveState()
                team2Score++
                checkWinCondition()
                updateScoreboardUI()
            }
        }

        btnFault.setOnClickListener {
            if (isGameOver) {
                launchWinnerScreen()
            } else {
                saveState()
                handleFault()
            }
        }

        btnRecall.setOnClickListener {
            if (history.isNotEmpty()) {
                val prevState = history.removeAt(history.size - 1)
                servingTeam = prevState.servingTeam
                team1Score = prevState.team1Score
                team2Score = prevState.team2Score
                isGameOver = prevState.isGameOver
                updateScoreboardUI()
            }
        }

        updateScoreboardUI()
    }

    fun saveState() {
        history.add(SinglesGameState(servingTeam, team1Score, team2Score, isGameOver))
    }

    fun handleFault() {
        servingTeam = if (servingTeam == 1) 2 else 1
        updateScoreboardUI()
    }

    fun checkWinCondition() {
        if (team1Score >= 11 && (team1Score - team2Score) >= 2) isGameOver = true
        else if (team2Score >= 11 && (team2Score - team1Score) >= 2) isGameOver = true
    }

    fun launchWinnerScreen() {
        val winnerName = if (team1Score > team2Score) t1p1 else t2p1
        val intent = Intent(this, WinnerActivity::class.java)
        intent.putExtra("WINNER_NAME", winnerName)
        intent.putExtra("T1_NAME", t1p1)
        intent.putExtra("T2_NAME", t2p1)
        intent.putExtra("T1_SCORE", team1Score)
        intent.putExtra("T2_SCORE", team2Score)
        startActivity(intent)
    }

    fun updateScoreboardUI() {
        tvTeam1Score.text = team1Score.toString()
        tvTeam2Score.text = team2Score.toString()

        if (isGameOver) {
            if (team1Score > team2Score) {
                tvTeam1Score.setTextColor(Color.GREEN)
                btnTeam1.text = "WINNER"
            } else {
                tvTeam2Score.setTextColor(Color.GREEN)
                btnTeam2.text = "WINNER"
            }
            btnFault.text = "See Results"
            return
        } else {
            tvTeam1Score.setTextColor(defaultTextColor)
            tvTeam2Score.setTextColor(defaultTextColor)
            btnTeam1.text = "+ T1"
            btnTeam2.text = "+ T2"
            btnFault.text = "Fault (Missed Rally)"
        }

        val topParams = tvPlayerTop.layoutParams as ConstraintLayout.LayoutParams
        val bottomParams = tvPlayerBottom.layoutParams as ConstraintLayout.LayoutParams

        tvPlayerTop.text = "👤 $t1p1"
        tvPlayerBottom.text = "👤 $t2p1"

        if (servingTeam == 1) {
            if (team1Score % 2 == 0) {
                topParams.horizontalBias = 0.25f
                bottomParams.horizontalBias = 0.75f
            } else {
                topParams.horizontalBias = 0.75f
                bottomParams.horizontalBias = 0.25f
            }
        } else {
            if (team2Score % 2 == 0) {
                bottomParams.horizontalBias = 0.75f
                topParams.horizontalBias = 0.25f
            } else {
                bottomParams.horizontalBias = 0.25f
                topParams.horizontalBias = 0.75f
            }
        }

        tvPlayerTop.layoutParams = topParams
        tvPlayerBottom.layoutParams = bottomParams

        resetPlayerStyle(tvPlayerTop, t1p1)
        resetPlayerStyle(tvPlayerBottom, t2p1)

        if (servingTeam == 1) highlightServer(tvPlayerTop, t1p1)
        else highlightServer(tvPlayerBottom, t2p1)
    }

    private fun resetPlayerStyle(tv: TextView, name: String) {
        tv.text = "👤 $name"
        tv.setTextColor(Color.WHITE)
        tv.setTypeface(null, Typeface.NORMAL)
    }

    private fun highlightServer(tv: TextView, name: String) {
        tv.text = "🏓 $name"
        tv.setTextColor(Color.YELLOW)
        tv.setTypeface(null, Typeface.BOLD)
    }
}