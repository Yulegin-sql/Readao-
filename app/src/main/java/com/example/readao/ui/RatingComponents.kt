package com.example.readao.ui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RatingStars(
    rating: Double,
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star
                else Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (count > 0) "%.1f (%d)".format(rating, count) else "Нет оценок",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun InteractiveRatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star
                else Icons.Outlined.StarBorder,
                contentDescription = "$i звёзд",
                tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier
                    .size(36.dp)
                    .padding(2.dp)
                    .clickable { onRatingChanged(i) }
            )
        }
    }
}