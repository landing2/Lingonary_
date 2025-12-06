package com.example.lingonary_
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun WordDetailScreen(
    word: String,
    definition: String,
    startTime: Int,
    endTime: Int,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            //1.Word
            Text(
                text = word,
                style = TextStyle(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            //2.Pronounciation
            Text(
                text = "Pronunciation: /${word.lowercase()}/",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
            //3.Defintion
            Text(
                text = definition,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                ),
                modifier = Modifier.padding(top = 24.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))
            //4.Audio waveform player
            Text(
                text = "Context Clip",
                style = TextStyle(fontSize = 14.sp, color = Color.Gray),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            // Pass the Resource ID of our audio file here
            AudioWaveformPlayer(
                audioResId = R.raw.tradicion_navidena,
                startTime = startTime,
                endTime = endTime
            )
        }
    }
}

//--Helper: waveform player--
@Composable
fun AudioWaveformPlayer(
    audioResId: Int,
    startTime: Int,
    endTime: Int
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }

    // Initialize MediaPlayer
    val mediaPlayer = remember {
        try {
            MediaPlayer.create(context, audioResId)
        } catch (e:Exception) {
            null
        }
    }
    DisposableEffect(Unit) {
        onDispose { mediaPlayer?.release() }
    }
    //  Stop playing when we reach the endTime
    LaunchedEffect(isPlaying) {
        if (isPlaying && mediaPlayer != null) {
            val duration = endTime - startTime
            // Wait for the duration of the clip, then pause
            if (duration > 0) {
                delay(duration.toLong())
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                    isPlaying = false
                }
            }
        }
    }
    // Visuals (Random bars to look like a waveform)
    val barHeights = remember { List(40) { Random.nextInt(15, 45).dp } }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(enabled = mediaPlayer != null){
                mediaPlayer?.let { player ->
                    if (isPlaying) {
                        player.pause()
                        isPlaying = false
                    } else {
                        //Seek to start time before playing
                        player.seekTo(startTime)
                        player.start()
                        isPlaying = true
                        player.setOnCompletionListener { isPlaying = false }
                    }
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Play/Stop button
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF8DA399), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isPlaying) {
                //Pause/stop icon
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(Color.White, RoundedCornerShape(2.dp))
                )
            } else {
                // Play icon
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        // Visualizer Bars
        barHeights.forEachIndexed { index, height ->
            // Dynamic Color Logic
            val barColor = if (isPlaying && index % 2 == 0) Color(0xFF8DA399) else Color.LightGray

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(height)
                    .clip(RoundedCornerShape(2.dp))
                    .background(barColor)
            )
        }
    }
}