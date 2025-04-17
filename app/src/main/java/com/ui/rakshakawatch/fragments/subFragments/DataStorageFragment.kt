package com.ui.rakshakawatch.fragments.subFragments

import SettingFragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ui.rakshakawatch.R
import java.io.File
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

class DataStorageFragment : Fragment() {

        private lateinit var switchBackgroundData: Switch
        private lateinit var switchAutoUpload: Switch
        private lateinit var switchCompressMedia: Switch
        private lateinit var btnClearCache: Button
        private lateinit var btnResetSettings: Button
        private lateinit var btnClearAllData: Button
        private lateinit var textCacheSize: TextView
        private lateinit var backArrow: ImageView

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_data_storage, container, false)

            val bottomNav = activity?.findViewById<View>(R.id.bottomNavigation)
            bottomNav?.visibility = View.GONE
            backArrow = view.findViewById(R.id.backArrow)
            backArrow.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container,SettingFragment())
                    .addToBackStack(null).commit()
            }
            // Initialize views
            switchBackgroundData = view.findViewById(R.id.switch_background_data)
            switchAutoUpload = view.findViewById(R.id.switch_auto_upload)
            switchCompressMedia = view.findViewById(R.id.switch_compress_media)
            btnClearCache = view.findViewById(R.id.btn_clear_cache)
            btnResetSettings = view.findViewById(R.id.btn_reset_settings)
            btnClearAllData = view.findViewById(R.id.btn_clear_all_data)
            textCacheSize = view.findViewById(R.id.text_cache_size)

            loadPreferences()
            showCacheSize()

            switchBackgroundData.setOnCheckedChangeListener { _, isChecked ->
                saveBooleanPreference("background_data", isChecked)
            }

            switchAutoUpload.setOnCheckedChangeListener { _, isChecked ->
                saveBooleanPreference("auto_upload", isChecked)
            }

            switchCompressMedia.setOnCheckedChangeListener { _, isChecked ->
                saveBooleanPreference("compress_media", isChecked)
            }

            btnClearCache.setOnClickListener {
                clearAppCache()
                showCacheSize()
            }

            btnResetSettings.setOnClickListener {
                resetSettings()
            }

            btnClearAllData.setOnClickListener {
                clearAppData()
            }

            return view
        }

        private fun loadPreferences() {
            val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
            switchBackgroundData.isChecked = prefs.getBoolean("background_data", false)
            switchAutoUpload.isChecked = prefs.getBoolean("auto_upload", false)
            switchCompressMedia.isChecked = prefs.getBoolean("compress_media", false)
        }

        private fun saveBooleanPreference(key: String, value: Boolean) {
            val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
            prefs.edit().putBoolean(key, value).apply()
        }

        private fun resetSettings() {
            val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            Toast.makeText(context, "Settings Reset", Toast.LENGTH_SHORT).show()
            loadPreferences()
        }

        private fun clearAppCache() {
            try {
                val dir = requireContext().cacheDir
                deleteDir(dir)
                Toast.makeText(context, "Cache Cleared", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun clearAppData() {
            try {
                val packageName = requireContext().packageName
                val runtime = Runtime.getRuntime()
                runtime.exec("pm clear $packageName")
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error clearing app data", Toast.LENGTH_SHORT).show()
            }
        }

        private fun deleteDir(dir: File?): Boolean {
            if (dir != null && dir.isDirectory) {
                val children = dir.list()
                if (children != null) {
                    for (child in children) {
                        val success = deleteDir(File(dir, child))
                        if (!success) {
                            return false
                        }
                    }
                }
            }
            return dir?.delete() ?: false
        }

        private fun showCacheSize() {
            val cacheSize = getDirSize(requireContext().cacheDir)
            textCacheSize.text = "App cache: ${readableFileSize(cacheSize)}"
        }

        private fun getDirSize(dir: File): Long {
            var size = 0L
            if (dir.isDirectory) {
                dir.listFiles()?.forEach {
                    size += if (it.isDirectory) getDirSize(it) else it.length()
                }
            } else {
                size = dir.length()
            }
            return size
        }

        private fun readableFileSize(size: Long): String {
            if (size <= 0) return "0 B"
            val units = arrayOf("B", "KB", "MB", "GB", "TB")
            val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
            return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
        }
    }
