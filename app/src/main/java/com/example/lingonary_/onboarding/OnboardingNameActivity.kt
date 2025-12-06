package com.example.lingonary_.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import com.example.lingonary_.R

class OnboardingNameActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_name)

        val nameEditText = findViewById<EditText>(R.id.etName)
        val continueButton = findViewById<Button>(R.id.btnContinue1)

        continueButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()

            if (name.isEmpty()) {
                nameEditText.error = getString(R.string.error_name_empty)
            } else {
                val intent = Intent(this, NativeLanguageActivity::class.java)
                intent.putExtra(OnboardingKeys.EXTRA_USER_NAME, name)
                startActivity(intent)
            }
        }
    }
}
