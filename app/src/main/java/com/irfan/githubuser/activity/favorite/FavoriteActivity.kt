package com.irfan.githubuser.activity.favorite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.irfan.githubuser.R
import com.irfan.githubuser.activity.detail.DetailActivity
import com.irfan.githubuser.activity.main.adapter.GithubAdapter
import com.irfan.githubuser.databinding.ActivityFavoriteBinding
import com.irfan.githubuser.db.UserDao
import com.irfan.githubuser.db.UserDatabase
import com.irfan.githubuser.model.DetailUser
import com.irfan.githubuser.util.Commons.hide
import com.irfan.githubuser.util.Commons.show

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFavoriteBinding
    private lateinit var dao : UserDao
    private lateinit var database : UserDatabase
    private lateinit var user: List<DetailUser>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        binding.layoutNoData.tvNoData.text = resources.getString(R.string.no_data)
        setContentView(binding.root)
        setUpActionBar()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        initDatabaseInstance()
    }

    override fun onResume() {
        super.onResume()
        getListFavoriteUser()
        showListFavoriteUser()
    }

    private fun setUpActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = resources.getString(R.string.favorite_activity_title)
        }
    }

    private fun showListFavoriteUser() {
        val githubAdapter = GithubAdapter(user.toMutableList()) {user->
            user as DetailUser
            showDetailUser(user)
        }

        if (user.isNullOrEmpty()) {
            binding.layoutNoData.root.show()
        } else {
            binding.layoutNoData.root.hide()
        }

        binding.recyclerView.adapter = githubAdapter
    }

    private fun getListFavoriteUser() {
        user = dao.getAllUser()
    }

    private fun showDetailUser(user: DetailUser) {
        val intent = Intent(this@FavoriteActivity, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_GITHUB_USER, user)
        }
        startActivity(intent)
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
        when(item.itemId) {
            R.id.settings -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initDatabaseInstance() {
        database = UserDatabase.getInstance(applicationContext)
        dao = database.userDao()
    }


}