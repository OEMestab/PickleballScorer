package com.example.pickleballscorer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PlayerCountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_count)

        val etCount = findViewById<EditText>(R.id.etPlayerCount)
        val btnNext = findViewById<Button>(R.id.btnNextPlayerCount)

        btnNext.setOnClickListener {
            val countStr = etCount.text.toString()
            if (countStr.isNotEmpty()) {
                val count = countStr.toIntOrNull() ?: 0
                if (count < 2) {
                    Toast.makeText(this, "Please enter at least 2 players.", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this, DynamicSignupActivity::class.java)
                    intent.putExtra("PLAYER_COUNT", count)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }
    }
}