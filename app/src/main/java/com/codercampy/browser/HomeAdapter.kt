package com.codercampy.browser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codercampy.browser.databinding.ItemHomeNewsBinding
import com.codercampy.browser.databinding.ItemHomeStarredBinding

class HomeAdapter(
    private val list: List<Any>,
    private val listener: HomeAdapterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val itemStarred = 1
    val itemNews = 2

    override fun getItemViewType(position: Int): Int {
        return if (list[position] is HomeStarredItem) {
            itemStarred
        } else {
            itemNews
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == itemStarred) {
            StarredViewHolder(
                ItemHomeStarredBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            ArticleViewHolder(
                ItemHomeNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArticleViewHolder) {
            val article = list[position] as HomeArticleItem

            holder.binding.tvTitle.text = article.title
            Glide.with(holder.binding.ivImage).load(article.image).into(holder.binding.ivImage)

            holder.binding.tvDate.text = parseDate(article.date)

            holder.binding.root.setOnClickListener {
                listener.onArticleClick(article)
            }

            holder.binding.btnShare.setOnClickListener {
                listener.onArticleShare(article)
            }

        } else if (holder is StarredViewHolder) {
            val item = list[position] as HomeStarredItem

            holder.binding.tv1.text = item.t1
            Glide.with(holder.binding.iv1).load(item.i1).into(holder.binding.iv1)

            holder.binding.tv2.text = item.t2
            Glide.with(holder.binding.iv2).load(item.i2).into(holder.binding.iv2)

            holder.binding.tv3.text = item.t3
            Glide.with(holder.binding.iv3).load(item.i3).into(holder.binding.iv3)

            holder.binding.tv4.text = item.t4
            Glide.with(holder.binding.iv4).load(item.i4).into(holder.binding.iv4)

            holder.binding.clSearch.setOnClickListener {
                listener.onSearchBarClick()
            }

            holder.binding.btnCamera.setOnClickListener {
                listener.onCameraClick()
            }

            holder.binding.btnMic.setOnClickListener {
                listener.onMicClick()
            }

        }
    }

}

class ArticleViewHolder(val binding: ItemHomeNewsBinding) : RecyclerView.ViewHolder(binding.root) {
}

class StarredViewHolder(val binding: ItemHomeStarredBinding) :
    RecyclerView.ViewHolder(binding.root) {
}

interface HomeAdapterListener {

    fun onArticleClick(articleItem: HomeArticleItem)

    fun onArticleShare(articleItem: HomeArticleItem)

    fun onStarredClick(link: String)

    fun onSearchBarClick()

    fun onMicClick()

    fun onCameraClick()

}