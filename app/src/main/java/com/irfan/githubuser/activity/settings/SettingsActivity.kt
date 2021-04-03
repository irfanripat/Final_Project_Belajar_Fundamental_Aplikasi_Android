package com.irfan.githubuser.activity.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import com.irfan.githubuser.R
import com.irfan.githubuser.util.AlarmReceiver


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.setting, SettingsFragment())
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

    class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener, PreferenceManager.OnPreferenceTreeClickListener {

        private lateinit var alarmReceiver: AlarmReceiver
        private lateinit var sharedPreferences: SharedPreferences

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            sharedPreferences.registerOnSharedPreferenceChangeListener(this)
            alarmReceiver = AlarmReceiver()
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        private fun changeLanguage() {
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
        }

        override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {
            val isReminderActivated = preferences?.getBoolean("reminder", false)
            if (isReminderActivated == true) {
                if (activity != null) {
                    alarmReceiver.setRepeatingAlarm(this.requireContext())
                }
            } else {
                if (activity != null) {
                    alarmReceiver.cancelAlarm(this.requireContext())
                }
            }
        }

        override fun onPreferenceTreeClick(preference: Preference?): Boolean {
            if (preference?.key.equals("change_language")) {
                changeLanguage()
            }

            return super.onPreferenceTreeClick(preference)
        }
    }
}