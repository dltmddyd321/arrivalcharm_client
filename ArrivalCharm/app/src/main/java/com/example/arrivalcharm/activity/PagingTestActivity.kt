package com.example.arrivalcharm.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.arrivalcharm.adapter.PostAdapter
import com.example.arrivalcharm.api.ApiTestUtil
import com.example.arrivalcharm.databinding.ActivityPagingTestBinding
import com.example.arrivalcharm.paging.PostRepository
import com.example.arrivalcharm.paging.PostViewModel
import com.example.arrivalcharm.paging.PostViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PagingTestActivity : AppCompatActivity() {

    private val viewModel: PostViewModel by viewModels {
        PostViewModelFactory(
            PostRepository(
                ApiTestUtil().testApiService
            )
        )
    }
    private lateinit var binding: ActivityPagingTestBinding
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagingTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = PostAdapter()

        binding.pagingRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.pagingRecyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.getPosts().collectLatest {
                adapter.submitData(it)
            }
        }
    }
}