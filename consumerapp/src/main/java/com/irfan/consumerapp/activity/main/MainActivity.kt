package com.irfan.consumerapp.activity.main


import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.irfan.consumerapp.R
import com.irfan.consumerapp.`package`.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.irfan.consumerapp.activity.detail.DetailActivity
import com.irfan.consumerapp.activity.main.adapter.GithubAdapter
import com.irfan.consumerapp.activity.settings.SettingsActivity
import com.irfan.consumerapp.databinding.ActivityMainBinding
import com.irfan.consumerapp.model.DetailUser
import com.irfan.consumerapp.util.Commons.hide
import com.irfan.consumerapp.util.Commons.show
import com.irfan.consumerapp.util.MappingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        setUpActionBar()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.layoutNoResult.tvNoData.text = resources.getString(R.string.no_data)

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        val myObserver = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                showListFavoriteUser()
            }
        }

        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)

    }

    override fun onResume() {
        super.onResume()
        showListFavoriteUser()
    }

    private fun initializeBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setUpActionBar() {
        supportActionBar?.apply {
            title = resources.getString(R.string.app_name)
        }
    }

    private fun showListFavoriteUser() {
        GlobalScope.launch(Dispatchers.Main) {
            val deferredUser = async(Dispatchers.IO) {
                val cursor = contentResolver?.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }

            val user = deferredUser.await()
            val githubAdapter = GithubAdapter(user) {
                it as DetailUser
                showDetailUser(it)
            }

            if (user.isNullOrEmpty()) {
                binding.layoutNoResult.root.show()
            } else {
                binding.layoutNoResult.root.hide()
            }

            binding.recyclerView.adapter = githubAdapter

        }
    }

    private fun showDetailUser(user: DetailUser) {
        val intent = Intent(this@MainActivity, DetailActivity::class.java).apply {
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
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }


}