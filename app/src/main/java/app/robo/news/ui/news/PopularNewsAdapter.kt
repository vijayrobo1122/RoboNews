package app.robo.news.ui.news

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import app.robo.news.R
import app.robo.news.data.model.other.News
import app.robo.news.databinding.ItemPopularNewsBinding
import app.robo.news.utils.BindableAdapter

class PopularNewsAdapter(private val clickListener: (News) -> Unit) :
    RecyclerView.Adapter<PopularNewsAdapter.PopularNewsViewHolder>(),
    BindableAdapter<ArrayList<News>> {

    private var items = ArrayList<News>()

    override fun setData(data: ArrayList<News>) {
        this.items = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularNewsViewHolder {
        val binding: ItemPopularNewsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_popular_news, parent, false
        )
        return PopularNewsViewHolder(binding, clickListener)
    }


    override fun onBindViewHolder(holder: PopularNewsViewHolder, position: Int) {
        val news = items[position]
        holder.bind(news)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class PopularNewsViewHolder(
        private val itemBinding: ItemPopularNewsBinding, val clickListener: (News) -> Unit
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
            clickListener(news)
        }
    }
}