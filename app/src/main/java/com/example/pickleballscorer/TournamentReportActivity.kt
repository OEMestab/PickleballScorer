package com.example.pickleballscorer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TournamentReportActivity : AppCompatActivity() {

    private val CREATE_FILE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tournament_report)

        val tvReportWinner = findViewById<TextView>(R.id.tvReportWinner)
        val bracketView = findViewById<BracketView>(R.id.reportBracketView)
        val llMatchHistory = findViewById<LinearLayout>(R.id.llMatchHistory)
        val tvScoreKeeper = findViewById<TextView>(R.id.tvScoreKeeper)
        val btnExportPdf = findViewById<Button>(R.id.btnExportPdf)
        val btnEndTournament = findViewById<Button>(R.id.btnEndTournament)

        // Winner
        val champ = TournamentManager.getTournamentWinner() ?: "Unknown"
        tvReportWinner.text = "Grand Champion: $champ"

        // Bracket
        bracketView.setBracket(TournamentManager.tournamentRounds)

        // Settings (Score Keeper Name)
        val prefs = getSharedPreferences("PickleballSettings", Context.MODE_PRIVATE)
        val scoreKeeper = prefs.getString("SCORE_KEEPER_NAME", "Not Set")
        tvScoreKeeper.text = "Score Keeper: $scoreKeeper"

        // Match History
        for (round in TournamentManager.tournamentRounds) {
            for (match in round) {
                // Only show real matches
                if (match.p1 != "BYE" && match.p2 != "BYE" && match.winner != null) {
                    val tvMatch = TextView(this)
                    tvMatch.textSize = 16f
                    tvMatch.setTextColor(Color.BLACK)
                    tvMatch.setPadding(0, 8, 0, 8)
                    
                    val p1Name = match.p1 ?: "Unknown"
                    val p2Name = match.p2 ?: "Unknown"
                    val p1Score = match.p1Score ?: 0
                    val p2Score = match.p2Score ?: 0
                    val winner = match.winner
                    
                    tvMatch.text = "Match ${match.id}: $p1Name ($p1Score) vs $p2Name ($p2Score) -> Winner: $winner"
                    llMatchHistory.addView(tvMatch)
                }
            }
        }

        btnExportPdf.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
                putExtra(Intent.EXTRA_TITLE, "Tournament_Report.pdf")
            }
            startActivityForResult(intent, CREATE_FILE)
        }

        btnEndTournament.setOnClickListener {
            TournamentManager.endTournament()
            val intent = Intent(this, LandingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                generatePdf(uri)
            }
        }
    }

    private fun generatePdf(uri: android.net.Uri) {
        val pdfContent = findViewById<LinearLayout>(R.id.llPdfContent)

        // 1. Measure the view to get its full height/width, not just what's visible on screen
        pdfContent.measure(
            View.MeasureSpec.makeMeasureSpec(pdfContent.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val viewWidth = pdfContent.measuredWidth
        val viewHeight = pdfContent.measuredHeight

        pdfContent.layout(0, 0, viewWidth, viewHeight)

        // 2. Create PDF Document
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(viewWidth, viewHeight, 1).create()
        val page = document.startPage(pageInfo)

        // 3. Draw the View to the PDF Canvas
        val canvas: Canvas = page.canvas
        canvas.drawColor(Color.WHITE)
        pdfContent.draw(canvas)

        document.finishPage(page)

        // 4. Save file
        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                document.writeTo(outputStream)
            }
            Toast.makeText(this, "PDF saved successfully", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_LONG).show()
        } finally {
            document.close()
        }
    }
}