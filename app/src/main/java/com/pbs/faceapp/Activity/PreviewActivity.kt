package com.pbs.faceapp.Activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.pbs.faceapp.Modal.FaceModal
import com.pbs.faceapp.R
import com.pbs.faceapp.adapter.PreviewList
import com.pbs.faceapp.databinding.ActivityPreviewBinding

class PreviewActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityPreviewBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val faceList = intent.extras?.getParcelableArrayList<FaceModal>("faceList") ?: emptyList()

        val previewListAdapter = PreviewList(faceList)

        binding.previewList.layoutManager=LinearLayoutManager(this)
        binding.previewList.adapter = previewListAdapter
    }
}