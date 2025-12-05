package com.example.lingonary_

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lingonary_.ui.theme.*

@Composable
fun HomeScreen(
    onPodcastClick: () -> Unit = {},
    onWordLibClick: () -> Unit = {}
) {
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // --- HEADER ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Greeting
                Column {
                    Text(
                        text = "Hello Eliana!",
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
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
                            text = "Chinese",
                            style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Image(
                        painter = painterResource(id = R.drawable.ic_avatar_face),
                        contentDescription = "User",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            //--Search bar--
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(37.dp)
                    .shadow(2.dp, RoundedCornerShape(18.dp))
                    .background(White, RoundedCornerShape(18.dp))
                    .border(1.dp, BlackStroke, RoundedCornerShape(18.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_search_bar),
                        contentDescription = "Search",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Search for podcasts...",
                        style = TextStyle(color = Color.Gray, fontSize = 14.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            //--Featured--
            SectionHeader("Featured")
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(3) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_podcast_card),
                        contentDescription = "Podcast",
                        modifier = Modifier
                            .width(120.dp)
                            .height(120.dp)
                            .shadow(2.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onPodcastClick() },
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            //--Recent played--
            SectionHeader("Recent Played")
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(3) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_podcast_card),
                        contentDescription = "Podcast",
                        modifier = Modifier
                            .width(120.dp)
                            .height(120.dp)
                            .shadow(2.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onPodcastClick() },
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
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
        Text(
            text = "View All",
            style = TextStyle(fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        )
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
                // Home(selected)
                NavIcon(
                    iconRes = R.drawable.ic_home_filled,
                    label = "Home",
                    isSelected = true,
                    onClick = {} // Already home
                )
                // Word lib
                NavIcon(
                    iconRes = R.drawable.ic_wordlib,
                    label = "Word Library",
                    isSelected = false,
                    onClick = onWordLibClick // Connects the click
                )
                // Settings
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
            color = if(isSelected) Color.Black else Color.Gray
        )
    }
}