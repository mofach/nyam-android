package com.project.nyam.presentation.dashboard.tabs

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.nyam.data.model.NewsItem
import androidx.compose.ui.Alignment
import androidx.compose.material3.pulltorefresh.PullToRefreshBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsTab(
    newsList: List<NewsItem>,
    onRefresh: ((Boolean) -> Unit) -> Unit // Fungsi refresh dari NyamApp
) {
    val context = LocalContext.current
    var isRefreshing by remember { mutableStateOf(false) }

    // Wrapper Pull to Refresh
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            onRefresh { isRefreshing = false } // Selesai refresh, matikan loading
        },
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Info Kesehatan Terkini",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            if (newsList.isEmpty() && !isRefreshing) {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tidak ada berita tersedia", color = Color.Gray)
                    }
                }
            }

            items(newsList) { news ->
                NewsCard(news) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.link))
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun NewsCard(news: NewsItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = news.thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = news.source,
                    fontSize = 10.sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = news.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = news.snippet,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}