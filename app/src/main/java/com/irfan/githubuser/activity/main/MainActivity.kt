package com.irfan.githubuser.activity.main


import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.irfan.githubuser.R
import com.irfan.githubuser.activity.detail.DetailActivity
import com.irfan.githubuser.activity.favorite.FavoriteActivity
import com.irfan.githubuser.activity.main.adapter.GithubAdapter
import com.irfan.githubuser.activity.settings.SettingsActivity
import com.irfan.githubuser.databinding.ActivityMainBinding
import com.irfan.githubuser.model.DetailUser
import com.irfan.githubuser.util.Commons.hide
import com.irfan.githubuser.util.Commons.show


class MainActivity : AppCompatActivity(), TextView.OnEditorActionListener, View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        updateIfGetDataHasFinished()

        binding.inputUsername.setOnEditorActionListener(this)
        binding.layoutError.btnRefresh.setOnClickListener(this)
    }

    private fun initializeBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun getData() {
        mainViewModel.getSearchResult().observe(this, {
            val githubAdapter = GithubAdapter(it) { user ->
                user as DetailUser
                showDetailUser(user)
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

    private fun updateIfGetDataHasFinished() {
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

    private fun showDetailUser(user: DetailUser) {
        val intent = Intent(this@MainActivity, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_GITHUB_USER, user)
        }
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
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

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (binding.inputUsername.text.isNullOrEmpty()) {
                binding.inputUsername.error = resources.getString(R.string.type_something)
            } else {
                showShimmer()
                mainViewModel.setSearchQuery(binding.inputUsername.text.toString())
            }
        }
        return true
    }

    override fun onClick(view: View?) {
        when(view) {
            binding.layoutError.btnRefresh -> {
                showShimmer()
                mainViewModel.setSearchQuery(binding.inputUsername.text.toString())
            }
        }
    }
}