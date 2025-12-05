package com.example.lingonary_

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lingonary_.models.Podcast
import com.example.lingonary_.ui.theme.*

@Composable
fun HomeScreen(
    podcasts: List<Podcast>,
    onPodcastClick: (Podcast) -> Unit,
    onWordLibClick: () -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }
    val featuredPodcasts = podcasts.take(3)
    val recentPodcasts = podcasts.drop(3).take(3)
    val filteredForSearch = podcasts.filter { it.title.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = {
            HomeBottomNavBar(
                onWordLibClick = onWordLibClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // --- HEADER (STATIC) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Greeting
                Column {
                    Text(
                        text = "Hello Eliana!",
                        style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    )
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(4.dp)
                            .background(SageGreen, RoundedCornerShape(2.dp))
                    )
                }

                // Badge + avatar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(width = 60.dp, height = 26.dp)
                            .background(BadgeYellow, RoundedCornerShape(13.dp))
                            .border(1.dp, BlackStroke, RoundedCornerShape(13.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Spanish",
                            style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_avatar_face),
                        contentDescription = "User",
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))

            //--Search bar (STATIC)--
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                suggestions = filteredForSearch,
                onSuggestionClick = {
                    onPodcastClick(it) // Show popup
                    searchQuery = "" // Clear query
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- SCROLLABLE CONTENT ---
            LazyColumn(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                item {
                    SectionHeader("Featured")
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(featuredPodcasts) { podcast ->
                            PodcastItem(podcast, onPodcastClick)
                        }
                    }
                }

                item {
                    SectionHeader("Recent Played")
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(recentPodcasts) { podcast ->
                            PodcastItem(podcast, onPodcastClick)
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
fun PodcastItem(podcast: Podcast, onPodcastClick: (Podcast) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onPodcastClick(podcast) },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_podcast_card),
            contentDescription = "Podcast",
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .shadow(2.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Text(text = podcast.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    suggestions: List<Podcast>,
    onSuggestionClick: (Podcast) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val expanded = isFocused && query.isNotEmpty()

    Box {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search for podcasts...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .shadow(2.dp, RoundedCornerShape(18.dp))
                .background(White, RoundedCornerShape(18.dp))
                .border(1.dp, BlackStroke, RoundedCornerShape(18.dp))
                .onFocusChanged { isFocused = it.isFocused },
            leadingIcon = { Icon(painterResource(id = R.drawable.ic_search_bar), contentDescription = "Search") },
            shape = RoundedCornerShape(18.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                disabledContainerColor = White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { isFocused = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            suggestions.forEach { podcast ->
                DropdownMenuItem(
                    text = { Text(text = podcast.title) },
                    onClick = {
                        onSuggestionClick(podcast)
                        isFocused = false
                    }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = title,
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextBlack)
        )
//        Text(
//            text = "View All",
//            style = TextStyle(fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
//        )
    }
}

@Composable
fun HomeBottomNavBar(onWordLibClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .height(80.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(35.dp),
            color = White,
            shadowElevation = 10.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, NavStroke)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavIcon(
                    iconRes = R.drawable.ic_home_filled,
                    label = "Home",
                    isSelected = true,
                    onClick = {}
                )
                NavIcon(
                    iconRes = R.drawable.ic_wordlib,
                    label = "Word Library",
                    isSelected = false,
                    onClick = onWordLibClick
                )
                NavIcon(
                    iconRes = R.drawable.ic_setting_unfilled,
                    label = "Setting",
                    isSelected = false,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun NavIcon(iconRes: Int, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(if (isSelected) BadgeYellow else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.Black else Color.Gray
        )
    }
}