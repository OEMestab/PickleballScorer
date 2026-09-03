package com.example.pickleballscorer

import kotlin.math.max

object TournamentManager {
    data class Match(
        val id: Int,
        var p1: String? = null,
        var p2: String? = null,
        var p1Score: Int? = null,
        var p2Score: Int? = null,
        var winner: String? = null,
        var nextMatch: Match? = null
    )

    var isTournamentActive = false
    var gameMode = "SINGLES"
    var tournamentRounds = mutableListOf<List<Match>>()

    fun startTournament(players: List<String>, mode: String = "SINGLES") {
        isTournamentActive = true
        gameMode = mode
        tournamentRounds.clear()

        val shuffled = players.shuffled()
        val numPlayers = shuffled.size
        
        // Find the nearest power of 2 for bracket slots (e.g., 5 players -> 8 slots)
        var powerOf2 = 1
        while (powerOf2 < numPlayers) {
            powerOf2 *= 2
        }

        // Build the Tree structure
        val rounds = mutableListOf<List<Match>>()
        var matchesInRound = powerOf2 / 2
        var currentRoundMatches = mutableListOf<Match>()
        var matchIdCounter = 1

        // Create Round 1
        for (i in 0 until matchesInRound) {
            currentRoundMatches.add(Match(matchIdCounter++))
        }
        rounds.add(currentRoundMatches)

        // Create subsequent rounds and link them backwards
        while (matchesInRound > 1) {
            matchesInRound /= 2
            val nextRound = mutableListOf<Match>()
            for (i in 0 until matchesInRound) {
                nextRound.add(Match(matchIdCounter++))
            }
            
            val prevRound = rounds.last()
            for (i in prevRound.indices) {
                prevRound[i].nextMatch = nextRound[i / 2]
            }
            rounds.add(nextRound)
            currentRoundMatches = nextRound
        }
        tournamentRounds.addAll(rounds)

        // The goal: Distribute BYEs as evenly as possible across the branches
        // so no one side of the bracket is heavily favored or skipped.
        val totalSlots = powerOf2
        val numByes = totalSlots - numPlayers
        
        val slots = Array<String?>(totalSlots) { null }
        
        // 1. We'll distribute BYEs using a standard seeded bracket spacing algorithm.
        // We know we need `numByes`. We place them in specific indices to balance the tree.
        val byeIndices = getSeededIndices(totalSlots).take(numByes).toSet()

        var playerIdx = 0
        for (i in 0 until totalSlots) {
            if (i in byeIndices) {
                slots[i] = "BYE"
            } else {
                slots[i] = shuffled[playerIdx++]
            }
        }

        // 2. Populate Round 1 with the assigned slots
        val r1 = tournamentRounds.first()
        for (i in 0 until totalSlots step 2) {
            val match = r1[i / 2]
            match.p1 = slots[i]
            match.p2 = slots[i + 1]
        }

        // 3. Automatically resolve any matches involving Byes
        processAllByes()
    }

    /**
     * Generates a list of indices representing a balanced spread across a binary tree.
     * For example, for 8 slots, instead of placing BYEs at 0, 1, 2, it spreads them
     * out to branches on opposite sides of the tree: [0, 7, 3, 4, 1, 6, 2, 5].
     */
    private fun getSeededIndices(powerOf2: Int): List<Int> {
        if (powerOf2 <= 1) return listOf(0)
        
        var currentSequence = mutableListOf(0, 1)
        var currentSize = 2

        while (currentSize < powerOf2) {
            val nextSize = currentSize * 2
            val nextSequence = mutableListOf<Int>()
            for (value in currentSequence) {
                nextSequence.add(value)
                nextSequence.add((nextSize - 1) - value)
            }
            currentSequence = nextSequence
            currentSize = nextSize
        }
        return currentSequence
    }

    private fun processAllByes() {
        var changed = true
        while (changed) {
            changed = false
            for (round in tournamentRounds) {
                for (match in round) {
                    if (match.winner == null) {
                        if (match.p1 == "BYE" && match.p2 == "BYE") {
                            match.winner = "BYE"
                            changed = true
                        } else if (match.p1 == "BYE" && match.p2 != null) {
                            match.winner = match.p2
                            changed = true
                        } else if (match.p2 == "BYE" && match.p1 != null) {
                            match.winner = match.p1
                            changed = true
                        }

                        // Advance the winner automatically to the next match
                        if (match.winner != null && match.nextMatch != null) {
                            val next = match.nextMatch!!
                            if (next.p1 == null) next.p1 = match.winner
                            else if (next.p2 == null && next.p1 != match.winner) next.p2 = match.winner
                        }
                    }
                }
            }
        }
    }

    fun getNextMatch(): Match? {
        for (round in tournamentRounds) {
            for (match in round) {
                // Match is ready if it has no winner, and both players are assigned and are not BYEs
                if (match.winner == null && match.p1 != null && match.p2 != null && match.p1 != "BYE" && match.p2 != "BYE") {
                    return match
                }
            }
        }
        return null
    }

    fun reportMatchWinner(winner: String, score1: Int = 0, score2: Int = 0, player1Name: String = "", player2Name: String = "") {
        val match = getNextMatch()
        if (match != null) {
            match.winner = winner
            if (match.p1 == player1Name) {
                match.p1Score = score1
                match.p2Score = score2
            } else if (match.p1 == player2Name) {
                match.p1Score = score2
                match.p2Score = score1
            }

            val next = match.nextMatch
            if (next != null) {
                if (next.p1 == null) next.p1 = winner
                else if (next.p2 == null && next.p1 != winner) next.p2 = winner
            }
            processAllByes() 
        }
    }

    fun getTournamentWinner(): String? {
        val finalMatch = tournamentRounds.lastOrNull()?.firstOrNull()
        return if (finalMatch?.winner != null && finalMatch.winner != "BYE") finalMatch.winner else null
    }

    fun endTournament() {
        isTournamentActive = false
        tournamentRounds.clear()
    }
}