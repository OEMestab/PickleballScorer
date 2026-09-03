package com.example.pickleballscorer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class BracketView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var rounds: List<List<TournamentManager.Match>> = emptyList()
    private val matchCoords = mutableMapOf<Int, PointF>()

    private val density = context.resources.displayMetrics.density
    
    // Scale sizes dynamically based on screen density
    private val boxWidth = 140f * density
    private val boxHeight = 50f * density
    private val horizontalSpacing = 40f * density
    private val verticalSpacing = 20f * density

    private val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#444444")
        style = Paint.Style.FILL
    }
    private val boxStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2f * density
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 14f * density
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#888888")
        style = Paint.Style.STROKE
        strokeWidth = 2f * density
    }
    private val activeLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFCA28") // Yellow/Amber
        style = Paint.Style.STROKE
        strokeWidth = 3f * density
    }

    fun setBracket(rounds: List<List<TournamentManager.Match>>) {
        this.rounds = rounds
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (rounds.isEmpty()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        // Calculate total canvas size required to scroll around the tree
        val padding = 40f * density
        val desiredWidth = padding * 2 + rounds.size * boxWidth + (rounds.size - 1) * horizontalSpacing
        val desiredHeight = padding * 2 + rounds[0].size * boxHeight + (rounds[0].size - 1) * verticalSpacing

        setMeasuredDimension(desiredWidth.toInt(), desiredHeight.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (rounds.isEmpty()) return

        matchCoords.clear()
        val padding = 40f * density

        // Step 1: Calculate coordinates for Round 1
        var currentY = padding
        for (match in rounds[0]) {
            matchCoords[match.id] = PointF(padding, currentY)
            currentY += boxHeight + verticalSpacing
        }

        // Step 2: Calculate coordinates for subsequent rounds (centered between children)
        for (r in 1 until rounds.size) {
            val round = rounds[r]
            val prevRound = rounds[r - 1]
            val x = padding + r * (boxWidth + horizontalSpacing)

            for (i in round.indices) {
                val match = round[i]
                val child1 = prevRound[i * 2]
                val child2 = prevRound[i * 2 + 1]
                
                // Y is exactly in the middle of its two children
                val y = (matchCoords[child1.id]!!.y + matchCoords[child2.id]!!.y) / 2f
                matchCoords[match.id] = PointF(x, y)
            }
        }

        // Step 3: Draw Connection Lines (Under the boxes)
        for (r in 1 until rounds.size) {
            val round = rounds[r]
            val prevRound = rounds[r - 1]
            for (i in round.indices) {
                val match = round[i]
                val child1 = prevRound[i * 2]
                val child2 = prevRound[i * 2 + 1]

                val p1 = matchCoords[child1.id]!!
                val p2 = matchCoords[child2.id]!!
                val parent = matchCoords[match.id]!!

                drawConnection(canvas, p1.x, p1.y, parent.x, parent.y, child1.winner != null && child1.winner != "BYE")
                drawConnection(canvas, p2.x, p2.y, parent.x, parent.y, child2.winner != null && child2.winner != "BYE")
            }
        }

        // Step 4: Draw Bracket Boxes and Text
        for (round in rounds) {
            for (match in round) {
                val pos = matchCoords[match.id]!!
                val rect = RectF(pos.x, pos.y, pos.x + boxWidth, pos.y + boxHeight)
                
                // Highlight the specific match about to be played
                val isNext = TournamentManager.getNextMatch()?.id == match.id
                if (isNext) {
                    boxStrokePaint.color = Color.parseColor("#FFCA28")
                    boxStrokePaint.strokeWidth = 4f * density
                } else {
                    boxStrokePaint.color = Color.WHITE
                    boxStrokePaint.strokeWidth = 2f * density
                }

                // Draw Rectangles
                val cornerRadius = 8f * density
                canvas.drawRoundRect(rect, cornerRadius, cornerRadius, boxPaint)
                canvas.drawRoundRect(rect, cornerRadius, cornerRadius, boxStrokePaint)

                // Vertically center text
                val textY = pos.y + (boxHeight / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)
                
                val p1Text = match.p1 ?: "TBD"
                val p2Text = match.p2 ?: "TBD"
                
                if (match.winner != null && match.winner != "BYE") {
                    // Winner takes the slot
                    textPaint.color = Color.GREEN
                    canvas.drawText(match.winner!!, pos.x + boxWidth / 2, textY, textPaint)
                    textPaint.color = Color.WHITE // reset
                } else if (match.p1 == "BYE" && match.p2 == "BYE") {
                    // Empty ghost branch
                    textPaint.color = Color.GRAY
                    canvas.drawText("BYE", pos.x + boxWidth / 2, textY, textPaint)
                    textPaint.color = Color.WHITE // reset
                } else {
                    // Display the pending match text (e.g., Alice vs Bob)
                    val display = "$p1Text vs $p2Text"
                    
                    // Shrink text slightly if names are long (especially useful for Doubles)
                    var adjustedSize = 14f * density
                    if (display.length > 25) {
                        adjustedSize = 8.5f * density
                    } else if (display.length > 15) {
                        adjustedSize = 10f * density
                    }
                    textPaint.textSize = adjustedSize
                    
                    canvas.drawText(display, pos.x + boxWidth / 2, textY, textPaint)
                    textPaint.textSize = 14f * density // reset
                }
            }
        }
    }

    private fun drawConnection(canvas: Canvas, cx: Float, cy: Float, px: Float, py: Float, isActive: Boolean) {
        val paint = if (isActive) activeLinePaint else linePaint
        
        val startX = cx + boxWidth
        val startY = cy + boxHeight / 2
        val endX = px
        val endY = py + boxHeight / 2
        
        val midX = startX + (endX - startX) / 2

        // Draw segmented elbow lines
        canvas.drawLine(startX, startY, midX, startY, paint)
        canvas.drawLine(midX, startY, midX, endY, paint)
        canvas.drawLine(midX, endY, endX, endY, paint)
    }
}