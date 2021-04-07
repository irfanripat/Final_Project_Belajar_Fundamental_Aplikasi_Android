package com.irfan.consumerapp.activity.main.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.irfan.consumerapp.R
import com.irfan.consumerapp.databinding.ItemGithubUserBinding
import com.irfan.consumerapp.model.DetailUser

class GithubAdapter(private val listItem: MutableList<DetailUser>, private val listener: (Any) -> Unit) : RecyclerView.Adapter<GithubAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder =
        MainViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_github_user, parent, false))

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(listItem[position], listener)
    }

    override fun getItemCount() = listItem.size

    inner class MainViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemGithubUserBinding.bind(view)

        fun bind(listItem: DetailUser, listener: (Any) -> Unit) {
            binding.tvUsername.text = listItem.login
            binding.tvId.text = String.format("Id : ${listItem.id}")

            binding.shimmer.visibility = View.VISIBLE
            binding.shimmer.startShimmer()

            Glide.with(view.context)
                .load(listItem.avatar_url)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            binding.shimmer.stopShimmer()
                            binding.shimmer.visibility = View.GONE
                            return false
                        }
                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            binding.shimmer.stopShimmer()
                            binding.shimmer.visibility = View.GONE
                            return false
                        }
                    })
                .error(android.R.drawable.stat_notify_error)
                .into(binding.circleImageView)

            itemView.setOnClickListener{listener(listItem)}
        }
    }
}
