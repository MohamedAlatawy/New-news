package com.example.newnews.ui.news

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newnews.R
import com.example.newnews.data.utils.Constants.Companion.PAGE_SIZE
import com.example.newnews.databinding.FragmentNewsBinding
import com.example.newnews.domain.utils.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsFragment : Fragment(R.layout.fragment_news) {

    private val viewModel: NewsViewModel by viewModels()

    lateinit var binding: FragmentNewsBinding
    lateinit var newsAdapter: NewsAdapter
    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewsBinding.bind(view)

        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.layout_error, null)
        binding.itemHeadlinesError.btnRetry.setOnClickListener {
        }
        setUpNewsRV()
        viewModel.news.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is DataState.Success<*> -> {
                    hideError()
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles?.toList())
                        val totalPages = newsResponse.totalResults / PAGE_SIZE + 2
                        isLastPage = viewModel.pageNumber == totalPages
                        if (isLastPage) {
                            binding.rvNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is DataState.Error -> {
                    hideProgressBar()
                    showError(response.message.toString())
                }

                is DataState.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    fun hideError() {
        binding.itemHeadlinesError.root.visibility = View.INVISIBLE
        isError = false
    }

    fun showError(message: String) {
        binding.itemHeadlinesError.root.visibility = View.VISIBLE
        binding.itemHeadlinesError.tvError.text = message
        isError = true
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= PAGE_SIZE
            val shouldPaginate =
                isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                        && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getNews(countryCode = "us")
                isScrolling = false
            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setUpNewsRV() {
        newsAdapter = NewsAdapter {
            val bundle = Bundle().apply {
                putSerializable("article", it.url)
            }
            findNavController().navigate(R.id.action_to_articleDetailsFragment, bundle)
        }

        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@NewsFragment.scrollListener)
        }

    }
}