package com.example.newnews.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newnews.databinding.ItemArticleBinding
import com.example.newnews.domain.model.Article

class NewsAdapter(
    val onItemClick: (Article) -> Unit
) : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    private var onItemClickListener: ((Article) -> Unit)? = null
    lateinit var articleImage: ImageView
    lateinit var articleTitle: TextView
    lateinit var articleDescription: TextView
    lateinit var articlePublishedAt: TextView
    lateinit var articleSource: TextView

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
//        return ArticleViewHolder(
//            LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
//        )
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding, viewType)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    inner class ArticleViewHolder(private val binding: ItemArticleBinding, viewType: Int) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Article) {
            binding.apply {
                tvTitle.text = item.title
                tvDesc.text = item.description
                tvPublishedAt.text = item.publishedAt
                tvSource.text = item.source?.name
                Glide.with(binding.root).load(item.urlToImage).into(ivImage)

                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }
}