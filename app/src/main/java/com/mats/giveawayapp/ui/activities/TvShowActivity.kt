package com.mats.giveawayapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mats.giveawayapp.R
import com.mats.giveawayapp.databinding.ActivityTvShowBinding
import com.mats.giveawayapp.interfacejava.TvShowsListener
import com.mats.giveawayapp.models.TvShow
import com.mats.giveawayapp.ui.adapters.TvShowsAdapter

class TvShowActivity : AppCompatActivity(), TvShowsListener {
    private lateinit var binding: ActivityTvShowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTvShowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tvShowsRecyclerView: RecyclerView = binding.tvShowsRecyclerView

        val tvShows: ArrayList<TvShow> = ArrayList()

        val the100: TvShow = TvShow()
        the100.image = R.drawable.user
        the100.name = "The 100"
        the100.rating = 5f
        the100.createdBy = "Ich"
        the100.story = "Ich"
        tvShows.add(the100)

        val the10: TvShow = TvShow()
        the10.image = R.drawable.user
        the10.name = "The 100"
        the10.rating = 5f
        the10.createdBy = "Ich"
        the10.story = "Ich"
        tvShows.add(the10)

        val the11: TvShow = TvShow()
        the11.image = R.drawable.user
        the11.name = "The 100"
        the11.rating = 5f
        the11.createdBy = "Ich"
        the11.story = "Ich"
        tvShows.add(the11)

        val the1000: TvShow = TvShow()
        the1000.image = R.drawable.user
        the1000.name = "The 100"
        the1000.rating = 5f
        the1000.createdBy = "Ich"
        the1000.story = "Ich"
        tvShows.add(the1000)

        val tvShowsAdapter: TvShowsAdapter = TvShowsAdapter(tvShows, this)
        tvShowsRecyclerView.adapter = tvShowsAdapter

        binding.buttonAddToWatchlist.setOnClickListener {
            val selectedTvShows: List<TvShow> = tvShowsAdapter.selectedTvShows

        }
    }

    override fun onTvShowAction(isSelected: Boolean?) {
        if (isSelected!!) {
            binding.buttonAddToWatchlist.visibility = View.VISIBLE
        } else {
            binding.buttonAddToWatchlist.visibility = View.GONE
        }
    }
}