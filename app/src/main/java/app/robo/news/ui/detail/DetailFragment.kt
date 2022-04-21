package app.robo.news.ui.detail

import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.robo.news.R
import app.robo.news.data.model.other.News
import app.robo.news.data.remote.Status
import app.robo.news.databinding.FragmentDetailBinding
import app.robo.news.ui.HomeActivity
import app.robo.news.ui.news.PopularNewsAdapter
import app.robo.news.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : Fragment() {

    companion object {

        private val TAG: String = DetailFragment::class.java.simpleName

        fun newInstance(): DetailFragment {
            return DetailFragment()
        }
    }

    private lateinit var binding: FragmentDetailBinding

    private val myViewModel: DetailViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_detail, container, false)
        binding.viewModel = myViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchIntentData()
        setToolbar()
        observerMessage()
        observerPopularNewsListResponse()
        initialisePopularNewsAdapter()
        setUpWebview()
    }


    private fun fetchIntentData() {
        if (arguments != null) {
            if (requireArguments().containsKey(EXTRA_KEY_NEWS)) {
                val newsData: News? =
                    requireArguments().getSerializable(EXTRA_KEY_NEWS) as News?
                if (newsData != null) {
                    myViewModel._selectedNews.value = newsData
                }
            }
        }
    }

    private fun setToolbar() {
        if (activity != null) {
            (activity as HomeActivity).supportActionBar?.title =
                myViewModel.selectedNews.value?.url ?: ""
        }
    }

    private fun setUpWebview() {

        // Get the web view settings instance
        val settings = binding.webview.settings
        settings.apply {
            // Enable java script in web view
            javaScriptEnabled = true

            // Enable zooming in web view
            setSupportZoom(false)
        }

        if (myViewModel.selectedNews.value != null && !TextUtils.isEmpty(myViewModel.selectedNews.value!!.url)) {
            binding.webview.loadUrl(myViewModel.selectedNews.value!!.url!!)
            binding.webview.visible()
        } else {
            binding.webview.gone()
        }

        // Set web view client
        binding.webview.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                // Page loading started
                // Do something
                binding.loaderView.progressView.visible()
            }

            override fun onPageFinished(view: WebView, url: String) {
                // Page loading finished
                // Enable disable back forward button
                binding.loaderView.progressView.gone()
                myViewModel.fetchPopularNewsList(DEFAULT_PAGE_INDEX)
            }
        }
    }


    private fun initialisePopularNewsAdapter() {
        val adapter = PopularNewsAdapter(
            clickListener = { _: News, _: Int, _: Int ->
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
            if (diff == 0 && myViewModel.popularNewsList.value!!.size > 0 && !myViewModel.lastPage.value!!) {
                // your pagination code
                //viewModel._loading.value = true
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
                        myViewModel._showEmptyView.value =
                            myViewModel.popularNewsList.value!!.size < 1
                        binding.txtTitle.visible()
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

}