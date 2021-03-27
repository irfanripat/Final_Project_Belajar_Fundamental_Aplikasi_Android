package com.irfan.githubuser.activity.detail

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.irfan.githubuser.R
import com.irfan.githubuser.activity.detail.adapter.FragmentAdapter
import com.irfan.githubuser.databinding.ActivityDetailBinding
import com.irfan.githubuser.util.Commons.hide
import com.irfan.githubuser.util.Commons.show

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    private var isFavorite: Boolean = false

    companion object {
        const val EXTRA_GITHUB_USER = "github"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        binding.layoutError.textError.setTextColor(Color.WHITE)
        setContentView(binding.root)

        detailViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(DetailViewModel::class.java)

        val username = intent.getStringExtra(EXTRA_GITHUB_USER)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.apply {
            setTitleTextColor(Color.WHITE)
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = username
        }

        tabLayout = binding.content.tabLayout
        viewPager = binding.content.viewPager

        tabLayout.apply {
            addTab(this.newTab().setText(resources.getString(R.string.followers)))
            addTab(this.newTab().setText(resources.getString(R.string.following)))
            tabGravity = TabLayout.GRAVITY_FILL
        }

        val adapter = FragmentAdapter(supportFragmentManager, tabLayout.tabCount)
        viewPager.apply {
            this.adapter = adapter
            addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        getData(username!!)

        detailViewModel.isSuccess.observe(this, {isSuccess->
            when(isSuccess) {
                0 -> {
                    binding.layoutError.root.show()
                    binding.shimmerLayout.hide()
                }
            }
        })

        binding.layoutError.btnRefresh.setOnClickListener {
            getData(username)
        }

        binding.btnFavorite.setOnClickListener {
            isFavorite = !isFavorite
            when(isFavorite) {
                true -> addToFavorite()
                else -> removeFromFavorite()
            }
        }

    }

    private fun getData(username: String) {
        showShimmer()
        detailViewModel.getDetailUser(username).observe(this, {user->
            hideShimmer()
            if (user!=null) {
                binding.apply {
                    tvName.text = user.name
                    tvLocation.text = user.location?:resources.getString(R.string.unknown)
                    tvCompany.text = user.company?:resources.getString(R.string.unknown)
                    tvFollowers.text = user.followers.toString()
                    tvFollowing.text = user.following.toString()
                    tvRepository.text = user.public_repos.toString()
                }
                Glide.with(this)
                    .load(user.avatar_url)
                    .into(binding.imgAvatar)

                binding.btnVisit.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(user.html_url)))
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.language_setting) {
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showShimmer() {
        binding.layoutError.root.hide()
        binding.shimmerLayout.apply {
            startShimmer()
            show()
        }

        binding.apply {
            tvName.hide()
            tvCompany.hide()
            tvLocation.hide()
            tvFollowers.hide()
            tvFollowing.hide()
            tvRepository.hide()
            labelFollowers.hide()
            labelFollowing.hide()
            labelRepository.hide()
            btnVisit.hide()
        }
    }

    private fun hideShimmer() {
        binding.shimmerLayout.apply {
            hide()
            stopShimmer()
        }

        binding.apply {
            tvName.show()
            tvCompany.show()
            tvLocation.show()
            tvFollowers.show()
            tvFollowing.show()
            tvRepository.show()
            labelFollowers.show()
            labelFollowing.show()
            labelRepository.show()
            btnVisit.show()
        }
    }

    private fun addToFavorite() {
        binding.btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
        Toast.makeText(this, resources.getString(R.string.add_to_favorite), Toast.LENGTH_SHORT).show()
    }

    private fun removeFromFavorite() {
        binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border)
        Toast.makeText(this, resources.getString(R.string.remove_from_favorite), Toast.LENGTH_SHORT).show()
    }

}