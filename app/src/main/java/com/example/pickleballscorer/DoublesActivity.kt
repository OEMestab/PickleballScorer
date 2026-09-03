package com.example.pickleballscorer

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

data class DoublesGameState(
    val servingTeam: Int,
    val serverNumber: Int,
    val team1Score: Int,
    val team2Score: Int,
    val t1Left: String,
    val t1Right: String,
    val t2Left: String,
    val t2Right: String,
    val activeServerSide: String,
    val isGameOver: Boolean
)

class DoublesActivity : AppCompatActivity() {

    var servingTeam = 1
    var serverNumber = 2 // Game starts on Server 2
    var team1Score = 0
    var team2Score = 0
    var isGameOver = false

    private val history = mutableListOf<DoublesGameState>()

    // Required for Tournament tracking
    private var originalT1Name = ""
    private var originalT2Name = ""

    // Current players residing in the left/right boxes
    private var t1LeftPlayer = ""
    private var t1RightPlayer = ""
    private var t2LeftPlayer = ""
    private var t2RightPlayer = ""

    // Tracks which box ("LEFT" or "RIGHT") currently holds the server
    private var activeServerSide = "LEFT"

    private lateinit var tvTeam1Score: TextView
    private lateinit var tvTeam2Score: TextView
    private lateinit var tvT1Right: TextView
    private lateinit var tvT1Left: TextView
    private lateinit var tvT2Right: TextView
    private lateinit var tvT2Left: TextView
    private lateinit var btnTeam1: Button
    private lateinit var btnTeam2: Button
    private lateinit var btnFault: Button
    private lateinit var btnRecall: Button

    private var defaultTextColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_doubles)

        servingTeam = intent.getIntExtra("STARTING_TEAM", 1)

        val t1p1 = intent.getStringExtra("T1P1") ?: "P1"
        val t1p2 = intent.getStringExtra("T1P2") ?: "P2"
        val t2p1 = intent.getStringExtra("T2P1") ?: "P3"
        val t2p2 = intent.getStringExtra("T2P2") ?: "P4"

        originalT1Name = "$t1p1 & $t1p2"
        originalT2Name = "$t2p1 & $t2p2"

        // Initial setup
        t1LeftPlayer = t1p1
        t1RightPlayer = t1p2
        t2LeftPlayer = t2p1
        t2RightPlayer = t2p2

        // FIX: First serve ALWAYS starts in the ODD house of the serving house
        // Top Team (1) ODD house = LEFT
        // Bottom Team (2) ODD house = RIGHT
        activeServerSide = if (servingTeam == 1) "LEFT" else "RIGHT"

        tvTeam1Score = findViewById(R.id.tvTeam1Score)
        tvTeam2Score = findViewById(R.id.tvTeam2Score)
        defaultTextColor = tvTeam1Score.currentTextColor

        tvT1Right = findViewById(R.id.tvT1Right)
        tvT1Left = findViewById(R.id.tvT1Left)
        tvT2Right = findViewById(R.id.tvT2Right)
        tvT2Left = findViewById(R.id.tvT2Left)

        btnTeam1 = findViewById(R.id.btnTeam1)
        btnTeam2 = findViewById(R.id.btnTeam2)
        btnFault = findViewById(R.id.btnFault)
        btnRecall = findViewById(R.id.btnRecall)

        btnTeam1.setOnClickListener {
            if (servingTeam == 1 && !isGameOver) {
                saveState()
                team1Score++

                // Swap physical players left and right
                val temp = t1LeftPlayer
                t1LeftPlayer = t1RightPlayer
                t1RightPlayer = temp

                // The server moves with the swap
                activeServerSide = if (activeServerSide == "LEFT") "RIGHT" else "LEFT"

                checkWinCondition()
                updateScoreboardUI()
            }
        }

        btnTeam2.setOnClickListener {
            if (servingTeam == 2 && !isGameOver) {
                saveState()
                team2Score++

                // Swap physical players left and right
                val temp = t2LeftPlayer
                t2LeftPlayer = t2RightPlayer
                t2RightPlayer = temp

                // The server moves with the swap
                activeServerSide = if (activeServerSide == "LEFT") "RIGHT" else "LEFT"

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
                serverNumber = prevState.serverNumber
                team1Score = prevState.team1Score
                team2Score = prevState.team2Score
                t1LeftPlayer = prevState.t1Left
                t1RightPlayer = prevState.t1Right
                t2LeftPlayer = prevState.t2Left
                t2RightPlayer = prevState.t2Right
                activeServerSide = prevState.activeServerSide
                isGameOver = prevState.isGameOver
                updateScoreboardUI()
            }
        }

        updateScoreboardUI()
    }

    fun saveState() {
        history.add(DoublesGameState(servingTeam, serverNumber, team1Score, team2Score, t1LeftPlayer, t1RightPlayer, t2LeftPlayer, t2RightPlayer, activeServerSide, isGameOver))
    }

    fun handleFault() {
        if (serverNumber == 1) {
            serverNumber = 2
            // Partner takes over, so the active side swaps to the other box
            activeServerSide = if (activeServerSide == "LEFT") "RIGHT" else "LEFT"
        } else {
            // Side out! Switch team
            servingTeam = if (servingTeam == 1) 2 else 1
            serverNumber = 1

            // Server 1 ALWAYS starts in the ODD house of their team, regardless of who is currently standing there.
            // Top Team (1) ODD house = LEFT
            // Bottom Team (2) ODD house = RIGHT
            activeServerSide = if (servingTeam == 1) "LEFT" else "RIGHT"
        }
        updateScoreboardUI()
    }

    fun checkWinCondition() {
        if (team1Score >= 11 && (team1Score - team2Score) >= 2) isGameOver = true
        else if (team2Score >= 11 && (team2Score - team1Score) >= 2) isGameOver = true
    }

    fun launchWinnerScreen() {
        val exactWinnerName = if (team1Score > team2Score) originalT1Name else originalT2Name
        val intent = Intent(this, WinnerActivity::class.java)
        intent.putExtra("WINNER_NAME", exactWinnerName)
        intent.putExtra("T1_NAME", originalT1Name)
        intent.putExtra("T2_NAME", originalT2Name)
        intent.putExtra("T1_SCORE", team1Score)
        intent.putExtra("T2_SCORE", team2Score)
        startActivity(intent)
    }

    fun updateScoreboardUI() {
        val servingScore = if (servingTeam == 1) team1Score else team2Score
        val receivingScore = if (servingTeam == 1) team2Score else team1Score

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
            btnFault.text = "Fault (Call: $servingScore - $receivingScore - $serverNumber)"
        }

        // Render players exactly where they currently are
        resetPlayerStyle(tvT1Left, t1LeftPlayer)
        resetPlayerStyle(tvT1Right, t1RightPlayer)
        resetPlayerStyle(tvT2Left, t2LeftPlayer)
        resetPlayerStyle(tvT2Right, t2RightPlayer)

        // Apply highlighting to the active server box based on the state tracker
        if (servingTeam == 1) {
            if (activeServerSide == "LEFT") highlightServer(tvT1Left, t1LeftPlayer)
            else highlightServer(tvT1Right, t1RightPlayer)
        } else {
            if (activeServerSide == "LEFT") highlightServer(tvT2Left, t2LeftPlayer)
            else highlightServer(tvT2Right, t2RightPlayer)
        }
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