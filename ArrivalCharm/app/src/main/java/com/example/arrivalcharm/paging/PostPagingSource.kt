package com.example.arrivalcharm.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.*
import com.example.arrivalcharm.api.TestApiService
import com.example.arrivalcharm.datamodel.Post
import kotlinx.coroutines.flow.Flow

class PostPagingSource(private val apiService: TestApiService) : PagingSource<Int, Post>() {
    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.id
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize

            val response = apiService.getPosts(page, pageSize)

            LoadResult.Page(
                data = response,
                prevKey = if (page > 1) page - 1 else null,
                nextKey = if (response.isNotEmpty()) page + 1 else null
            )
        } catch (e: java.lang.Exception) {
            LoadResult.Error(e)
        }
    }
}

class PostRepository(private val apiService: TestApiService) {
    fun getPosts(): Pager<Int, Post> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { PostPagingSource(apiService) }
        )
    }
}

class PostViewModel(private val repository: PostRepository) : ViewModel() {
    fun getPosts(): Flow<PagingData<Post>> = repository.getPosts().flow
}

class PostViewModelFactory(private val repository: PostRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}