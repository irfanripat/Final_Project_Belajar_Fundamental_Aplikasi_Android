package com.irfan.githubuser.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.irfan.githubuser.R
import com.irfan.githubuser.db.UserDao
import com.irfan.githubuser.db.UserDatabase
import com.irfan.githubuser.model.DetailUser
import com.irfan.githubuser.util.MappingHelper

internal class StackRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private lateinit var dao : UserDao
    private lateinit var database : UserDatabase
    private lateinit var user: List<DetailUser>

    override fun onCreate() {
        database = UserDatabase.getInstance(context)
        dao = database.userDao()
        user = MappingHelper.mapCursorToArrayList(dao.getAllUser())
    }

    override fun onDataSetChanged() {
        user = MappingHelper.mapCursorToArrayList(dao.getAllUser())
    }

    override fun onDestroy() {
        if (database.isOpen) {
            database.close()
        }
    }

    override fun getCount(): Int = user.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_item)
        try {
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(user[position].avatar_url)
                .submit(512, 512)
                .get()

            rv.setImageViewBitmap(R.id.imageView, bitmap)
            rv.setTextViewText(R.id.textView, user[position].login)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val extras = bundleOf(
            FavoriteUserWidget.EXTRA_ITEM to user[position].login
        )

        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}