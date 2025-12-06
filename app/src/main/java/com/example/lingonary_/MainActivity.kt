package com.example.lingonary_

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.lingonary_.database.WordDatabase
import com.example.lingonary_.models.Podcast
import com.example.lingonary_.models.Word
import com.example.lingonary_.ui.theme.Lingonary_Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val testTitle = "Tradición Navideña"
        setContent {
            Lingonary_Theme {
                val context = LocalContext.current
                val lifecycleOwner = LocalLifecycleOwner.current
                val allPodcasts = remember {
                    List(9) { i -> Podcast(title = "Podcast ${i + 1}") }
                }
                var currentScreen by remember { mutableStateOf("home") }
                var selectedWordObj by remember { mutableStateOf<Word?>(null) }
                var selectedPodcast by remember { mutableStateOf<Podcast?>(null) }
                val sharedPrefs = remember { context.getSharedPreferences("lingonary_prefs", Context.MODE_PRIVATE) }
                var recentPodcastTitles by remember { mutableStateOf(sharedPrefs.getStringSet("recent_podcasts", emptySet()) ?: emptySet()) }
                val recentPodcasts = remember(recentPodcastTitles) {
                    allPodcasts.filter { it.title in recentPodcastTitles }
                }
                val featuredPodcasts = remember(recentPodcasts) {
                    allPodcasts.filter { it !in recentPodcasts }.take(3)
                }
                val onClearRecent = {
                    sharedPrefs.edit().putStringSet("recent_podcasts", emptySet()).apply()
                    recentPodcastTitles = emptySet()
                }
                val coroutineScope = rememberCoroutineScope()
                val db = remember { WordDatabase.getInstance(context) }
                val wordDao = remember { db.wordDao() }
                val transcriptData = remember { loadTranscriptFromJson(context, "tradicion_navidena.json") }
                var savedWordsList by remember { mutableStateOf<List<Word>>(emptyList()) }
                var currentMasteryThreshold by remember { mutableIntStateOf(6) }
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            coroutineScope.launch(Dispatchers.IO) {
                                // 1. Refresh Words
                                savedWordsList = wordDao.getAllSavedWords()
                                // 2. Refresh Threshold Setting
                                val prefs = context.getSharedPreferences("lingonary_prefs", Context.MODE_PRIVATE)
                                currentMasteryThreshold = prefs.getInt("mastery_threshold", 6)
                            }
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
                if (selectedPodcast != null) {
                    PodcastDetailDialog(
                        podcast = selectedPodcast!!,
                        onDismiss = { selectedPodcast = null },
                        onPlayClick = {
                            val podcastToPlay = selectedPodcast!!
                            val updatedTitles = recentPodcastTitles + podcastToPlay.title
                            sharedPrefs.edit().putStringSet("recent_podcasts", updatedTitles).apply()
                            recentPodcastTitles = updatedTitles
                            selectedPodcast = null
                            currentScreen = "player"
                        }
                    )
                }

                // --- MAIN NAVIGATION ---
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    when (currentScreen) {
                        "home" -> HomeScreen(
                            featuredPodcasts = featuredPodcasts,
                            recentPodcasts = recentPodcasts,
                            allPodcasts = allPodcasts,
                            onPodcastClick = { podcast -> selectedPodcast = podcast },
                            onWordLibClick = { currentScreen = "wordlib" },
                            onSettingClick = { currentScreen = "setting" },
                            onClearRecent = onClearRecent
                        )

                        "setting" -> SettingsScreen(
                            onBackClick = { currentScreen = "home" }
                        )

                        "player" -> PlayerScreen(
                            transcriptData = transcriptData,
                            title = testTitle,
                            savedWords = savedWordsList.map { it.learning },

                            onSaveWord = { wordItem ->
                                coroutineScope.launch(Dispatchers.IO) {
                                    val cleanWord = wordItem.word.trim { !it.isLetterOrDigit() }

                                    // 2.5s Audio Padding
                                    val paddingMillis = 2500L
                                    val newStartTime = maxOf(0L, wordItem.startTime - paddingMillis).toInt()
                                    val newEndTime = (wordItem.endTime + paddingMillis).toInt()

                                    val newWord = Word(cleanWord, wordItem.definition, newStartTime, newEndTime)
                                    wordDao.insert(newWord)
                                    savedWordsList = wordDao.getAllSavedWords()
                                }
                            },
                            onUnsaveWord = { wordItem ->
                                coroutineScope.launch(Dispatchers.IO) {
                                    val cleanWord = wordItem.word.trim { !it.isLetterOrDigit() }
                                    wordDao.deleteByWord(cleanWord)
                                    savedWordsList = wordDao.getAllSavedWords()
                                }
                            },
                            onBackClick = { currentScreen = "home" }
                        )
                        "wordlib" -> WordLibraryScreen(
                            onHomeClick = { currentScreen = "home" },
                            savedWords = savedWordsList,
                            masteryThreshold = currentMasteryThreshold,

                            onDeleteWord = { wordToDelete ->
                                coroutineScope.launch(Dispatchers.IO) {
                                    wordDao.deleteByWord(wordToDelete)
                                    savedWordsList = wordDao.getAllSavedWords()
                                }
                            },
                            onWordOptionsClick = { wordString ->
                                coroutineScope.launch(Dispatchers.IO) {
                                    val wordObj = wordDao.getWord(wordString)
                                    withContext(Dispatchers.Main) {
                                        if (wordObj != null) {
                                            selectedWordObj = wordObj
                                            currentScreen = "word_detail"
                                        }
                                    }
                                }
                            },
                            onReviewClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    val allWords = wordDao.getAllSavedWords()
                                    withContext(Dispatchers.Main) {
                                        // Minimum 4 words check
                                        if (allWords.size < 4) {
                                            Toast.makeText(context, "Save at least 4 words to start a quiz!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            val wordArrayList = ArrayList(allWords)
                                            val intent = Intent(context, QuizActivity::class.java)
                                            intent.putParcelableArrayListExtra("wordList", wordArrayList)
                                            context.startActivity(intent)
                                        }
                                    }
                                }
                            }
                        )

                        "word_detail" -> {
                            if (selectedWordObj != null) {
                                WordDetailScreen(
                                    word = selectedWordObj!!.learning,
                                    definition = selectedWordObj!!.nativeLang,
                                    startTime = selectedWordObj!!.startTime,
                                    endTime = selectedWordObj!!.endTime,
                                    onBackClick = { currentScreen = "wordlib" }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PodcastDetailDialog(podcast: Podcast, onDismiss: () -> Unit, onPlayClick: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_podcast_card), // Placeholder
                    contentDescription = "Podcast Image",
                    modifier = Modifier.size(100.dp) // Added size for consistency
                )
                Spacer(Modifier.height(16.dp))
                Text(podcast.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(podcast.description)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onPlayClick) {
                    Text("PLAY")
                }
            }
        }
    }
}