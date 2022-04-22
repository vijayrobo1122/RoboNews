package app.robo.news.ui.news

import app.robo.news.data.remote.Status
import app.robo.news.data.repository.NewsRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class NewsViewModelTest : DescribeSpec({

    lateinit var myViewModel: NewsViewModel
    lateinit var repository: NewsRepository
    beforeTest {
        myViewModel = mockk()
        repository = mockk()
    }

    describe("fetching top headline news") {
        it("Loader is visible then will be return true") {
            myViewModel.loading shouldBe true
        }
        it("Loader is gone then will be return false") {
            myViewModel.loading shouldBe false
        }

        it("fetch top headline news list") {
            val response = repository.getTopHeadLineNewsList(
                country = "in",
                category = "sports",
                pageSize = 1,
                page = 1
            )
            it("response status loading") {
                response.value?.status shouldBe Status.LOADING

            }
            it("response status success") {
                response.value?.status shouldBe Status.SUCCESS
            }
            it("response status error") {
                response.value?.status shouldBe Status.ERROR
            }
        }
    }
})