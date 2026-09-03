package com.example.pickleballscorer

object TournamentManager {
    data class Match(
        val id: Int,
        var p1: String? = null,
        var p2: String? = null,
        var winner: String? = null,
        var nextMatch: Match? = null
    )

    var isTournamentActive = false
    var tournamentRounds = mutableListOf<List<Match>>()

    fun startTournament(players: List<String>) {
        isTournamentActive = true
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

        // Populate Round 1 with players and BYEs
        val r1 = tournamentRounds.first()
        var playerIdx = 0
        
        for (i in 0 until powerOf2) {
            val match = r1[i / 2]
            val p = if (playerIdx < numPlayers) shuffled[playerIdx++] else "BYE"
            if (i % 2 == 0) match.p1 = p else match.p2 = p
        }

        // Automatically resolve any matches involving Byes
        processAllByes()
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

    fun reportMatchWinner(winner: String) {
        val match = getNextMatch()
        if (match != null) {
            match.winner = winner
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