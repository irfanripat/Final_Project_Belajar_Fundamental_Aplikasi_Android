package com.irfan.githubuser.activity.main


import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.irfan.githubuser.R
import com.irfan.githubuser.activity.detail.DetailActivity
import com.irfan.githubuser.activity.favorite.FavoriteActivity
import com.irfan.githubuser.activity.main.adapter.GithubAdapter
import com.irfan.githubuser.databinding.ActivityMainBinding
import com.irfan.githubuser.model.DetailUser
import com.irfan.githubuser.util.Commons.hide
import com.irfan.githubuser.util.Commons.show


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.inputUsername.setOnEditorActionListener { _, p1, _ ->
            if (p1 == EditorInfo.IME_ACTION_DONE) {
                if (binding.inputUsername.text.isNullOrEmpty()) {
                    binding.inputUsername.error = resources.getString(R.string.type_something)
                } else {
                    showShimmer()
                    mainViewModel.setSearchQuery(binding.inputUsername.text.toString())
                }
            }
            true
        }

        mainViewModel.isSuccess.observe(this, {isSucces->
            when(isSucces) {
                0 -> {
                    binding.shimmerLayout.hide()
                    binding.layoutError.root.show()
                }
                1 -> {
                    binding.layoutError.root.hide()
                    getData()
                }
            }
        })

        binding.layoutError.btnRefresh.setOnClickListener {
            showShimmer()
            mainViewModel.setSearchQuery(binding.inputUsername.text.toString())
        }
    }

    private fun getData() {
        mainViewModel.getSearchResult().observe(this, {
            val githubAdapter = GithubAdapter(it) { user ->
                user as DetailUser
                showDetailUser(user.login!!)
            }

            hideShimmer()

            if (it.isNullOrEmpty()) {
                binding.layoutNoData.root.show()
            } else {
                binding.layoutNoData.root.hide()
                githubAdapter.notifyDataSetChanged()
            }

            binding.recyclerView.adapter = githubAdapter
        })
    }


    private fun showShimmer() {
        binding.apply {
            layoutNoData.root.hide()
            layoutError.root.hide()
            recyclerView.hide()
            shimmerLayout.apply {
                show()
                startShimmer()
            }
        }
    }

    private fun hideShimmer() {
        binding.apply {
            shimmerLayout.apply {
                stopShimmer()
                hide()
            }
            recyclerView.show()
        }
    }

    private fun showDetailUser(username: String) {
        val intent = Intent(this@MainActivity, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_GITHUB_USER, username)
        }
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.language_setting -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
            }
            R.id.favorite -> {
                moveToFavoriteActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu?.findItem(R.id.favorite) != null) {
            menu.findItem(R.id.favorite).isVisible = true
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun moveToFavoriteActivity() {
        startActivity(Intent(this@MainActivity, FavoriteActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        binding.inputUsername.hint = resources.getString(R.string.search)
    }
}