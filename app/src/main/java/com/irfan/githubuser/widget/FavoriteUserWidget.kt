package com.irfan.githubuser.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.irfan.githubuser.R
import com.irfan.githubuser.activity.detail.DetailActivity
import com.irfan.githubuser.db.UserDao
import com.irfan.githubuser.db.UserDatabase
import com.irfan.githubuser.model.DetailUser
import com.irfan.githubuser.util.MappingHelper.mapToObject

class FavoriteUserWidget : AppWidgetProvider() {

    companion object {
        private const val TOAST_ACTION = "com.irfan.githubuser.TOAST_ACTION"
        const val EXTRA_ITEM = "com.irfan.githubuser.EXTRA_ITEM"

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val intent = Intent(context, StackWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

            val views = RemoteViews(context.packageName, R.layout.favorite_user_widget)
            views.setRemoteAdapter(R.id.stack_view, intent)
            views.setEmptyView(R.id.stack_view, R.id.empty_view)

            val toastIntent = Intent(context, FavoriteUserWidget::class.java)
            toastIntent.action = TOAST_ACTION
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()
            val toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action != null) {
            if (intent.action == TOAST_ACTION) {
                val username = intent.getStringExtra(EXTRA_ITEM)
                val user = getDataFromDb(context!!, username!!)
                val i = Intent(context, DetailActivity::class.java)
                i.putExtra(DetailActivity.EXTRA_GITHUB_USER, user)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(i)
            }
        }
    }

    private fun getDataFromDb(context: Context, username: String) : DetailUser {
        val database : UserDatabase = UserDatabase.getInstance(context)
        val dao : UserDao = database.userDao()

        val cursor = dao.getUserByUsername(username)
        return cursor.mapToObject()?: DetailUser()
    }
}