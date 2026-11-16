package com.fajar.cineverse.core.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.fajar.cineverse.core.ui.model.ExceptionWithUIText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.flowOf

@Composable
fun <T : Any> rememberDummyPagingItems(
    list: ImmutableList<T> = persistentListOf()
): LazyPagingItems<T> {
    return remember {
        flowOf(PagingData.from(list))
    }.collectAsLazyPagingItems()
}

val <T : Any> LazyPagingItems<T>.isInitialLoadError
    get() = loadState.refresh is LoadState.Error
val <T : Any> LazyPagingItems<T>.isAppendLoadError
    get() = loadState.append is LoadState.Error

val <T : Any> LazyPagingItems<T>.isInitialLoadFinished
    get() = loadState.refresh is LoadState.NotLoading
val <T : Any> LazyPagingItems<T>.isListEmpty
    get() = itemCount == 0 && isInitialLoadFinished
val <T : Any> LazyPagingItems<T>.isRefreshing
    get() = loadState.refresh is LoadState.Loading
val <T : Any> LazyPagingItems<T>.isAppending
    get() = loadState.append is LoadState.Loading

val <T : Any> LazyPagingItems<T>.initialLoadErrorMessage
    @Composable get() = (loadState.refresh as? LoadState.Error)
        ?.error?.let { error ->
            if (error is ExceptionWithUIText) {
                error.text.getValue()
            } else error.message
        }

val <T : Any> LazyPagingItems<T>.appendErrorMessage
    @Composable get() = (loadState.append as? LoadState.Error)
        ?.error?.let { error ->
            if (error is ExceptionWithUIText) {
                error.text.getValue()
            } else error.message
        }