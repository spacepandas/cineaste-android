package de.cineaste.android.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import de.cineaste.android.R
import de.cineaste.android.adapter.series.EpisodeAdapter
import de.cineaste.android.database.dbHelper.SeriesDbHelper
import de.cineaste.android.entity.series.Episode
import de.cineaste.android.viewholder.series.EpisodeViewHolder

class SeasonDetailFragment : Fragment(), EpisodeViewHolder.OnEpisodeWatchStateChangeListener, EpisodeViewHolder.OnDescriptionShowToggleListener {

    private var seriesId: Long = 0
    private var seasonId: Long = 0
    private lateinit var seriesDbHelper: SeriesDbHelper
    private var emptyListTextView: TextView? = null
    private lateinit var recyclerView: RecyclerView
    private val episodes: MutableList<Episode> = mutableListOf()

    override fun watchStateChanged(episode: Episode) {
        seriesDbHelper.episodeClicked(episode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments ?: return
        seasonId = args.getLong("seasonId", -1)
        seriesId = args.getLong("seriesId", -1)

        seriesDbHelper = SeriesDbHelper.getInstance(context!!)
        episodes.addAll(seriesDbHelper.getEpisodesBySeasonId(seasonId))

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val activity = activity ?: return null
        val view = inflater.inflate(R.layout.fragment_series_detail, container, false)

        emptyListTextView = view.findViewById(R.id.info_text)

        recyclerView = view.findViewById(R.id.episodeRecyclerView)

        if (episodes.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyListTextView!!.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyListTextView!!.visibility = View.GONE

            recyclerView.setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(activity)
            val adapter = EpisodeAdapter(episodes, this, this)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }

        return view
    }

    override fun showDescription(showDescription: ImageButton, hideDescription: ImageButton, description: TextView) {
        showDescription.visibility = View.INVISIBLE
        hideDescription.visibility = View.VISIBLE
        description.visibility = View.VISIBLE
    }

    override fun hideDescription(showDescription: ImageButton, hideDescription: ImageButton, description: TextView) {
        showDescription.visibility = View.VISIBLE
        hideDescription.visibility = View.INVISIBLE
        description.visibility = View.GONE
    }


}
