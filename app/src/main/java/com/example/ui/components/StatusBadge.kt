package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WatchStatus
import com.example.ui.theme.LocalCinemoraColors

@Composable
fun StatusBadge(
    status: WatchStatus,
    modifier: Modifier = Modifier
) {
    val colors = LocalCinemoraColors.current
    val color = when (status) {
        WatchStatus.PLAN_TO_WATCH -> colors.statusPlanToWatch
        WatchStatus.WATCHING -> colors.statusWatching
        WatchStatus.COMPLETED -> colors.statusCompleted
        WatchStatus.ON_HOLD -> colors.statusOnHold
        WatchStatus.DROPPED -> colors.statusDropped
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .border(0.5.dp, color.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = status.label,
            color = color,
            fontSize = 11.sp
        )
    }
}
