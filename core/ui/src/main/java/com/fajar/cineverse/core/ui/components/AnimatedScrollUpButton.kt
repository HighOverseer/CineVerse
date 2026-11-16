package com.fajar.cineverse.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fajar.cineverse.core.ui.R
import com.fajar.cineverse.core.ui.theme.Green55


@Composable
fun AnimatedScrollUpButton(
    modifier: Modifier = Modifier,
    iconSize: Dp = 40.dp,
    isScrollUpButtonVisibleProvider: () -> Boolean = { false },
    onClick: () -> Unit = { }
) {
    AnimatedVisibility(
        modifier = modifier
            .size(iconSize),
        visible = isScrollUpButtonVisibleProvider()
    ) {
        IconButton(
            onClick = onClick
        ) {
            val scrollUpButtonModifier = Modifier
                .fillMaxSize()
                .rotate(-90f)
                .alpha(0.7f)
                .background(color = Green55, shape = CircleShape)
                .clip(CircleShape)
            Box(
                modifier = scrollUpButtonModifier
            ) {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .align(Alignment.Center),
                    imageVector = ImageVector
                        .vectorResource(R.drawable.ic_arrow_right),
                    tint = Color.White,
                    contentDescription = null
                )
            }

        }
    }
}