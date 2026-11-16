package com.fajar.cineverse.movie_discovery.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.fajar.cineverse.core.ui.theme.Black10
import com.fajar.cineverse.core.ui.theme.Grey69
import com.fajar.cineverse.core.ui.R as CoreR


@Composable
internal fun SliverItemCard(
    modifier: Modifier = Modifier,
    text: String,
    onClicked: () -> Unit = { }
) {
    Row(
        modifier = Modifier
            .heightIn(min = 48.dp)
            .clickable(onClick = onClicked)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            color = Black10
        )

        Icon(
            imageVector = ImageVector.vectorResource(CoreR.drawable.ic_arrow_right),
            tint = Grey69,
            contentDescription = null
        )
    }
}