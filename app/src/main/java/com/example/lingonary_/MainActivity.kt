package com.example.lingonary_

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner // 🟢 NEW IMPORT
import androidx.lifecycle.Lifecycle // 🟢 NEW IMPORT
import androidx.lifecycle.LifecycleEventObserver // 🟢 NEW IMPORT
import com.example.lingonary_.database.WordDatabase
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
                val lifecycleOwner = LocalLifecycleOwner.current // 🟢 1. Get Lifecycle Owner
                // Navigation State
                var currentScreen by remember { mutableStateOf("home") }
                var selectedWordObj by remember { mutableStateOf<Word?>(null) }
                // Database
                val coroutineScope = rememberCoroutineScope()
                val db = remember { WordDatabase.getInstance(context) }
                val wordDao = remember { db.wordDao() }
                val transcriptData = remember { loadTranscriptFromJson(context, "tradicion_navidena.json") }
                // Main Data State
                var savedWordsList by remember { mutableStateOf<List<Word>>(emptyList()) }
                // ensures data reloads every time we return to the app (like from Quiz/Review mode)
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            // User came back (e.g. from Quiz) -> Reload Data!
                            coroutineScope.launch(Dispatchers.IO) {
                                savedWordsList = wordDao.getAllSavedWords()
                            }
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    when (currentScreen) {
                        "home" -> HomeScreen(
                            onPodcastClick = { currentScreen = "player" },
                            onWordLibClick = { currentScreen = "wordlib" }
                        )
                        "player" -> PlayerScreen(
                            transcriptData = transcriptData,
                            title = testTitle,
                            savedWords = savedWordsList.map { it.learning },

                            // Save Logic
                            onSaveWord = { wordItem ->
                                coroutineScope.launch(Dispatchers.IO) {
                                    val cleanWord = wordItem.word.trim { !it.isLetterOrDigit() }
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
                                    val allWords = wordDao.getAllWords()
                                    val wordArrayList = ArrayList(allWords)
                                    withContext(Dispatchers.Main) {
                                        val intent = Intent(context, QuizActivity::class.java)
                                        intent.putParcelableArrayListExtra("wordList", wordArrayList)
                                        context.startActivity(intent)
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