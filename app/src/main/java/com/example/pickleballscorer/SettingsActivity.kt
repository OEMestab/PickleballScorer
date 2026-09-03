package com.example.pickleballscorer

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val etScoreKeeperName = findViewById<EditText>(R.id.etScoreKeeperName)
        val btnSaveSettings = findViewById<Button>(R.id.btnSaveSettings)

        // Load existing
        val prefs = getSharedPreferences("PickleballSettings", Context.MODE_PRIVATE)
        val currentName = prefs.getString("SCORE_KEEPER_NAME", "")
        etScoreKeeperName.setText(currentName)

        btnSaveSettings.setOnClickListener {
            val name = etScoreKeeperName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save
            prefs.edit().putString("SCORE_KEEPER_NAME", name).apply()
            Toast.makeText(this, "Settings Saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}