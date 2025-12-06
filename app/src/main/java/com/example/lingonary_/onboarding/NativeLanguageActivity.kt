package com.example.lingonary_.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.lingonary_.R

class NativeLanguageActivity : ComponentActivity() {

    private var selectedLanguage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_native_language)

        val userName = intent.getStringExtra(OnboardingKeys.EXTRA_USER_NAME)

        val btnEnglish = findViewById<Button>(R.id.btnEnglishNative)
        val btnBack = findViewById<Button>(R.id.btnBack2)
        val btnContinue = findViewById<Button>(R.id.btnContinue2)

        fun updateSelection(lang: String) {
            selectedLanguage = lang

            btnEnglish.setBackgroundResource(
                if (lang == getString(R.string.lang_english))
                    R.drawable.bg_language_button_selected
                else
                    R.drawable.bg_language_button
            )


        }

        btnEnglish.setOnClickListener { updateSelection(getString(R.string.lang_english)) }

        btnBack.setOnClickListener { finish() }

        btnContinue.setOnClickListener {
            if (selectedLanguage == null) {
                Toast.makeText(this, R.string.error_language_empty, Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, LearningLanguageActivity::class.java)
                intent.putExtra(OnboardingKeys.EXTRA_USER_NAME, userName)
                intent.putExtra(OnboardingKeys.EXTRA_NATIVE_LANGUAGE, selectedLanguage)
                startActivity(intent)
            }
        }
    }
}
