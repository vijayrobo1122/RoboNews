package app.robo.news.ui.news

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import app.robo.news.R
import app.robo.news.data.model.other.News
import app.robo.news.databinding.ItemTopNewsBinding
import app.robo.news.utils.BindableAdapter
import app.robo.news.utils.TYPE_NEWS_DETAIL

class TopNewsAdapter(private val clickListener: (News, Int, Int) -> Unit) :
    RecyclerView.Adapter<TopNewsAdapter.TopNewsViewHolder>(),
    BindableAdapter<ArrayList<News>> {

    companion object {
        private val TAG: String = TopNewsAdapter::class.java.simpleName
    }

    private var items = ArrayList<News>()

    override fun setData(data: ArrayList<News>) {
        this.items = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopNewsViewHolder {
        val binding: ItemTopNewsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_top_news, parent, false
        )
        return TopNewsViewHolder(binding, clickListener)
    }


    override fun onBindViewHolder(holder: TopNewsViewHolder, position: Int) {
        val news = items[position]
        holder.bind(news)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getItem(position: Int): News {
        return items[position]
    }

    inner class TopNewsViewHolder(
        private val itemBinding: ItemTopNewsBinding,
        val clickListener: (News, Int, Int) -> Unit
    ) :
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        private lateinit var news: News

        init {
            itemBinding.root.setOnClickListener(this)
        }

        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: News) {
            this.news = item

            itemBinding.apply {
                dataModel = item
                index = bindingAdapterPosition
                executePendingBindings()
            }

        }

        override fun onClick(v: View?) {
            clickListener(news, bindingAdapterPosition, TYPE_NEWS_DETAIL)
        }

    }
}