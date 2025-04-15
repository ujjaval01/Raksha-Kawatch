package com.ui.rakshakawatch.fragments.subFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.ui.rakshakawatch.R

class DataStorageFragment : Fragment() {

    private lateinit var storageProgress: ProgressBar
    private lateinit var usedSpaceText: TextView
    private lateinit var totalSpaceText: TextView
    private lateinit var mediaSpaceText: TextView
    private lateinit var documentsSpaceText: TextView
    private lateinit var appDataSpaceText: TextView
    private lateinit var clearCacheBtn: MaterialButton
    private lateinit var optimizeStorageBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_data_storage, container, false)


        return view

    }
}