package com.fajar.cineverse.movie_detail.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
internal fun YoutubeVideoPlayer(modifier: Modifier = Modifier, videoKey: String) {
    val playerView = rememberYoutubePlayerView(videoKey)

    AndroidView(
        modifier = modifier,
        factory = { playerView }
    )
}

@Composable
internal fun YoutubeVideoPlayer(
    modifier: Modifier = Modifier,
    playerView: YouTubePlayerView
) {
    AndroidView(
        modifier = modifier,
        factory = { playerView }
    )
}

@Composable
internal fun rememberYoutubePlayerView(
    videoKey: String
): YouTubePlayerView {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val playerView = remember {
        YouTubePlayerView(context).apply {
            lifecycleOwner.lifecycle.addObserver(this)

            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(videoKey, 0f)
                }
            })
        }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(playerView)
            playerView.release()
        }
    }

    return playerView
}