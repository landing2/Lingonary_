package com.example.lingonary_

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lingonary_.ui.theme.*
val SageGreen = Color(0xFFD9D56B)
val LightGrayBg = Color(0xFFF5F5F5)
val TextGray = Color(0xFF808080)
val TextBlack = Color(0xFF000000)

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("lingonary_prefs", Context.MODE_PRIVATE)
    }

    // load saved value (deafult ot 6 if not found)
    var masteryThreshold by remember {
        mutableIntStateOf(prefs.getInt("mastery_threshold", 6))
    }

    //Other preferences
    var includeMastered by remember { mutableStateOf(false) }
    var quizLength by remember { mutableIntStateOf(10) }

    //helper to savev values
    fun saveThreshold(newValue: Int) {
        masteryThreshold = newValue
        prefs.edit().putInt("mastery_threshold", newValue).apply()
    }

    Scaffold(
        containerColor = LightGrayBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, CircleShape)
                        .shadow(2.dp, CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextBlack)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Setting",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {

            //--1.Mastery cards--
            Text(
                text = "Set Mastery Criteria",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Text(
                text = "How many times must you answer correctly?",
                fontSize = 14.sp,
                color = TextGray,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            //Card 1:Casual (3 Repetitions)
            IntensityCard(
                title = "Casual",
                subtitle = "3 repetitions",
                description = "Good for passive recognition.",
                isSelected = masteryThreshold == 3,
                onClick = { saveThreshold(3)}
            )

            Spacer(modifier = Modifier.height(12.dp))

            //Card 2: Standard (6 Repetitions)
            IntensityCard(
                title = "Standard",
                subtitle = "6 repetitions",
                description = "Balance speed and retention.",
                isSelected = masteryThreshold == 6,
                onClick = {saveThreshold(6)}
            )

            Spacer(modifier = Modifier.height(12.dp))

            //Card3: Rigorous (12 Repetitions)
            IntensityCard(
                title = "Rigorous",
                subtitle = "12 repetitions",
                description = "Strict mastery for long-term memory.",
                isSelected = masteryThreshold == 12,
                onClick = { saveThreshold(12) }
            )

            Spacer(modifier = Modifier.height(32.dp))
            //--2.Quiz preferences--
            Text(
                text = "Quiz Preferences",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            //Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Include Mastered Words", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Switch(
                    checked = includeMastered,
                    onCheckedChange = { includeMastered = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SageGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.LightGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chips
            Text("Quiz Length", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextGray)
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuizLengthChip(5, quizLength == 5) { quizLength = 5 }
                QuizLengthChip(10, quizLength == 10) { quizLength = 10 }
                QuizLengthChip(20, quizLength == 20) { quizLength = 20 }
            }
        }
    }
}
@Composable
fun IntensityCard(
    title: String,
    subtitle: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) SageGreen else Color.Transparent
    val backgroundColor = Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(16.dp))
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Subtitle is now the 'repetition' count
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SageGreen
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = TextGray,
                    lineHeight = 18.sp
                )
            }
            // Radio Button Icon
            Icon(
                imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) SageGreen else Color.LightGray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun QuizLengthChip(count: Int, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (isSelected) SageGreen else Color.White,
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                if (isSelected) Color.Transparent else Color.LightGray,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$count",
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.Black else TextGray
        )
    }
}