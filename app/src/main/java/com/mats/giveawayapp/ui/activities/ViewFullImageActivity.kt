package com.mats.giveawayapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mats.giveawayapp.MyApplication
import com.mats.giveawayapp.databinding.ActivityViewFullImageBinding
import com.mats.giveawayapp.utils.Constants
import com.squareup.picasso.Picasso

class ViewFullImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewFullImageBinding
    private var onlineStatus: MyApplication = MyApplication()

    private var imageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewFullImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent = intent
        if (intent.hasExtra(Constants.EXTRA_IMAGE_URL))
        {
            imageUrl = intent.getStringExtra(Constants.EXTRA_IMAGE_URL)!!
        }

        Picasso.get().load(imageUrl).into(binding.imageViewer)
    }

    override fun onResume() {
        super.onResume()
        onlineStatus.onMoveToForeground()
    }

    override fun onPause() {
        super.onPause()
        onlineStatus.onMoveToBackground()
    }
}