package com.example.lingonary_
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lingonary_.models.Word
import com.example.lingonary_.ui.theme.*

@Composable
fun WordLibraryScreen(
    onHomeClick: () -> Unit,
    savedWords: List<Word>,
    masteryThreshold: Int,
    onDeleteWord: (String) -> Unit,
    onWordOptionsClick: (String) -> Unit,
    onReviewClick: () -> Unit
) {
    val listState = rememberLazyListState()
    // New: Not seen in quiz yet
    val newWordsCount = savedWords.count { !it.hasBeenInQuiz }
    // Mastered: Times correct >= User's Setting
    val masteredCount = savedWords.count { it.timesCorrect >= masteryThreshold }
    // Learning: Seen in quiz, but score is below threshold
    val learningCount = savedWords.count { it.hasBeenInQuiz && it.timesCorrect < masteryThreshold }

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = {
            WordLibBottomNavBar(onHomeClick = onHomeClick)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item { Spacer(modifier = Modifier.height(20.dp)) }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "For You",
                                style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                            )
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(3.dp)
                                    .background(SageGreen, RoundedCornerShape(2.dp))
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.ic_avatar_face),
                            contentDescription = "User",
                            modifier = Modifier.size(45.dp).clip(CircleShape)
                        )
                    }
                }

                item {
                    Text(
                        text = "Current Status",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack),
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Mastered Card
                        StatusCard(MasteredGreen, "Words\nMastered", "$masteredCount", Modifier.weight(1f).fillMaxHeight())
                        Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Learning Card
                            StatusCard(BadgeYellow, "Learning: $learningCount", "", Modifier.weight(1f).fillMaxWidth(), true)
                            // New Card
                            StatusCard(NewRed, "New: $newWordsCount", "", Modifier.weight(1f).fillMaxWidth(), true)
                        }
                    }
                }

                item {
                    Text(
                        text = "Your Saved Words",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                if (savedWords.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No words saved yet.",
                                style = TextStyle(color = Color.Gray, fontSize = 14.sp)
                            )
                        }
                    }
                } else {
                    itemsIndexed(savedWords) { _, wordObj ->
                        WordListItem(
                            wordObj = wordObj,
                            masteryThreshold = masteryThreshold,
                            onDeleteClick = {onDeleteWord(wordObj.learning)},
                            onOptionsClick = {onWordOptionsClick(wordObj.learning)}
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 24.dp, end = 24.dp)
                    .width(85.dp)
                    .height(44.dp)
                    .shadow(4.dp, RoundedCornerShape(15.dp))
                    .background(BadgeYellow, RoundedCornerShape(15.dp))
                    .border(1.dp, BlackStroke, RoundedCornerShape(15.dp))
                    .clickable { onReviewClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Review",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                )
            }
        }
    }
}
@Composable
fun WordListItem(
    wordObj: Word,
    masteryThreshold: Int,
    onDeleteClick: () -> Unit,
    onOptionsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFFEAEAEA), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // The Word
                Text(
                    text = wordObj.learning,
                    style = TextStyle(color = TextBlack, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                )
                Text(
                    text = "Score: ${wordObj.timesCorrect} / $masteryThreshold",
                    style = TextStyle(color = Color.Gray, fontSize = 12.sp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp).clickable { onDeleteClick() }
                )
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp).clickable { onOptionsClick() }
                )
            }
        }
    }
}

@Composable
fun StatusCard(color: Color, title: String, value: String, modifier: Modifier = Modifier, isCompact: Boolean = false) {
    Box(modifier = modifier.shadow(4.dp, RoundedCornerShape(16.dp)).background(color, RoundedCornerShape(16.dp)).border(1.dp, BlackStroke, RoundedCornerShape(16.dp)).padding(16.dp)) {
        Column(verticalArrangement = Arrangement.Center) {
            Text(title, style = TextStyle(fontSize = if (isCompact) 14.sp else 16.sp, color = Color.White, fontWeight = FontWeight.Medium))
            if (value.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(value, style = TextStyle(fontSize = 36.sp, color = Color.White, fontWeight = FontWeight.Normal))
            }
        }
    }
}
@Composable
fun WordLibBottomNavBar(onHomeClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(20.dp).height(80.dp)) {
        Surface(modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(35.dp), color = White, shadowElevation = 10.dp, border = androidx.compose.foundation.BorderStroke(1.dp, NavStroke)) {
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                NavIcon(R.drawable.ic_home_filled, "Home", false, onHomeClick)
                NavIcon(R.drawable.ic_wordlib, "Word Library", true, {})
                NavIcon(R.drawable.ic_setting_unfilled, "Setting", false, {})
            }
        }
    }
}