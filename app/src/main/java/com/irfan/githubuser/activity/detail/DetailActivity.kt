package com.irfan.githubuser.activity.detail

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.irfan.githubuser.R
import com.irfan.githubuser.activity.detail.adapter.FragmentAdapter
import com.irfan.githubuser.databinding.ActivityDetailBinding
import com.irfan.githubuser.db.UserDao
import com.irfan.githubuser.db.UserDatabase
import com.irfan.githubuser.model.DetailUser
import com.irfan.githubuser.util.Commons.hide
import com.irfan.githubuser.util.Commons.hideViews
import com.irfan.githubuser.util.Commons.show
import com.irfan.githubuser.util.Commons.showToast
import com.irfan.githubuser.util.Commons.showViews

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    private var isFavorite: Boolean = false
    private lateinit var dao : UserDao
    private lateinit var database : UserDatabase
    private var user : DetailUser? = null
    private var username = ""

    companion object {
        const val EXTRA_GITHUB_USER = "github"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        binding.layoutError.textError.setTextColor(Color.WHITE)
        setContentView(binding.root)
        detailViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(DetailViewModel::class.java)
        username = intent.getStringExtra(EXTRA_GITHUB_USER)?:""

        initDatabaseInstance()
        checkIfUserHasAddedToFavorite(username)
        setUpActionBar(username)
        setUpTabLayout()
        getData(username)
        observeIfGetDataHasFinished()

        binding.layoutError.btnRefresh.setOnClickListener(this)
        binding.btnFavorite.setOnClickListener(this)
    }

    private fun getData(username: String) {
        showShimmer()
        detailViewModel.getDetailUser(username).observe(this, { user ->
            this.user = user

            hideShimmer()
            if (user != null) {
                binding.apply {
                    tvName.text = user.name
                    tvLocation.text = user.location ?: resources.getString(R.string.unknown)
                    tvCompany.text = user.company ?: resources.getString(R.string.unknown)
                    tvFollowers.text = user.followers.toString()
                    tvFollowing.text = user.following.toString()
                    tvRepository.text = user.public_repos.toString()
                    Glide.with(this@DetailActivity)
                        .load(user.avatar_url)
                        .into(imgAvatar)
                }

                binding.btnVisit.setOnClickListener{
                    visitGithubAccount(user.html_url?:"")
                }
            }
        })
    }

    private fun observeIfGetDataHasFinished() {
        detailViewModel.isSuccess.observe(this, {isSuccess->
            when(isSuccess) {
                0 -> {
                    binding.layoutError.root.show()
                    binding.shimmerLayout.hide()
                }
            }
        })
    }

    private fun showShimmer() {
        binding.layoutError.root.hide()
        binding.shimmerLayout.apply {
            startShimmer()
            show()
        }

        hideViews(
            binding.tvName,
            binding.tvCompany,
            binding.tvLocation,
            binding.tvFollowers,
            binding.tvFollowing,
            binding.tvRepository,
            binding.labelFollowers,
            binding.labelFollowing,
            binding.labelRepository,
            binding.btnVisit
        )
    }

    private fun hideShimmer() {
        binding.shimmerLayout.apply {
            hide()
            stopShimmer()
        }

        showViews(
            binding.tvName,
            binding.tvCompany,
            binding.tvLocation,
            binding.tvFollowers,
            binding.tvFollowing,
            binding.tvRepository,
            binding.labelFollowers,
            binding.labelFollowing,
            binding.labelRepository,
            binding.btnVisit
        )
    }

    private fun addToFavorite(user: DetailUser) {
        dao.insert(user)
        binding.btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
        this.showToast(resources.getString(R.string.add_to_favorite))
    }

    private fun removeFromFavorite(user: DetailUser) {
        dao.delete(user)
        binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border)
        this.showToast(resources.getString(R.string.remove_from_favorite))
    }

    private fun checkIfUserHasAddedToFavorite(username: String) {
        isFavorite = (dao.getUserByUsername(username) != null)
        when(isFavorite) {
            true -> binding.btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
            false -> binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    private fun initDatabaseInstance() {
        database = UserDatabase.getInstance(applicationContext)
        dao = database.userDao()
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

    private fun setUpActionBar(username: String) {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.apply {
            setTitleTextColor(Color.WHITE)
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = username
        }
    }

    private fun setUpTabLayout() {
        val adapter = FragmentAdapter(supportFragmentManager, tabLayout.tabCount)
        tabLayout = binding.content.tabLayout
        viewPager = binding.content.viewPager

        tabLayout.apply {
            addTab(this.newTab().setText(resources.getString(R.string.followers)))
            addTab(this.newTab().setText(resources.getString(R.string.following)))
            tabGravity = TabLayout.GRAVITY_FILL
        }

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
    }

    private fun setToFavorite() {
        if (user != null) {
            isFavorite = !isFavorite
            when(isFavorite) {
                true -> addToFavorite(user?: DetailUser())
                else -> removeFromFavorite(user?: DetailUser())
            }
        } else {
            this.showToast("Belum ada data bosku")
        }
    }

    override fun onClick(view: View?) {
        when(view) {
            binding.layoutError.btnRefresh -> getData(username)
            binding.btnFavorite -> setToFavorite()
        }
    }

    private fun visitGithubAccount(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}