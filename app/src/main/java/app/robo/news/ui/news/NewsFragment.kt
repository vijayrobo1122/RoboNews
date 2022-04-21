package app.robo.news.ui.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.robo.news.R
import app.robo.news.data.model.other.News
import app.robo.news.data.remote.Status
import app.robo.news.databinding.FragmentNewsBinding
import app.robo.news.ui.HomeActivity
import app.robo.news.ui.detail.DetailFragment
import app.robo.news.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewsFragment : Fragment() {

    companion object {

        private val TAG: String = NewsFragment::class.java.simpleName

        fun newInstance(): NewsFragment {
            return NewsFragment()
        }
    }

    private lateinit var binding: FragmentNewsBinding

    private val myViewModel: NewsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.fragment_news, container, false
        )
        binding.apply {
            lifecycleOwner = this@NewsFragment
            viewModel = myViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        observerMessage()
        observerTopNewsListResponse()
        observerPopularNewsListResponse()
        initialiseTopNewsAdapter()
        initialisePopularNewsAdapter()
        myViewModel.fetchTopNewsList()
    }

    private fun setToolbar() {
        activity?.let {
            (it as HomeActivity).supportActionBar?.title = ""
        }
    }

    private fun initialiseTopNewsAdapter() {
        val topNewsAdapter = TopNewsAdapter(
            clickListener = { news: News ->
                openDetailView(news)
            }
        )
        binding.topNewsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = topNewsAdapter
        }
    }

    private fun initialisePopularNewsAdapter() {
        val popularNewsAdapter = PopularNewsAdapter(
            clickListener = { news: News ->
                openDetailView(news)
            }
        )
        binding.popularNewsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = popularNewsAdapter
        }

        binding.mainScrollView.viewTreeObserver.addOnScrollChangedListener {
            val view =
                binding.mainScrollView.getChildAt(binding.mainScrollView.childCount - 1) as View
            val diff: Int =
                view.bottom - (binding.mainScrollView.height + binding.mainScrollView.scrollY)
            if (diff == 0 && myViewModel.popularNewsList.value!!.size > 0 && !myViewModel.lastPage.value!!) {
                binding.viewLoading.visible()
                myViewModel.fetchPopularNewsList(myViewModel.pageIndex.value!!)
            }
        }

    }

    private fun observerMessage() {
        myViewModel.message.observeEvent(this) {
            activity?.toast(it)
        }
    }

    private fun observerTopNewsListResponse() {
        myViewModel.topNewsListResponse.observeEvent(this) {
            Log.d(TAG, "observerTopNewsListResponse observeEvent")
            when (it.status) {
                Status.LOADING -> {
                    Log.d(TAG, "LOADING...")
                    binding.loaderView.progressView.visible()
                }
                Status.SUCCESS -> {
                    runCatching {
                        Log.d(TAG, "SUCCESS...")
                        binding.loaderView.progressView.gone()
                        val success = it.data?.success ?: false
                        val message = it.data?.message ?: ""
                        if (!success) {
                            activity?.toast(message)
                        }
                        binding.topNewsRecyclerView.adapter!!.notifyDataSetChanged()
                        myViewModel.fetchPopularNewsList(DEFAULT_PAGE_INDEX)
                    }.onFailure { e ->
                        e.printStackTrace()
                    }
                }
                Status.ERROR -> {
                    runCatching {
                        Log.d(TAG, "ERROR...")
                        binding.loaderView.progressView.gone()
                        binding.viewLoading.gone()
                        activity?.toast(it.message)
                    }.onFailure { e ->
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun observerPopularNewsListResponse() {
        myViewModel.popularNewsListResponse.observeEvent(this) {
            when (it.status) {
                Status.LOADING -> {
                    Log.d(TAG, "LOADING...")
                    if (!binding.viewLoading.isVisible()) {
                        binding.loaderView.progressView.visible()
                    }
                }
                Status.SUCCESS -> {
                    runCatching {
                        Log.d(TAG, "SUCCESS...")
                        binding.loaderView.progressView.gone()
                        binding.viewLoading.gone()
                        val success = it.data?.success ?: false
                        val message = it.data?.message ?: ""
                        if (!success) {
                            activity?.toast(message)
                        }
                        binding.popularNewsRecyclerView.adapter!!.notifyDataSetChanged()
                        myViewModel._showEmptyView.value =
                            myViewModel.popularNewsList.value!!.size < 1
                    }.onFailure { e ->
                        e.printStackTrace()
                    }
                }
                Status.ERROR -> {
                    runCatching {
                        Log.d(TAG, "ERROR...")
                        binding.loaderView.progressView.gone()
                        binding.viewLoading.gone()
                        activity?.toast(it.message)
                    }.onFailure { e ->
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun openDetailView(news: News) {

        val bundle = Bundle()
        bundle.putSerializable(EXTRA_KEY_NEWS, news)

        val detailsFragment = DetailFragment.newInstance()
        detailsFragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, detailsFragment, "DetailFragment").addToBackStack(null)
            .commit()

    }
}