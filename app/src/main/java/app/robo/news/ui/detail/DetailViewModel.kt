package app.robo.news.ui.detail

import androidx.lifecycle.*
import app.robo.news.data.model.api.NewsListResponse
import app.robo.news.data.model.other.News
import app.robo.news.data.remote.ApiResult
import app.robo.news.data.remote.Status
import app.robo.news.data.repository.NewsRepository
import app.robo.news.utils.DEFAULT_PAGE_INDEX
import app.robo.news.utils.Event
import kotlinx.coroutines.launch

class DetailViewModel constructor(private val newsRepository: NewsRepository) : ViewModel() {

    val _selectedNews: MutableLiveData<News> = MutableLiveData()
    val selectedNews: LiveData<News>
        get() = _selectedNews

    val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    val _lastPage: MutableLiveData<Boolean> = MutableLiveData(false)
    val lastPage: LiveData<Boolean>
        get() = _lastPage

    val _pageIndex: MutableLiveData<Int> = MutableLiveData(DEFAULT_PAGE_INDEX)
    val pageIndex: LiveData<Int>
        get() = _pageIndex

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = _message

    val _showEmptyView: MutableLiveData<Boolean> = MutableLiveData(false)
    val showEmptyView: LiveData<Boolean>
        get() = _showEmptyView

    val _popularNewsList: MutableLiveData<ArrayList<News>> = MutableLiveData(ArrayList())
    val popularNewsList: LiveData<ArrayList<News>>
        get() = _popularNewsList

    private val _popularNewsListResponse = MediatorLiveData<Event<ApiResult<NewsListResponse>>>()
    val popularNewsListResponse: LiveData<Event<ApiResult<NewsListResponse>>>
        get() = _popularNewsListResponse

    fun fetchPopularNewsList(pageIndex: Int) {
        viewModelScope.launch {
            _popularNewsListResponse.addSource(newsRepository.getPopularNewsList(page = pageIndex)) {
                if (it.status == Status.SUCCESS && it.data != null) {
                    runCatching {
                        val list = it.data.newsList
                        if (pageIndex == DEFAULT_PAGE_INDEX) _popularNewsList.value!!.clear()
                        _popularNewsList.value?.addAll(list)
                        if (it.data.totalResults > _popularNewsList.value!!.size) {
                            _pageIndex.value = pageIndex + 1
                        } else {
                            _pageIndex.value = -1
                            _lastPage.value = true
                        }
                    }.onFailure { e ->
                        e.printStackTrace()
                    }
                }
                _popularNewsListResponse.value = Event(it)
            }
        }
    }

}