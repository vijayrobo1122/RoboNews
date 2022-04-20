package app.robo.news.ui.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.robo.news.R
import app.robo.news.data.model.other.News
import app.robo.news.data.remote.Status
import app.robo.news.databinding.FragmentNewsBinding
import app.robo.news.ui.detail.DetailFragment
import app.robo.news.ui.home.HomeActivity
import app.robo.news.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewsFragment : Fragment() {

    companion object {

        private val TAG: String = NewsFragment::class.java.simpleName

        fun newInstance(): NewsFragment {
            return NewsFragment()
        }
    }

    private lateinit var binding: FragmentNewsBinding

    private val viewModel: NewsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.e(TAG, "onCreateView")
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.fragment_news, container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "onViewCreated")
        setToolbar()
        observerMessage()
        observerTopNewsListResponse()
        observerPopularNewsListResponse()
        initialiseTopNewsAdapter()
        initialisePopularNewsAdapter()
        viewModel.fetchTopNewsList()
    }

    private fun setToolbar() {
        if (activity != null) {
            (activity as HomeActivity).supportActionBar?.title = ""
        }
    }

    private fun initialiseTopNewsAdapter() {
        val topNewsAdapter = TopNewsAdapter(
            clickListener = { news: News, pos: Int, type: Int ->
                Log.d(TAG, "initialiseTopNewsAdapter item click")
                openDetailView(news)
            }
        )
        binding.topNewsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.topNewsRecyclerView.adapter = topNewsAdapter
    }

    private fun initialisePopularNewsAdapter() {
        val adapter = PopularNewsAdapter(
            clickListener = { news: News, pos: Int, type: Int ->
                openDetailView(news)

            }
        )
        binding.popularNewsRecyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.popularNewsRecyclerView.layoutManager = layoutManager

        binding.mainScrollView.viewTreeObserver.addOnScrollChangedListener {
            val view =
                binding.mainScrollView.getChildAt(binding.mainScrollView.childCount - 1) as View
            val diff: Int =
                view.bottom - (binding.mainScrollView.height + binding.mainScrollView.scrollY)
            if (diff == 0 && viewModel.popularNewsList.value!!.size > 0 && !viewModel.lastPage.value!!) {
                // your pagination code
                //viewModel._loading.value = true
                binding.viewLoading.visible()
                viewModel.fetchPopularNewsList(viewModel.pageIndex.value!!)
            }
        }

    }

    private fun observerMessage() {
        viewModel.message.observeEvent(this) {
            activity?.toast(it)
        }
    }

    private fun observerTopNewsListResponse() {
        viewModel.topNewsListResponse.observeEvent(this) {
            Log.d(TAG, "observerTopNewsListResponse observeEvent")
            when (it.status) {
                Status.LOADING -> {
                    Log.d(TAG, "LOADING...")
                    binding.loaderView.progressView.visible()
                }
                Status.SUCCESS -> {
                    try {
                        Log.d(TAG, "SUCCESS...")
                        binding.loaderView.progressView.gone()
                        //viewModel._loading.value = false
                        val success = it.data?.success ?: false
                        val message = it.data?.message ?: ""
                        Log.d(TAG, "success : $success")
                        Log.d(TAG, "message : $message")
                        if (!success) {
                            activity?.toast(message)
                        }
                        binding.topNewsRecyclerView.adapter!!.notifyDataSetChanged()
                        viewModel.fetchPopularNewsList(DEFAULT_PAGE_INDEX)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                Status.ERROR -> {
                    try {
                        Log.d(TAG, "ERROR...")
                        binding.loaderView.progressView.gone()
                        binding.viewLoading.gone()
                        activity?.toast(it.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun observerPopularNewsListResponse() {
        viewModel.popularNewsListResponse.observeEvent(this) {
            when (it.status) {
                Status.LOADING -> {
                    Log.d(TAG, "LOADING...")
                    if (!binding.viewLoading.isVisible()) {
                        binding.loaderView.progressView.visible()
                    }
                }
                Status.SUCCESS -> {
                    try {
                        Log.d(TAG, "SUCCESS...")
                        binding.loaderView.progressView.gone()
                        binding.viewLoading.gone()
                        //viewModel._loading.value = false
                        val success = it.data?.success ?: false
                        val message = it.data?.message ?: ""
                        Log.d(TAG, "success : $success")
                        Log.d(TAG, "message : $message")
                        if (!success) {
                            activity?.toast(message)
                        }
                        binding.popularNewsRecyclerView.adapter!!.notifyDataSetChanged()
                        viewModel._showEmptyView.value = viewModel.popularNewsList.value!!.size < 1
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                Status.ERROR -> {
                    try {
                        Log.d(TAG, "ERROR...")
                        binding.loaderView.progressView.gone()
                        binding.viewLoading.gone()
                        activity?.toast(it.message)
                    } catch (e: Exception) {
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

        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, detailsFragment, "DetailFragment")
            .addToBackStack(null)
            .commit()

    }
}