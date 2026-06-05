package com.dubproductions.sudokuchallenge.game.ui.complete

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dubproductions.sudokuchallenge.game.ui.puzzle.playTimeString

@Composable
fun CompleteScreen(
    playTime: Long,
    mistakeCount: Int
) {
    CompleteScreenContent(
        playTime = playTime,
        mistakeCount = mistakeCount
    )
}

@Composable
fun CompleteScreenContent(
    playTime: Long,
    mistakeCount: Int
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {
            Text(
                fontSize = 30.sp,
                text = "Puzzle Complete!",
                color = MaterialTheme.colorScheme.onSurface
            )

            Icon(
                modifier = Modifier
                    .size(100.dp),
                imageVector = Icons.Default.CheckCircleOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                fontSize = 20.sp,
                text = playTimeString(playTime, Locale.current.platformLocale),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                fontSize = 20.sp,
                text = "Mistake Count: $mistakeCount",
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun CompleteScreenPreview() {
    CompleteScreenContent(
        playTime = 100,
        mistakeCount = 3
    )
}