package com.example.lingonary_

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forward5
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay5
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lingonary_.ui.theme.TextBlack
import kotlinx.coroutines.delay
import kotlin.random.Random

val LingoGreen = Color(0xFF2E5E38)
val LingoGrey = Color(0xFFC0C0C0)
val LingoBlue = Color(0xFF396B92)

fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@Composable
fun PlayerScreen(
    transcriptData: List<WordTimestamp>,
    title: String,
    audioResId: Int,
    savedWords: List<String>,
    onSaveWord: (WordTimestamp) -> Unit,
    onUnsaveWord: (WordTimestamp) -> Unit,
    onGotoLibrary:() -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    // --Rebuild text--
    val (fullText, wordRanges) = remember(transcriptData) {
        val sb = StringBuilder()
        val ranges = mutableListOf<Triple<WordTimestamp, Int, Int>>()
        transcriptData.forEachIndexed { index, item ->
            val start = sb.length
            sb.append(item.word)
            val end = sb.length
            ranges.add(Triple(item, start, end))
            if (index < transcriptData.size - 1) sb.append(" ")
        }
        Pair(sb.toString(), ranges)
    }

    // --states --
    var isPlaying by remember { mutableStateOf(false) }
    var currentMillis by remember { mutableLongStateOf(0L) }
    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }
    val barHeights = remember { List(35) { (15 + Random.nextInt(25)).dp } }
    val scrollState = rememberScrollState()
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var columnHeightPx by remember { mutableIntStateOf(0) }
    var selectedWordItem by remember { mutableStateOf<WordTimestamp?>(null) }
    var showDefinitionDialog by remember { mutableStateOf(false) }
    val mediaPlayer = remember(audioResId) {
        try {
            MediaPlayer.create(context, audioResId)
        } catch (e: Exception) {
            null
        }
    }
    // Duration updates when player updates
    var duration by remember(mediaPlayer) { mutableIntStateOf(mediaPlayer?.duration ?: 0) }

    val isCurrentWordSaved = remember(selectedWordItem, savedWords) {
        if (selectedWordItem == null) false
        else {
            val cleanWord = selectedWordItem!!.word.trim { !it.isLetterOrDigit() }
            savedWords.contains(cleanWord)
        }
    }

    // --lifecycle / cleanup--
    DisposableEffect(mediaPlayer) {
        mediaPlayer?.setOnCompletionListener {
            isPlaying = false;
            currentMillis = 0;
            if(!isDragging) dragProgress=0f
        }
        // Release old player when audioResId changes
        onDispose { mediaPlayer?.release() }
    }

    // --timer loop--
    LaunchedEffect(isPlaying, isDragging) {
        while (isPlaying && !isDragging) {
            mediaPlayer?.let { currentMillis = it.currentPosition.toLong() }
            delay(50)
        }
    }
    val activeProgress = if (isDragging) dragProgress else { if (duration > 0) currentMillis.toFloat()/duration.toFloat() else 0f }
    val displayMillis = if (duration > 0) (activeProgress * duration).toLong() else 0L
    val currentPlayingWord = transcriptData.findLast { it.startTime <= displayMillis }
    val splitIndex = if (currentPlayingWord != null) wordRanges.find { it.first.id == currentPlayingWord.id }?.third ?: 0 else 0

    LaunchedEffect(splitIndex, columnHeightPx, isDragging) {
        if (isPlaying && !isDragging && layoutResult != null && columnHeightPx > 0) {
            val layout = layoutResult!!
            val lineIndex = layout.getLineForOffset(splitIndex)
            val lineBottom = layout.getLineBottom(lineIndex)
            val viewportBottom = scrollState.value + columnHeightPx
            if (lineBottom > (viewportBottom - (columnHeightPx * 0.3))) {
                scrollState.animateScrollTo(layout.getLineTop(lineIndex).toInt())
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp).systemBarsPadding()) {
            // header
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                IconButton(onClick = onBackClick) { Icon(Icons.Filled.ArrowBack, "Back", tint = Color.Black) }
                Spacer(modifier = Modifier.weight(1f))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextBlack)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onGotoLibrary) { Icon(painterResource(id = R.drawable.ic_wordlib), "Word Library", tint = Color.Black) }
            }
            // Transcript
            Column(modifier = Modifier.weight(1f).verticalScroll(scrollState).onSizeChanged { columnHeightPx = it.height }) {
                Text(
                    text = buildAnnotatedString {
                        append(fullText)
                        addStyle(SpanStyle(color = LingoGrey, fontWeight = FontWeight.Light), 0, fullText.length)
                        if (splitIndex > 0) addStyle(SpanStyle(color = LingoGreen, fontWeight = FontWeight.Normal), 0, splitIndex)
                        selectedWordItem?.let { selected ->
                            wordRanges.find { it.first.id == selected.id }?.let { range ->
                                addStyle(SpanStyle(color = LingoBlue, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline), range.second, range.third)
                            }
                        }
                    },
                    fontSize = 32.sp, lineHeight = 48.sp,
                    onTextLayout = { layoutResult = it },
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures { pos ->
                            layoutResult?.let { layout ->
                                val tapOffset = layout.getOffsetForPosition(pos)
                                val clickedWord = wordRanges.find { (_, start, end) -> tapOffset in start..end }
                                if (clickedWord != null) {
                                    selectedWordItem = clickedWord.first
                                    showDefinitionDialog = true
                                    mediaPlayer?.let { if (it.isPlaying) { it.pause(); isPlaying = false } }
                                } else { selectedWordItem = null }
                            }
                        }
                    }
                )
            }
            // Controls
            Column(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                // 1. Interactive wave box
                Box(modifier = Modifier.fillMaxWidth().height(50.dp)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = { _ -> isDragging = true },
                            onDragEnd = {
                                isDragging = false
                                mediaPlayer?.let {
                                    it.seekTo((dragProgress * duration).toInt())
                                    currentMillis = it.currentPosition.toLong()
                                }
                            },
                            onDragCancel = { isDragging = false },
                            onHorizontalDrag = { change, _ ->
                                dragProgress = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            mediaPlayer?.let {
                                it.seekTo(((offset.x / size.width.toFloat()).coerceIn(0f, 1f) * duration).toInt())
                                currentMillis = it.currentPosition.toLong()
                            }
                        }
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                        barHeights.forEachIndexed { index, height ->
                            val isBarPlayed = activeProgress > (index.toFloat() / barHeights.size.toFloat())
                            Box(modifier = Modifier.weight(1f).height(height).clip(RoundedCornerShape(4.dp)).background(if (isBarPlayed) LingoGreen else LingoGrey))
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(formatTime(displayMillis), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(formatTime(duration.toLong()), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { mediaPlayer?.let { it.seekTo(0) }}) { Icon(Icons.Filled.Refresh, "Loop", tint = Color.Black) }
                    IconButton(onClick = { mediaPlayer?.let { it.seekTo((it.currentPosition - 5000).coerceAtLeast(0)) } }) { Icon(Icons.Filled.Replay5, "-5s", tint = Color.Black) }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .border(2.dp, Color.Black, RoundedCornerShape(24.dp))
                            .clickable {
                                mediaPlayer?.let {
                                    if (isPlaying) { it.pause(); isPlaying = false }
                                    else { it.start(); isPlaying = true }
                                }
                            }
                    ) {
                        Icon(
                            painterResource(if (isPlaying) R.drawable.ic_stop else R.drawable.ic_play_),
                            if (isPlaying) "Stop" else "Play",
                            Modifier.size(32.dp),
                            tint = Color.Black
                        )
                    }

                    IconButton(onClick = { mediaPlayer?.let { it.seekTo((it.currentPosition + 5000).coerceAtMost(duration)) } }) { Icon(Icons.Filled.Forward5, "+5s", tint = Color.Black) }
                    IconButton(onClick = {}) { Icon(Icons.Filled.MoreVert,  "More", tint = Color.Gray) }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        if (showDefinitionDialog && selectedWordItem != null) {
            val resumePlayback = {
                showDefinitionDialog = false
                selectedWordItem = null
                mediaPlayer?.let { it.seekTo((it.currentPosition - 5000).coerceAtLeast(0)); it.start(); isPlaying = true }
            }
            AlertDialog(
                onDismissRequest = { resumePlayback() },
                title = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(selectedWordItem!!.word.trim { !it.isLetterOrDigit() }, style = MaterialTheme.typography.headlineSmall, color = LingoBlue, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = {
                                if (isCurrentWordSaved) {
                                    onUnsaveWord(selectedWordItem!!)
                                } else {
                                    onSaveWord(selectedWordItem!!)
                                }
                            }) {
                                if (isCurrentWordSaved) Icon(Icons.Filled.Check, "Saved", tint = LingoGreen)
                                else Icon(Icons.Filled.Add, "Save", tint = LingoBlue)
                            }
                            IconButton(onClick = { resumePlayback() }) { Icon(Icons.Filled.Close, "Close", tint = Color.Gray) }
                        }
                    }
                },
                text = { Column { Text("Definition:", style = MaterialTheme.typography.labelMedium, color = Color.Gray); Spacer(modifier = Modifier.height(4.dp)); Text(selectedWordItem!!.definition, fontSize = 18.sp) } },
                confirmButton = {}, containerColor = Color.White, shape = RoundedCornerShape(16.dp)
            )
        }
    }
}